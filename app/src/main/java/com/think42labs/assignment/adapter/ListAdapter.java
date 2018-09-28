/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.think42labs.assignment.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.think42labs.assignment.loginscreen.R;
import com.think42labs.assignment.model.Contact;

import java.util.Collections;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    List<Contact> data = Collections.emptyList();
    Contact current;
    int currentPos = 0;

    public ListAdapter(Context context, List<Contact> soApprovalModelList_arrayList) {

        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = soApprovalModelList_arrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_items, parent, false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyHolder myHolder = (MyHolder) holder;
        Contact current = data.get(position);
        myHolder.listId.setText(""+current.getId());
        myHolder.listname.setText(""+current.getName());
        myHolder.listGender.setText(""+current.getGender());
        myHolder.listAddress.setText(""+current.getAddress());
    }

    @Override
    public int getItemCount() {

        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}

class MyHolder extends RecyclerView.ViewHolder {
    TextView listId,listname,listGender,listAddress;

    public MyHolder(View itemView) {
        super(itemView);
        listId = (TextView) itemView.findViewById(R.id.idlist);
        listname = (TextView)itemView.findViewById(R.id.nameList);
        listGender = (TextView)itemView.findViewById(R.id.genderlist);
        listAddress = (TextView)itemView.findViewById(R.id.addresslist);
    }

}
