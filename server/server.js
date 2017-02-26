var express = require('express');
var app = express();

var server = require('http').Server(app);
var io = require('socket.io')(server);

var socketSessionMap = new Map();
var sessionSocketMap = new Map();

var sessionUserMap = new Map();

var users = {
	'tim': {
		'username': 'tim',
		'password': 'hunter2',
		'name': 'Timothy J. Aveni',
		'session': null
	}
};

io.on('connection', function (socket) {
	registerEvents(socket);
});

var registerEvents = function (socket) {
	socket.on('bind session', function (data) {
		if (data.session_id !== undefined) {
			sessionSocketMap.set(data.session_id, socket);
			socketSessionMap.set(socket, data.session_id);

			sendUpdate(socket);
		}
	});

	socket.on('session update', function (data) {
		sendUpdate(socket);
	});

	socket.on('attach', function (data) {
		if (!socketSessionMap.has(socket)) {
			socket.emit('attach failure', {
				'error': 'This socket is not bound to a session.'
			});
			return;
		}

		var username = data.username;
		var password = data.password;
		
		var session_id = socketSessionMap.get(socket);
		if (sessionUserMap.has(session_id)) {
			socket.emit('attach failure', {
				'error': 'This session already has an attached user.'
			});
			return;
		}

		if (!users.hasOwnProperty(username)) {
			socket.emit('attach failure', {
				'error': 'That username does not exist.'
			});
			return;
		}

		if (users[username].password !== password) {
			socket.emit('attach failure', {
				'error': 'The passwords did not match.'
			});
			return;
		}

		if (users[username].session !== null && sessionSocketMap.has(users[username].session)) {
			sessionSocketMap.get(users[username].session).emit('detach success');
		}

		sessionUserMap.set(socketSessionMap.get(socket), users[username]);
		users[username].session = socketSessionMap.get(socket);

		socket.emit('attach success', {
			user: users[username]
		});
	});

	socket.on('register and attach', function (data) {
		if (!socketSessionMap.has(socket)) {
			socket.emit('attach failure', {
				'error': 'This socket is not bound to a session.'
			});
			return;
		}

		var username = data.username;
		var password = data.password;
		var name = data.name;
		
		var session_id = socketSessionMap.get(socket);
		if (sessionUserMap.has(session_id)) {
			socket.emit('attach failure', {
				'error': 'This session already has an attached user.'
			});
			return;
		}

		if (users.hasOwnProperty(username)) {
			socket.emit('attach failure', {
				'error': 'That username is already in use.'
			});
			return;
		}

		users[username] = {
			'username': username,
			'password': password,
			'name': name,
			'session': session_id
		}

		sessionUserMap.set(session_id, users[username]);

		socket.emit('attach success', {
			user: users[username]
		});
	});

	socket.on('detach', function () {
		if (!socketSessionMap.has(socket)) {
			socket.emit('detach failure', {
				'error': 'This socket is not bound to a session.'
			});
			return;
		}

		if (!sessionUserMap.has(socketSessionMap.get(socket))) {
			socket.emit('detach failure', {
				'error': 'This session does not have an attached user.'
			});
			return;
		}

		var session_id = socketSessionMap.get(socket);

		sessionUserMap.get(session_id).session = null;
		sessionUserMap.delete(session_id);

		socket.emit('detach success');
	});

	socket.on('disconnect', function () {
		if (socketSessionMap.has(socket)) {
			var session_id = socketSessionMap.get(socket);

			sessionSocketMap.delete(socketSessionMap.get(socket));
			socketSessionMap.delete(socket);
		}
	});
};

var sendUpdate = function (socket) {
	if (socketSessionMap.has(socket)) {
		var session = socketSessionMap.get(socket);
		socket.emit('session update', {
			'user': sessionUserMap.get(session) || null
		});
	}
};

server.listen(2340, function () {
	console.log('Server listening on port 2340 :)');
});
