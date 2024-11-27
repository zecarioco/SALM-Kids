package com.unir.salmkids;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;

public class CustomAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;
    private final int[] images;  // Array of image resource IDs

    public CustomAdapter(Context context, String[] values, int[] images) {
        super(context, R.layout.list_item_button, values);
        this.context = context;
        this.values = values;
        this.images = images;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_button, parent, false);

            holder = new ViewHolder();
            holder.textView = convertView.findViewById(R.id.textViewItem);
            holder.imageView = convertView.findViewById(R.id.imageViewItem);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textView.setText(values[position]);
        holder.imageView.setImageResource(images[position]);

        return convertView;
    }


    // ViewHolder class to cache references to views
    static class ViewHolder {
        TextView textView;
        ImageView imageView;
    }
}
