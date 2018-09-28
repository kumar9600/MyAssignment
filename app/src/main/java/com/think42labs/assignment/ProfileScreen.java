package com.think42labs.assignment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.think42labs.assignment.database.DbHelper;
import com.think42labs.assignment.loginscreen.R;
import com.think42labs.assignment.model.User;
import com.think42labs.assignment.utils.CommonUtils;
import com.think42labs.assignment.utils.Constant;

import java.io.File;

import static com.think42labs.assignment.utils.Constant.EMAIL_PREF;
import static com.think42labs.assignment.utils.Constant.PREF_NAME;

public class ProfileScreen extends AppCompatActivity {
    private final AppCompatActivity ACTIVITY = ProfileScreen.this;
    private TextView profileMail, profileName, profileAge, profilePhoneNo, profileAadharNo, profileAddress;
    private ImageView profileImage;
    private FloatingActionButton floatingButton;
    private DbHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private String userMail;
    private User user;
    private Button btnShowList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilescreen);
        initializeviews();
    }

    private void initializeviews() {
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String mail = sharedPreferences.getString(EMAIL_PREF, "");
        if (!mail.equalsIgnoreCase("") && mail != null) {
            userMail = mail;
        }
        btnShowList = (Button) findViewById(R.id.showList);
        floatingButton = (FloatingActionButton)findViewById(R.id.floatingButton);
        profileImage = (ImageView)findViewById(R.id.profileimage);
        profileName = (TextView) findViewById(R.id.profileusername);
        profileAge = (TextView)findViewById(R.id.profileage);
        profilePhoneNo = (TextView) findViewById(R.id.phoneNo);
        profileAadharNo = (TextView) findViewById(R.id.profileaadhar);
        profileAddress = (TextView) findViewById(R.id.profileaddress);
        profileMail = (TextView) findViewById(R.id.profilemailid);
        dbHelper = new DbHelper(ACTIVITY);
        btnShowList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showListData();
            }
        });
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editProfile();
            }
        });
        getUserProfileDetails();
    }

    private void showListData() {
        startActivity(new Intent(ProfileScreen.this,ListActivity.class));
        finish();
    }

    private void editProfile() {
        if (this.user != null){
            Intent intent = new Intent(ProfileScreen.this, EditProfileActivity.class);
            intent.putExtra(Constant.USER_PROFILE, this.user);
            startActivity(intent);
            finish();
        }
    }

    private void getUserProfileDetails() {
        this.user = dbHelper.getUser(userMail);
        profileName.setText(user.getName());
        profileMail.setText(user.getEmail());
        profileAge.setText(""+user.getAge());
        profilePhoneNo.setText(""+user.getPhoneNo());
        profileAadharNo.setText(""+user.getAadhaarNo());
        profileAddress.setText(user.getAddress());
        if (user.getImagePath() == null){
            profileImage.setImageResource(R.drawable.aadharico);
        } else {
            Picasso.get().load(new File(user.getImagePath()))
                    .resize(100, 100)
                    .into(profileImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap imageBitmap = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
                            RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                            imageDrawable.setCircular(true);
                            imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                            profileImage.setImageDrawable(imageDrawable);

                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e("TAG", "exception camera : " + e);
                            profileImage.setImageResource(R.drawable.camera_icon);
                            CommonUtils.showToast(ProfileScreen.this,"Something Went Wrong While storing image");
                        }
                    });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout, menu);
        return true;
    }

    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        CommonUtils.showToast(this, "Logout successfully");
        startActivity(new Intent(getApplicationContext(), LoginScreen.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_logout_id:
                logout();
                break;
            default:
                //nothing selected
                break;
        }

        return true;
    }
}
