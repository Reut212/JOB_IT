package com.example.myapplication.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.Model.Data;
import com.example.myapplication.R;
import com.example.myapplication.detailedJobData;

import java.util.ArrayList;

public class adapterClass extends RecyclerView.Adapter<adapterClass.MyViewHolder>{


    ArrayList<Data> dataList;
    private OnItemClickListener mListener;
    public static String email;

    public interface Callbacks {
        public void onButtonClicked(String titleKey);
    }

    private Callbacks mCallbacks;

    public adapterClass(Context context, ArrayList<Data> dataList) {
        this.dataList = dataList;
        email = "";
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    public adapterClass(ArrayList<Data> dataList){
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_board_item, parent, false);
        MyViewHolder mvh = new MyViewHolder(v, mListener);
        return mvh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.title.setText(dataList.get(position).getTitle());
        holder.description.setText(dataList.get(position).getDescription());
        holder.jobDate.setText(dataList.get(position).getDateJob());
        holder.date.setText(dataList.get(position).getDate());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), detailedJobData.class);
                intent.putExtra("title", dataList.get(position).getTitle());
                intent.putExtra("date", dataList.get(position).getDate());
                intent.putExtra("description", dataList.get(position).getDescription());
                intent.putExtra("jobDate", dataList.get(position).getDateJob());
                intent.putExtra("salary", dataList.get(position).getSalary());
                intent.putExtra("email", dataList.get(position).getEmail());
                email = dataList.get(position).getEmail();
                intent.putExtra("contactName", dataList.get(position).getContactName());
                intent.putExtra("location", dataList.get(position).getLocation());
                intent.putExtra("imageUrl" , dataList.get(position).getImageUrl());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setCallbacks(Callbacks callbacks) {
        this.mCallbacks = callbacks;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView title, description, jobDate, date;
        ImageView imageView;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            title = itemView.findViewById(R.id.job_title1);
            description = itemView.findViewById(R.id.job_desc1);
            jobDate = itemView.findViewById(R.id.job_date);
            date = itemView.findViewById(R.id.job_date_board1);
            imageView = itemView.findViewById(R.id.imageView2);
            cardView = (CardView) itemView.findViewById(R.id.card_view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }

    }


}