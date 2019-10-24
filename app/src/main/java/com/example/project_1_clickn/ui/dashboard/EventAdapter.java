package com.example.project_1_clickn.ui.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import com.example.project_1_clickn.R;

public class EventAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<EventModel> eventModelArrayList;

    public EventAdapter(Context context, ArrayList<EventModel> eventModelArrayList) {

        this.context = context;
        this.eventModelArrayList = eventModelArrayList;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }
    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public int getCount() {
        return eventModelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return eventModelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.lv_events, null, true);

            holder.tvtitle = (TextView) convertView.findViewById(R.id.title);
            holder.tvdescription = (TextView) convertView.findViewById(R.id.description);

            convertView.setTag(holder);
        } else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)convertView.getTag();
        }

        holder.tvtitle.setText("Title: " + eventModelArrayList.get(position).getEventTitle());
        holder.tvdescription.setText("Description: " + eventModelArrayList.get(position).getEventDescription());

        return convertView;
    }

    private class ViewHolder {
        protected TextView tvtitle, tvdescription;
    }
}
