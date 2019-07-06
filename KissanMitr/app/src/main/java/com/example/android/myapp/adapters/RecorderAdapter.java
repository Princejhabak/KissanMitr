package com.example.android.myapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.myapp.R;
import com.example.android.myapp.helper.RecorderHelper;

import java.util.List;

public class RecorderAdapter  extends ArrayAdapter<RecorderHelper> {

    public RecorderAdapter(Context context, List<RecorderHelper> recordings) {
        super(context, 0, recordings);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.recordings_list_item, parent, false);
        }

        RecorderHelper currentFile = getItem(position);

        TextView fileNameView = listItemView.findViewById(R.id.recording_name);
        fileNameView.setText(currentFile.getRecordingName());

        TextView fileDateView = listItemView.findViewById(R.id.recording_date);
        fileDateView.setText(currentFile.getRecordingDate());

        TextView fileLengthView = listItemView.findViewById(R.id.recording_length);
        fileLengthView.setText(currentFile.getRecordingLength());



        return listItemView;
    }

}
