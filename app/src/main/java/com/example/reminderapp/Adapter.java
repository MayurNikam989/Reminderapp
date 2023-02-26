package com.example.reminderapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ConcurrentModificationException;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    LayoutInflater layoutInflater;
    List<String> titles;
    List<String> descriptions;
    List<String> dates;

    public Adapter(Context context, List<String> titles, List<String> descriptions, List<String> dates){
        this.layoutInflater = LayoutInflater.from(context);
        this.titles = titles;
        this.descriptions = descriptions;
        this.dates = dates;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.custom_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String title = titles.get(position);
        String desc = descriptions.get(position);
        String date = dates.get(position);

        holder.Content.setText(desc);
        holder.titles.setText(title);
        holder.Date.setText(date);


    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView titles, Content, Date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titles = itemView.findViewById(R.id.xlTitle);
            Content = itemView.findViewById(R.id.xlDate);
            Date = itemView.findViewById(R.id.xlTime);
        }
    }
}
