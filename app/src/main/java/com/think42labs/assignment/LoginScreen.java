package com.think42labs.assignment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.think42labs.assignment.database.DbHelper;
import com.think42labs.assignment.loginscreen.R;
import com.think42labs.assignment.utils.CommonUtils;

import static com.think42labs.assignment.utils.CommonUtils.showSnack;
import static com.think42labs.assignment.utils.Constant.EMAIL_PREF;
import static com.think42labs.assignment.utils.Constant.PREF_NAME;

public class LoginScreen extends AppCompatActivity {
    private final AppCompatActivity ACTIVITY = LoginScreen.this;
    private DbHelper dbHelper;
    private ScrollView container;
    private TextView signupPage;
    private EditText userName, passWord;
    private Button loginButton;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginscreen);
        initViews();
    }

    private void initViews() {
        this.dbHelper = new DbHelper(ACTIVITY);
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String email = sharedPreferences.getString(EMAIL_PREF, "");
        if (!email.isEmpty() && email != null) {
            startActivity(new Intent(LoginScreen.this, ProfileScreen.class));
            finish();
        }
        signupPage = (TextView) findViewById(R.id.singuppage);
        userName = (EditText) findViewById(R.id.name);
        passWord = (EditText) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.login);
        container = (ScrollView) findViewById(R.id.loginContainer);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginAttempt();
            }
        });
        signupPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoSignupPage();
            }
        });
    }


    public boolean validate() {
        boolean valid = true;
        String user = userName.getText().toString();
        String pass = passWord.getText().toString();

        if (user.isEmpty()) {
            userName.setError("Enter the Email ");
            CommonUtils.showSnack(container, "Please check your username");
            valid = false;
        } else {
            userName.setError(null);
        }

        if (pass.isEmpty()) {
            passWord.setError("Enter valid password");
            CommonUtils.showSnack(container, "Please check your password");
            valid = false;
        } else {
            passWord.setError(null);
        }

        return valid;
    }

    private void loginAttempt() {
        if (!validate()) {
            return;
        }
        if (!CommonUtils.isEmailValid(userName.getText().toString())) {
            CommonUtils.showSnack(container, "This is not a valid email");
            return;
        }

        String username = userName.getText().toString();
        String password = passWord.getText().toString();
        if (dbHelper.login(username, password)) {
            sharedPreferences = getSharedPreferences(PREF_NAME,MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(EMAIL_PREF,username);
            editor.commit();
            CommonUtils.showToast(this, "Login success");
            startActivity(new Intent(LoginScreen.this, ProfileScreen.class));
            finish();
        } else {
            CommonUtils.showSnack(container, "Incorrect username or password");
        }

    }

    private void gotoSignupPage() {
        startActivity(new Intent(LoginScreen.this, SignupScreen.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
