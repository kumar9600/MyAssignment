package com.think42labs.assignment.utils;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.think42labs.assignment.loginscreen.R;

public class Constant {
    public static final String PREF_NAME = "mypref";
    public static final String EMAIL_PREF = "mail";
    public static final String USER_PROFILE = "userProfile";
    public static final int CAMERA_REQUEST = 1;
    public static final int GALLERY_REQUEST = 2;
    public static final String BASE_URL = "https://api.androidhive.info/contacts/";
    public static final int GET = Request.Method.GET;
}
