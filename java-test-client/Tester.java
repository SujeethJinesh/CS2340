public class Tester {

	public static void main(String[] args) {
		Session session = new SocketIOSession();
		System.out.println(session.getSessionId());
		session.addUpdateListener(new Session.UpdateListener() {
			@Override
			public boolean onUpdate() {
				System.out.println("update: ");
				System.out.println(session.getLoggedInUser());
				
				session.login("tim", "hunter2");
				return true;
			}
		});
		
		session.addAttachListener(new Session.AttachListener() {
			@Override
			public boolean onAttachSuccess() {
				System.out.println("attach success: ");
				System.out.println(session.getLoggedInUser());
				return true;
			}
			
			@Override
			public boolean onAttachFailure(String error) {
				System.out.println("attach failure: " + error);
				return true;
			}
		});
		
		session.addDetachListener(new Session.DetachListener() {
			@Override
			public boolean onDetachSuccess() {
				System.out.println("detached");
				return true;
			}
			
			@Override
			public boolean onDetachFailure(String error) {
				System.out.println("detach failure: " + error);
				return true;
			}
		});
		
		while (true){}
	}
}
