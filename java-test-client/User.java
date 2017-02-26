import org.json.JSONException;
import org.json.JSONObject;

public class User {
	String username;
	String realName;
	
	public User(JSONObject obj) {
		try {
			this.username = obj.getString("username");
			this.realName = obj.getString("name");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString() {
		return realName + " (" + username + ")";
	}
}
