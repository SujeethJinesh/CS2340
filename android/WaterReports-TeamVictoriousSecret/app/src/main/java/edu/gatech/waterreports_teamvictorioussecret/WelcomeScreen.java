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

        SocketIOSession.getInstance().addAttachListener(new Session.AttachListener() {
            @Override
            public boolean onAttachSuccess() {
                Intent intent = new Intent(WelcomeScreen.this, MainScreen.class);
                WelcomeScreen.this.startActivity(intent);
                return true;
            }

            @Override
            public boolean onAttachFailure(String error) {
                Toast.makeText(WelcomeScreen.this, error, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    private void tryLogin(String username, String password) {
        SocketIOSession.getInstance().login(username, password);
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
            final LayoutInflater inflater = getActivity().getLayoutInflater();

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            final View view = inflater.inflate(R.layout.sign_in_dialog, null);
            builder.setView(view)
                    // Add action buttons
                    .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            EditText usernameEditText = (EditText) view.findViewById(R.id.username);
                            EditText passwordEditText = (EditText) view.findViewById(R.id.password);

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
