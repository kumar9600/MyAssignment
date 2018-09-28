package com.think42labs.assignment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.think42labs.assignment.database.DbHelper;
import com.think42labs.assignment.loginscreen.R;
import com.think42labs.assignment.model.User;
import com.think42labs.assignment.utils.CommonUtils;
import com.think42labs.assignment.utils.Constant;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.think42labs.assignment.utils.Constant.CAMERA_REQUEST;
import static com.think42labs.assignment.utils.Constant.GALLERY_REQUEST;

public class EditProfileActivity extends AppCompatActivity {
    private final AppCompatActivity ACTIVITY = EditProfileActivity.this;
    private ScrollView container;
    private AppCompatEditText name, age, phoneNo, aadharNo, address;
    private DbHelper dbHelper;
    private ImageView btnUserImage;
    private Button updateuserRecords, cancelRecords;
    private User user;
    private String mCurrentPhotoPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);
        initViews();
    }

    private void initViews() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            this.user = null;
            if (bundle.getSerializable(Constant.USER_PROFILE) != null) {
                this.user = (User)
                        bundle.getSerializable(Constant.USER_PROFILE);
            }
        }
        dbHelper = new DbHelper(ACTIVITY);
        btnUserImage = (ImageView) findViewById(R.id.editprofileImage);
        container = (ScrollView) findViewById(R.id.editprofileContainer);
        name = (AppCompatEditText) findViewById(R.id.editname);
        age = (AppCompatEditText) findViewById(R.id.editAge);
        phoneNo = (AppCompatEditText) findViewById(R.id.editphone);
        aadharNo = (AppCompatEditText) findViewById(R.id.editaadhar);
        address = (AppCompatEditText) findViewById(R.id.editaddress);
        updateuserRecords = (Button) findViewById(R.id.save);
        cancelRecords = (Button) findViewById(R.id.cancel);

        btnUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        updateuserRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUser();
            }
        });
        cancelRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar snackbar = CommonUtils.showSnackWithBtn(container, "Are you sure to cancel updating user");
                snackbar.setAction("Yes", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(EditProfileActivity.this, ProfileScreen.class));
                        finish();
                    }
                });
                snackbar.show();
            }
        });
        this.bindUser();
    }

    private void bindUser() {
        if (this.user != null) {
            this.name.setText(this.user.getName());
            this.age.setText(this.user.getAge() + "");
            this.phoneNo.setText("" + this.user.getPhoneNo());
            this.address.setText(this.user.getAddress());
            this.aadharNo.setText(this.user.getAadhaarNo() + "");
        }
    }

    private boolean validate() {
        if (this.user != null) {
            boolean isModified = false;
            if (this.name.getText().toString().trim().isEmpty() == false) {
                if (CommonUtils.isStringModified(this.user.getName(), name.getText().toString())) {
                    isModified = true;
                }
            } else {
                CommonUtils.showSnack(this.container, "Name should not empty");
                return false;
            }

            if (CommonUtils.isStringModified(this.user.getAge() + "", this.age.getText().toString())) {
                isModified = true;
            }

            if (CommonUtils.isStringModified(this.user.getPhoneNo() + "", this.phoneNo.getText().toString())) {
                isModified = true;
            }

            if (CommonUtils.isStringModified(this.user.getAadhaarNo() + "", this.aadharNo.getText().toString())) {
                isModified = true;
            }

            if (CommonUtils.isStringModified(this.user.getAddress(), this.address.getText().toString())) {
                isModified = true;
            }

            if (CommonUtils.isStringModified(this.user.getImagePath(), this.mCurrentPhotoPath)) {
                isModified = true;
            }

            if (!isModified) {
                CommonUtils.showSnack(this.container, "No changes found");
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    private void updateUser() {
        if (this.validate()) {
            User user = new User(this.user.getEmail(), null);
            //current record
            String nameStr = name.getText().toString();
            String ageStr = age.getText().toString();
            String phoneNOStr = phoneNo.getText().toString();
            String addressStr = address.getText().toString();
            String aadharStr = aadharNo.getText().toString();

            user.setName(nameStr);
            user.setPhoneNo(Long.parseLong(phoneNOStr));
            user.setAadhaarNo(Long.parseLong(aadharStr));
            user.setAddress(addressStr);
            user.setAge(Integer.parseInt(ageStr));
            user.setImagePath(mCurrentPhotoPath);

            dbHelper.updateUser(user);
            //empty fields
            name.setText("");
            age.setText("");
            phoneNo.setText("");
            aadharNo.setText("");
            address.setText("");
            CommonUtils.showToast(this, "Profile updated");
            startActivity(new Intent(this, ProfileScreen.class));
            finish();
        }

    }
    public void selectImage() {

        final CharSequence items[] = {"camera", "gallery", "cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (items[i].equals("camera")) {

                    requestPermissionForCamera(CAMERA_REQUEST);
                    dialogInterface.dismiss();
                } else if (items[i].equals("gallery")) {

                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent, GALLERY_REQUEST);
                } else if (items[i].equals("cancel")) {

                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    private void requestPermissionForCamera(int request) {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                CommonUtils.showSnack(container,"This App needs Camera");
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        request);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                dispatchTakePictureIntent(request);
            } else {
                cameraForOld(request);
            }
        }
    }

    private void dispatchTakePictureIntent(int REQUEST_TAKE_PHOTO) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(REQUEST_TAKE_PHOTO);
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("TAG", ex.getMessage(), ex);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.think42labs.assignment.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile(int REQUEST) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" +
                "" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        mCurrentPhotoPath = image.getAbsolutePath();

        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST) {

            Picasso.get().load(new File(mCurrentPhotoPath))
                    .resize(100, 100)
                    .into(btnUserImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap imageBitmap = ((BitmapDrawable) btnUserImage.getDrawable()).getBitmap();
                            RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                            imageDrawable.setCircular(true);
                            imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                            btnUserImage.setImageDrawable(imageDrawable);

                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e("TAG", "exception camera : " + e);
                            btnUserImage.setImageResource(R.drawable.camera_icon);
                            CommonUtils.showToast(EditProfileActivity.this,"Something Went Wrong While storing image");
                        }
                    });
        } else if (requestCode == GALLERY_REQUEST) {

            final Uri selectedImageUri = data.getData();
            mCurrentPhotoPath = getPath(EditProfileActivity.this, selectedImageUri);
            Picasso.get().load(new File(mCurrentPhotoPath))
                    .resize(100, 100)
                    .into(btnUserImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap imageBitmap = ((BitmapDrawable) btnUserImage.getDrawable()).getBitmap();
                            RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                            imageDrawable.setCircular(true);
                            imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                            btnUserImage.setImageDrawable(imageDrawable);

                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e("TAG", "exception camera : " + e);
                            btnUserImage.setImageResource(R.drawable.camera_icon);
                            CommonUtils.showToast(EditProfileActivity.this,"Something Went Wrong While storing image");
                        }
                    });
        }

    }

    private void cameraForOld(int REQUEST_TAKE_PHOTO) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(REQUEST_TAKE_PHOTO);
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("TAg", ex.getMessage(), ex);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    public static String getPath(Context context, Uri uri) {
        String result = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(proj[0]);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        if (result == null) {
            result = "Not found";
        }
        return result;
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(EditProfileActivity.this, ProfileScreen.class));
        finish();
    }
}
