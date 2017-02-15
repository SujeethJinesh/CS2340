package edu.gatech.waterreports_teamvictorioussecret;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class WelcomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        Button login = (Button) findViewById(R.id.loginButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new LoginDialogFragment(WelcomeScreen.this);
                newFragment.show(getFragmentManager(), "login");
            }
        });
    }

    private void tryLogin(String username, String password) {
        if (username.equals("tim") && password.equals("hunter2")) {
            Intent intent = new Intent(this, MainScreen.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Invalid login", Toast.LENGTH_SHORT).show();
        }
    }

    private class LoginDialogFragment extends DialogFragment {

        WelcomeScreen w;

        public LoginDialogFragment(WelcomeScreen w) {
            super();
            this.w = w;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(inflater.inflate(R.layout.sign_in_dialog, null))
                    // Add action buttons
                    .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            EditText usernameEditText = (EditText) findViewById(R.id.username);
                            EditText passwordEditText = (EditText) findViewById(R.id.password);

                            w.tryLogin(usernameEditText.getText().toString(), passwordEditText.getText().toString());
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            LoginDialogFragment.this.getDialog().cancel();
                        }
                    });
            return builder.create();
        }
    }
}
