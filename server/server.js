var express = require('express');
var app = express();

var server = require('http').Server(app);
var io = require('socket.io')(server);

var socketSessionMap = new Map();
var sessionSocketMap = new Map();

var sessionUserMap = new Map();

var users = {
	'timothy@tacosareawesome.com': {
		'email': 'timothy@tacosareawesome.com',
		'password': 'hunter2',
		'workerType': 'ADMIN',
		'session': null
	}
};

var profiles = {
	'timothy@tacosareawesome.com': {
		'address': '700 Techwood Drive',
		'name': 'Timothy J. Aveni',
		'phone': '(404) 555-1123',
		'password': 'hunter2'
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

		var email = data.email;
		var password = data.password;
		
		var session_id = socketSessionMap.get(socket);
		if (sessionUserMap.has(session_id)) {
			socket.emit('attach failure', {
				'error': 'This session already has an attached user.'
			});
			return;
		}

		if (!users.hasOwnProperty(email)) {
			socket.emit('attach failure', {
				'error': 'That email does not exist.'
			});
			return;
		}

		if (profiles[email].password !== password) {
			socket.emit('attach failure', {
				'error': 'The passwords did not match.'
			});
			return;
		}

		if (users[email].session !== null && sessionSocketMap.has(users[email].session)) {
			sessionSocketMap.get(users[email].session).emit('detach success');
		}

		sessionUserMap.set(socketSessionMap.get(socket), users[email]);
		users[email].session = socketSessionMap.get(socket);

		socket.emit('attach success', {
			user: users[email]
		});
	});

	socket.on('register and attach', function (data) {
		if (!socketSessionMap.has(socket)) {
			socket.emit('attach failure', {
				'error': 'This socket is not bound to a session.'
			});
			return;
		}

		var email = data.email;
		var password = data.password;
		var workerType = data.workerType;

		var session_id = socketSessionMap.get(socket);
		if (sessionUserMap.has(session_id)) {
			socket.emit('attach failure', {
				'error': 'This session already has an attached user.'
			});
			return;
		}

		if (users.hasOwnProperty(email)) {
			socket.emit('attach failure', {
				'error': 'That email is already in use.'
			});
			return;
		}

		users[email] = {
			'email': email,
			'workerType': workerType,
			'session': session_id
		};

		profiles[email] = {
			'email': email,
			'address': '',
			'phone': '',
			'password': password
		}

		sessionUserMap.set(session_id, users[email]);

		socket.emit('attach success', {
			user: users[email]
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

	socket.on('get profile', function () {
		if (socketSessionMap.has(socket)) {
			var session_id = socketSessionMap.get(socket);
			if (sessionUserMap.has(session_id)) {
				var user = sessionUserMap.get(session_id);
				var profile = profiles[user.email];

				socket.emit('profile update', profile);
			}
		}
	});

	socket.on('update profile', function (data) {
		if (socketSessionMap.has(socket)) {
			var session_id = socketSessionMap.get(socket);
			if (sessionUserMap.has(session_id)) {
				var user = sessionUserMap.get(session_id);
				profiles[user.email] = data;
			}
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
