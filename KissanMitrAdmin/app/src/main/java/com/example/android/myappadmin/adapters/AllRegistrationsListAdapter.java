package com.example.android.myappadmin.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.CircularProgressDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.myappadmin.R;
import com.example.android.myappadmin.helper.AllRegistrationsListHelper;

import java.util.List;

public class AllRegistrationsListAdapter extends ArrayAdapter<AllRegistrationsListHelper> {
    private Activity context;
    private List<AllRegistrationsListHelper> list;

    public AllRegistrationsListAdapter (Activity context, List<AllRegistrationsListHelper> list){
        super(context, R.layout.all_registrations_list_item, list);
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.all_registrations_list_item, null, true);

        ImageView ivDp = listViewItem.findViewById(R.id.all_registrations_dp);
        TextView  tvMobileNo = listViewItem.findViewById(R.id.all_registrations_mobile_no);
        TextView tvName = listViewItem.findViewById(R.id.all_registrations_name);

        final AllRegistrationsListHelper allRegistrationsListHelper = list.get(position);

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5);
        circularProgressDrawable.setCenterRadius(20);
        circularProgressDrawable.start();

        Glide.with(context).load(allRegistrationsListHelper.getDp()).placeholder(circularProgressDrawable).into(ivDp);

        tvMobileNo.setText(allRegistrationsListHelper.getMobileNo());
        tvName.setText(allRegistrationsListHelper.getName());

        return listViewItem;
    }
}

