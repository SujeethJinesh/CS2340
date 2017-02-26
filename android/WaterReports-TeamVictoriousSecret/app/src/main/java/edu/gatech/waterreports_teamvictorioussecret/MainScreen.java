package edu.gatech.waterreports_teamvictorioussecret;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        Button logoutButton = (Button) findViewById(R.id.logout);

        SocketIOSession.getInstance().addDetachListener(new Session.DetachListener() {
            @Override
            public boolean onDetachSuccess() {
                Intent intent = new Intent(MainScreen.this, WelcomeScreen.class);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onDetachFailure(String error) {
                return false;
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocketIOSession.getInstance().logout();
            }
        });
    }

}
