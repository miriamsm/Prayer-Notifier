package com.praytimes.MobileProject.prayernotifier;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.praytimes.MobileProject.R;

import java.util.ArrayList;

public class ViewAdapter extends RecyclerView.Adapter<ViewAdapter.MyViewHolder> {

    Context context;
    ArrayList<PrayerTime> list;

    public ViewAdapter(Context c,ArrayList<PrayerTime> l){
        context = c;
        list = l;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_view_adapter,viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {

        holder.name.setText(list.get(i).getName());
        holder.time.setText(list.get(i).getTime());

        boolean test = list.get(i).isNext();

        if(test){ //for test
            holder.layout.setBackgroundResource(R.drawable.now_prayer);
            holder.icon.setBackgroundResource(R.drawable.baseline22);

        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name,time;
        LinearLayout layout;
        TextView icon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.prayerName);
            time = (TextView) itemView.findViewById(R.id.prayerTime);
            layout = itemView.findViewById(R.id.timeView);
            icon = (TextView) itemView.findViewById(R.id.notifyIcon);

        }
    }

}

