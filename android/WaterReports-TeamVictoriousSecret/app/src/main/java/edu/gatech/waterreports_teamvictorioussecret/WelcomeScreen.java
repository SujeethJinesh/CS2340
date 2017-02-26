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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

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

        Button register = (Button) findViewById(R.id.registerButton);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment fragment = new RegisterDialogFragment(WelcomeScreen.this);
                fragment.show(getFragmentManager(), "register");
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
//                Toast.makeText(WelcomeScreen.this, error, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    private void tryLogin(String email, String password) {
        SocketIOSession.getInstance().login(email, password);
    }

    private void tryRegister(String email, String password, WorkerType type) {
        SocketIOSession.getInstance().register(email, password, type);
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

    private class RegisterDialogFragment extends DialogFragment {
        WelcomeScreen screen;

        RegisterDialogFragment(WelcomeScreen screen) {
            super();
            this.screen = screen;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            final LayoutInflater inflater = getActivity().getLayoutInflater();

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            final View view = inflater.inflate(R.layout.dialog_register, null);
            Spinner spinner = (Spinner) view.findViewById(R.id.dialog_register_spinner);
            ArrayAdapter<WorkerType> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, WorkerType.values());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            builder.setView(view)
                    // Add action buttons
                    .setPositiveButton(R.string.register, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            EditText emailEditText = (EditText) view.findViewById(R.id.dialog_register_email);
                            EditText passwordEditText = (EditText) view.findViewById(R.id.dialog_register_password);
                            EditText passwordConfirmationEditText = (EditText) view.findViewById(R.id.dialog_register_password_confirmation);
                            Spinner workerSpinner = (Spinner) view.findViewById(R.id.dialog_register_spinner);
                            WorkerType type = (WorkerType) workerSpinner.getSelectedItem();
                            String email = emailEditText.getText().toString();
                            String password = passwordEditText.getText().toString();
                            String passwordConfirmation = passwordConfirmationEditText.getText().toString();

                            if (!password.equals(passwordConfirmation)) {
//                                Toast.makeText(getContext(), "Passwords don't match", Toast.LENGTH_SHORT).show();
                                RegisterDialogFragment.this.getDialog().cancel();
                            }

                            screen.tryRegister(email, password, type);
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            RegisterDialogFragment.this.getDialog().cancel();
                        }
                    });
            return builder.create();
        }
    }
}
