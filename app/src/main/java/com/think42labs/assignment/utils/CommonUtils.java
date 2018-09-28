
package com.think42labs.assignment.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.think42labs.assignment.loginscreen.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static void showToast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        View view1 = toast.getView();
        toast.getView().setPadding(20, 20, 20, 20);
        view1.setBackgroundResource(R.color.bittersweet);
        TextView text = (TextView) view1.findViewById(android.R.id.message);
        text.setTextColor(context.getResources().getColor(R.color.aliceblue));
        toast.show();
    }

    public static void showSnack(View view, String message) {
        final Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        TextView snackViewText = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        Button snackViewButton = (Button) sbView.findViewById(android.support.design.R.id.snackbar_action);
        sbView.setBackgroundColor(view.getResources().getColor(R.color.bittersweet));
        snackViewText.setTextColor(view.getResources().getColor(R.color.aliceblue));
        snackViewButton.setTextColor(view.getResources().getColor(R.color.aliceblue));
        snackbar.show();
    }
    public static Snackbar showSnackWithBtn(View view, String message) {
        final Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);
        View sbView = snackbar.getView();
        TextView snackViewText = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        Button snackViewButton = (Button) sbView.findViewById(android.support.design.R.id.snackbar_action);
        sbView.setBackgroundColor(view.getResources().getColor(R.color.bittersweet));
        snackViewText.setTextColor(view.getResources().getColor(R.color.aliceblue));
        snackViewButton.setTextColor(view.getResources().getColor(R.color.aliceblue));
        return snackbar;
    }

    public static boolean isStringModified(String existing, String modified) {
        boolean existingIsEmpty = existing == null || existing.trim().length() == 0;
        boolean modifiedIsEmpty = modified == null || modified.trim().length() == 0;

        if (existingIsEmpty != modifiedIsEmpty) {
            return true;
        }
        if (!existingIsEmpty && !modifiedIsEmpty) {
            return !existing.equals(modified);
        }
        return false;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
