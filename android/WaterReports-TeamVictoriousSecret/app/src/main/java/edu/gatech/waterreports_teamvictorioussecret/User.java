package edu.gatech.waterreports_teamvictorioussecret;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    String userEmail;
    String realName;
    WorkerType type;

    public User(JSONObject obj) {
        try {
            this.userEmail = obj.getString("useremail");
            this.realName = obj.getString("name");
            this.type = WorkerType.createFromString(obj.getString("workerType"));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return this.realName + " (" + this.userEmail + ")" + " (" + this.type + ")";
    }
}