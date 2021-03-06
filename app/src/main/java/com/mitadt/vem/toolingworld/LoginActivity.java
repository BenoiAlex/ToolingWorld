package com.mitadt.vem.toolingworld;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.android.core.auth.providers.userpassword.UserPasswordAuthProviderClient;
import com.mongodb.stitch.core.auth.providers.userpassword.UserPasswordCredential;

public class LoginActivity extends AppCompatActivity {

    public static StitchAppClient client;
    private EditText emailView;
    private EditText passwordView;
    private Button signInBtn;
    private TextView registerLink;
    private TextView resetPassword;
    private StitchUser currentUser;

    @Override
    protected void onStart() {
        super.onStart();
        if (client == null)
            client = Stitch.initializeAppClient("todo-orxsm");

        client = Stitch.getAppClient("todo-orxsm");
        currentUser = client.getAuth().getUser();
        if (currentUser != null) {
            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
            finish();
        }
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        overridePendingTransition(R.anim.frombottom, R.anim.clovernim);

        emailView = (EditText) findViewById(R.id.username);
        passwordView = (EditText) findViewById(R.id.password);
        signInBtn = (Button) findViewById(R.id.signinbtn);
        registerLink = (TextView) findViewById(R.id.registerlink);
        resetPassword = (TextView) findViewById(R.id.resetpassword);
        emailView.setError(null);
        passwordView.setError(null);

        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        signInBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        resetPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                recoverPassword();
            }
        });

        registerLink.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignupUserActivity.class));
            }
        });
    }

    private void attemptLogin() {
        final String email = emailView.getText().toString();
        String password = passwordView.getText().toString();
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(email)) {
            emailView.setError(getString(R.string.login_field_required));
            focusView = emailView;
            cancel = true;
        } else if (!MiscFunc.isEmailValid(email)) {
            emailView.setError(getString(R.string.login_invalid_email));
            focusView = emailView;
            cancel = true;
        }

        if (!TextUtils.isEmpty(password) && !MiscFunc.isPasswordValid(password)) {
            passwordView.setError(getString(R.string.login_invalid_password));
            focusView = passwordView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            UserPasswordCredential credential = new UserPasswordCredential(email, password);
            client.getAuth().loginWithCredential(credential).addOnCompleteListener(new OnCompleteListener<StitchUser>() {
                @Override
                public void onComplete(@NonNull final Task<StitchUser> task) {
                    if (task.isSuccessful()) {
                        Log.d("stitch", "Successfully logged in as user " + task.getResult().getId());
                        Toast.makeText(LoginActivity.this, "Successfully logged in.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                        finish();
                    } else {
                        Log.e("stitch", "Error logging in with email/password auth:", task.getException());
                        Toast.makeText(LoginActivity.this, "Unsuccessful login.", Toast.LENGTH_SHORT).show();
                        passwordView.setText("");
                    }
                }
            });
        }
    }

    private void recoverPassword() {
        View focusView = null;
        String email = emailView.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailView.setError(getString(R.string.login_field_required));
            focusView = emailView;
        } else {
            UserPasswordAuthProviderClient emailPassClient = client.getAuth().getProviderClient(UserPasswordAuthProviderClient.factory);

            emailPassClient.sendResetPasswordEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull final Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d("stitch", "Password reset mail sent.");
                        Toast.makeText(LoginActivity.this, "Password reset mail sent.", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("stitch", "Error sending password reset email:", task.getException());
                        Toast.makeText(LoginActivity.this, "Account not found!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}