
package com.think42labs.assignment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.think42labs.assignment.adapter.ListAdapter;
import com.think42labs.assignment.listener.RecyclerTouchListener;
import com.think42labs.assignment.loginscreen.R;
import com.think42labs.assignment.model.Contact;
import com.think42labs.assignment.utils.CommonUtils;
import com.think42labs.assignment.utils.Constant;
import com.think42labs.assignment.utils.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    private ConstraintLayout constraintLayout;
    private RecyclerView recyclerView_so;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<Contact> arrayList = new ArrayList<Contact>();
    private ListAdapter listAdapter ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        initializeviews();
    }

    private void initializeviews() {
        constraintLayout = (ConstraintLayout)findViewById(R.id.containerList);
        recyclerView_so = (RecyclerView)findViewById(R.id.recycler) ;
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeto);
        if (!CommonUtils.isNetworkAvailable(this)) {
            CommonUtils.showSnack(constraintLayout,"Please check your network connection");
        }
        getDataFromJson();
        recyclerView_so.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView_so, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Contact con = arrayList.get(position);
                CommonUtils.showToast(ListActivity.this,"id : "+con.getId()+"\n"
                                                                +"name : "+con.getName()+"\n"
                                                                +"address : "+con.getAddress());
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!CommonUtils.isNetworkAvailable(ListActivity.this)) {
                    CommonUtils.showSnack(constraintLayout,"Please check your network connection");
                }
                arrayList.clear();
                getDataFromJson();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void getDataFromJson() {
        final ProgressDialog progressDialog = new ProgressDialog(ListActivity.this);
        progressDialog.setCancelable(false); // set cancelable to false
        progressDialog.setMessage("Please Wait"); // set message
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Constant.GET, Constant.BASE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.e("TAG", "Reponse : " + response);
                try {
                    JSONObject jsonObj = new JSONObject(response);
                    JSONArray contacts = jsonObj.getJSONArray("contacts");
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        String id = c.getString("id");
                        String name = c.getString("name");
                        String email = c.getString("email");
                        String address = c.getString("address");
                        String gender = c.getString("gender");
                        JSONObject phone = c.getJSONObject("phone");
                        String mobile = phone.getString("mobile");
                        String home = phone.getString("home");
                        String office = phone.getString("office");

                        Contact contact = new Contact();
                        contact.setId(id);
                        contact.setName(name);
                        contact.setAddress(address);
                        contact.setEmail(email);
                        contact.setGender(gender);
                        contact.setHome(home);
                        contact.setMobile(mobile);
                        contact.setOffice(office);
                        arrayList.add(contact);

                    }
                    listAdapter = new ListAdapter(ListActivity.this, arrayList);
                    recyclerView_so.setAdapter(listAdapter);
                    recyclerView_so.setHasFixedSize(true);
                    recyclerView_so.setLayoutManager(new LinearLayoutManager(ListActivity.this));
                } catch (JSONException jsonStr) {

                    progressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressDialog.dismiss();
                Log.e("TAG", "error : " + error);
            }
        });
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ListActivity.this,ProfileScreen.class));
        finish();
    }
}
