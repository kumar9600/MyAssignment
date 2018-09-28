package com.think42labs.assignment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.think42labs.assignment.database.DbHelper;
import com.think42labs.assignment.loginscreen.R;
import com.think42labs.assignment.model.User;
import com.think42labs.assignment.utils.CommonUtils;
import com.think42labs.assignment.utils.VerhoeffAlgorithm;

public class SignupScreen extends AppCompatActivity {
    private final AppCompatActivity ACTIVITY = SignupScreen.this;
    private ScrollView container;
    private AppCompatEditText email, username, age, phonenumber, aadharno, address, password, passwordConform;
    private Button signupBtn;
    private TextView gotologinpage;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupscreen);
        initializeView();
    }

    private void initializeView() {
        passwordConform = (AppCompatEditText) findViewById(R.id.passwordcontext);
        email = (AppCompatEditText) findViewById(R.id.emailtext);
        username = (AppCompatEditText) findViewById(R.id.usernmetext);
        age = (AppCompatEditText) findViewById(R.id.agetext);
        phonenumber = (AppCompatEditText) findViewById(R.id.phonenumbertext);
        aadharno = (AppCompatEditText) findViewById(R.id.aadhartext);
        address = (AppCompatEditText) findViewById(R.id.addresstext);
        password = (AppCompatEditText) findViewById(R.id.passwordtext);
        gotologinpage = (TextView) findViewById(R.id.loginpage);
        signupBtn = (Button) findViewById(R.id.signup);
        container = (ScrollView) findViewById(R.id.signupContainer);

        dbHelper = new DbHelper(ACTIVITY);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signupAttempt();
            }
        });

        gotologinpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoLogin();
            }
        });
    }

    private void gotoLogin() {
        startActivity(new Intent(SignupScreen.this, LoginScreen.class));
        finish();
    }

    public boolean validate() {
        boolean valid = true;
        String mail = email.getText().toString();
        String user = username.getText().toString();
        String pass = password.getText().toString();
        String userage = age.getText().toString();
        String phone = phonenumber.getText().toString();
        String aadhar = aadharno.getText().toString();
        String addre = address.getText().toString();
        String passcon = passwordConform.getText().toString();

        if (mail.isEmpty()) {
            email.setError("Email is required");
            CommonUtils.showSnack(container, "Please check your email");
            valid = false;
        } else {
            email.setError(null);
        }

        if (user.isEmpty()) {
            username.setError("Username is required");
            CommonUtils.showSnack(container, "Please check your username");
            valid = false;
        } else {
            username.setError(null);
        }

        if (pass.isEmpty()) {
            password.setError("Password is required");
            CommonUtils.showSnack(container, "Please check your password");
            valid = false;
        } else {
            password.setError(null);
        }

        if (passcon.isEmpty()) {
            passwordConform.setError("Confirm password is required");
            CommonUtils.showSnack(container, "Please check your confirm password");
            valid = false;
        } else {
            passwordConform.setError(null);
        }

        if (!pass.equals(passcon)){
            CommonUtils.showSnack(container,"Password does not match");
            valid = false;
        }

        if (userage.isEmpty()) {
            age.setError("Age is required");
            CommonUtils.showSnack(container, "Please check your age");
            valid = false;
        } else {
            age.setError(null);
        }
        if (userage.length() == 0) {
            age.setError("Age should not be zero");
            CommonUtils.showSnack(container, "Please check your age");
            valid = false;
        } else {
            age.setError(null);
        }

        if (phone.isEmpty()) {
            phonenumber.setError("Phone no is required");
            CommonUtils.showSnack(container, "Please check your phone no");
            valid = false;
        } else {
            phonenumber.setError(null);
        }
        if (phone.length() != 10) {
            phonenumber.setError("Length should be 10 numbers");
            CommonUtils.showSnack(container, "Please check your phone no");
            valid = false;
        } else {
            phonenumber.setError(null);
        }

        if (aadhar.isEmpty()) {
            aadharno.setError("Aadhar no is required");
            CommonUtils.showSnack(container, "Please check your aadhar no");
            valid = false;
        } else {
            aadharno.setError(null);
        }
        if (aadhar.length() < 12) {
            aadharno.setError("Length should be 12 numbers");
            CommonUtils.showSnack(container, "Please check your aadhar no");
            valid = false;
        } else {
            aadharno.setError(null);
        }

        if (addre.isEmpty()) {
            address.setError("Address is required");
            CommonUtils.showSnack(container, "Please check your address");
            valid = false;
        } else {
            address.setError(null);
        }
        return valid;
    }

    private void signupAttempt() {
        if (!validate()) {
            return;
        }
        if (!CommonUtils.isEmailValid(email.getText().toString())) {
            CommonUtils.showSnack(container, "Not a valid email");
            return;
        }
        boolean result = VerhoeffAlgorithm.validateVerhoeff(aadharno.getText().toString());
        String msg = String.valueOf(result);
        if (msg == "false") {
            CommonUtils.showSnack(container, "Not a valid aadhar no");
            return;
        }
        String mail = email.getText().toString();
        String usern = username.getText().toString();
        String pass = password.getText().toString();
        String userage = age.getText().toString();
        String phone = phonenumber.getText().toString();
        String aadhar = aadharno.getText().toString();
        String addre = address.getText().toString();
        Log.e("TAG", "signup attempt: " + phone);

        if (!dbHelper.isEmailExistAlready(mail)) {
            Log.e("TAG", "signup atttempt: " + phone);
            User user = new User(mail, pass);
            user.setName(usern);
            user.setAge(Integer.parseInt(userage));
            user.setPhoneNo(Long.parseLong(phone));
            user.setAadhaarNo(Long.parseLong(aadhar));
            user.setAddress(addre);
            dbHelper.addUser(user);
            CommonUtils.showToast(this, "User successfully created");
            startActivity(new Intent(SignupScreen.this, LoginScreen.class));
            finish();
        } else {
            CommonUtils.showSnack(container, "The user is already exist");
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
