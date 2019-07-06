package com.example.android.myappadmin.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.myappadmin.R;
import com.example.android.myappadmin.helper.StatsHelper;
import java.util.List;

public class StatisticsAdapter extends ArrayAdapter<StatsHelper> {

    private Activity context;
    private List<StatsHelper> list;

    private String TAG = "Stats Adapter";

    public StatisticsAdapter(Activity context, List<StatsHelper> list) {
        super(context, R.layout.statistics_list_item,list);
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.statistics_list_item, null, true);

        TextView tvLocation = listViewItem.findViewById(R.id.stats_location);
        TextView tvAddress = listViewItem.findViewById(R.id.stats_address);
        TextView tvAudioCount = listViewItem.findViewById(R.id.stats_audio);
        TextView tvImageCount = listViewItem.findViewById(R.id.stats_images);

        StatsHelper statsHelper = list.get(position);

        tvLocation.setText(statsHelper.getLocation());
        tvAddress.setText(statsHelper.getAddress());
        tvAudioCount.setText(statsHelper.getAudioCount());
        tvImageCount.setText(statsHelper.getImageCount());

        return listViewItem;
    }
}
