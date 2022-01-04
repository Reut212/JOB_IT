package com.example.myapplication.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.Data;
import com.example.myapplication.R;
import com.example.myapplication.detailedJobData;
import com.example.myapplication.employeeRegister;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class adapterClassEmployee extends RecyclerView.Adapter<adapterClassEmployee.MyViewHolder>{

    ArrayList<Data> dataList;
    private OnItemClickListener mListener;
    public static String email;

    private FirebaseAuth fauth = FirebaseAuth.getInstance();
    FirebaseUser fUser = fauth.getCurrentUser();
    String uID;
    {
        assert fUser != null;
        uID = fUser.getUid();

    }

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PostJobInfo").child(uID);
    DatabaseReference favoriteRef = FirebaseDatabase.getInstance().getReference("favourites");
    DatabaseReference fvrt_listRef = FirebaseDatabase.getInstance().getReference("favouritesList").child(uID);
    DatabaseReference reff = FirebaseDatabase.getInstance().getReference();


    public adapterClassEmployee(ArrayList<Data> dataList){
        this.dataList = dataList;
    }
    public adapterClassEmployee(Context context, ArrayList<Data> dataList) {
        this.dataList = dataList;
        email = "";
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_employee_board_item, parent, false);
        MyViewHolder mvh = new MyViewHolder(v, mListener);
        return mvh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
//        String time = dataList.get(position).getTimeNow();
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

    static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView title, description, jobDate, date;
        ImageView imageView;
        CardView cardView;

        ImageButton favBut;
        DatabaseReference databaseReference, favoriteRef, fvrt_listRef;

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
                        //get item position in recycler view
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
