import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketIOSession implements Session {
	private Socket socket;
	private String sessionId;
	private User user;
	
	private int _lastListenerId = -1;
	private Map<Integer, AttachListener> attachListeners;
	private Map<Integer, DetachListener> detachListeners;
	private Map<Integer, UpdateListener> updateListeners;
	
	public SocketIOSession() {
		this(UUID.randomUUID().toString());
	}

	public SocketIOSession(String uuid) {
		try {
			this.socket = IO.socket("https://water-2340.syntaxblitz.net");
			this.sessionId = uuid;
			this.user = null;
			
			this.attachListeners = new HashMap<Integer, AttachListener>();
			this.detachListeners = new HashMap<Integer, DetachListener>();
			this.updateListeners = new HashMap<Integer, UpdateListener>();
			
			this.registerEvents();
			
			this.socket.connect();
		} catch (URISyntaxException e) {
			// it's literally hard-coded
			// what do you want from me
		}
	}

	@Override
	public String getSessionId() {
		return this.sessionId;
	}

	@Override
	public boolean isLoggedIn() {
		return this.user != null;
	}

	@Override
	public User getLoggedInUser() {
		return this.user;
	}

	@Override
	public int addAttachListener(AttachListener listener) {
		int listenerId = ++_lastListenerId;
		attachListeners.put(listenerId, listener);
		return listenerId;
	}

	@Override
	public int addDetachListener(DetachListener listener) {
		int listenerId = ++_lastListenerId;
		detachListeners.put(listenerId, listener);
		return listenerId;
	}
	
	@Override
	public int addUpdateListener(UpdateListener listener) {
		int listenerId = ++_lastListenerId;
		updateListeners.put(listenerId, listener);
		return listenerId;
	}

	@Override
	public boolean removeListener(int listenerId) {
		if (attachListeners.containsKey(listenerId)) {
			attachListeners.remove(listenerId);
			return true;
		}
		
		if (detachListeners.containsKey(listenerId)) {
			detachListeners.remove(listenerId);
			return true;
		}
		
		return false;
	}

	@Override
	public void login(String username, String password) {
		JSONObject request = new JSONObject();
		try {
			request.put("username", username);
			request.put("password", password);
			
			this.socket.emit("attach", request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void register(String username, String password, String name) {
		JSONObject request = new JSONObject();
		try {
			request.put("username", username);
			request.put("password", password);
			request.put("name", name);
			
			this.socket.emit("register and attach", request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void logout() {
		this.socket.emit("detach");
	}
	
	private void registerEvents() {
		this.socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject response = new JSONObject();
				try {
					response.put("session_id", SocketIOSession.this.sessionId);
					
					SocketIOSession.this.socket.emit("bind session", response);
				} catch (JSONException e) {
					// seriously what do you want from me
					e.printStackTrace();
				}
			}
		}).on("session update", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {
					if (data.isNull("user")) {
						SocketIOSession.this.user = null;
					} else {
						SocketIOSession.this.user = new User(data.getJSONObject("user"));	
					}
					
					for (UpdateListener listener : SocketIOSession.this.updateListeners.values()) {
						if (!listener.onUpdate()) {
							break;
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}).on("attach success", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {
					if (data.isNull("user")) {
						SocketIOSession.this.user = null;
					} else {
						SocketIOSession.this.user = new User(data.getJSONObject("user"));	
					}
					
					for (AttachListener listener : SocketIOSession.this.attachListeners.values()) {
						if (!listener.onAttachSuccess()) {
							break;
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}).on("attach failure", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {
					String error = data.getString("error");
					
					for (AttachListener listener : SocketIOSession.this.attachListeners.values()) {
						if (!listener.onAttachFailure(error)) {
							break;
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}).on("detach success", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				for (DetachListener listener : SocketIOSession.this.detachListeners.values()) {
					if (!listener.onDetachSuccess()) {
						break;
					}
				}
			}
		}).on("detach failure", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {
					String error = data.getString("error");
					
					for (DetachListener listener : SocketIOSession.this.detachListeners.values()) {
						if (!listener.onDetachFailure(error)) {
							break;
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
