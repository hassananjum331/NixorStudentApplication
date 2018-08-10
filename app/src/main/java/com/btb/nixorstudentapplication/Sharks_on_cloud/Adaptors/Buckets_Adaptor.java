package com.btb.nixorstudentapplication.Sharks_on_cloud.Adaptors;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.btb.nixorstudentapplication.Misc.common_util;
import com.btb.nixorstudentapplication.R;
import com.btb.nixorstudentapplication.Sharks_on_cloud.MyBucket;
//import com.btb.nixorstudentapplication.Sharks_on_cloud.Navigation_Classes.BucketData;
import com.btb.nixorstudentapplication.Sharks_on_cloud.Navigation_Classes.Buckets;
import com.btb.nixorstudentapplication.Sharks_on_cloud.Objects.BucketsObject;
import com.btb.nixorstudentapplication.Sharks_on_cloud.Soc_Main;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Buckets_Adaptor extends RecyclerView.Adapter<Buckets_Adaptor.Rv_ViewHolder> implements View.OnClickListener {
    ArrayList<BucketsObject> bucketNames;
    common_util cu = new common_util();


    public Buckets_Adaptor(ArrayList<BucketsObject> bucketNames) {
        this.bucketNames = bucketNames;
    }


    @NonNull
    @Override
    public Rv_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_buckets, parent, false);
        return new Rv_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Rv_ViewHolder holder, final int position) {
        if (bucketNames.get(position).getName() != "Empty List") {
            holder.rl_Buckets.setOnClickListener(this);
            holder.rl_Buckets.setTag(position);
            holder.studentName.setText(bucketNames.get(position).getName());
            holder.studentClass.setText(bucketNames.get(position).getClassName());
            if (bucketNames.get(position).getPhotoUrl() != null) {
                Picasso.get()
                        .load(bucketNames.get(position).getPhotoUrl())
                        .into(holder.studentPhoto, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                holder.pb.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get()
                                        .load(R.drawable.ic_error_outline)
                                        .into(holder.studentPhoto);
                                holder.pb.setVisibility(View.INVISIBLE);
                            }


                        });
            } else {
                Picasso.get()
                        .load(R.drawable.ic_error_outline)
                        .into(holder.studentPhoto);
            }

        } else {
            holder.studentName.setText("Empty List");
            holder.studentClass.setText("");
            holder.studentPhoto.setVisibility(View.INVISIBLE);
            holder.pb.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return bucketNames.size();
    }


    @Override
    public void onClick(View view) {
        RelativeLayout tempRL = (RelativeLayout) view;
        TextView tempUserName = (TextView) tempRL.getChildAt(2);
      /*  Soc_Main.isCurrentlyRunning = "BucketData";//To provide functionality for OnBackPressed;
        Soc_Main.ClearDataRv();// cleaing previous data of adadptor
        BucketData bucketData = new BucketData(Soc_Main.context, tempUserName.getText().toString());*/
        Intent i = new Intent(Soc_Main.context, MyBucket.class);
        i.putExtra("subject", Buckets.subjectName);
        i.putExtra("year", Buckets.yearSelected);
        i.putExtra("username",bucketNames.get((Integer) tempRL.getTag()).getID());
        i.putExtra("type", "otherBucket");
        Soc_Main.context.startActivity(i);

    }


    class Rv_ViewHolder extends RecyclerView.ViewHolder {
        TextView studentName;
        TextView studentClass;
        ImageView studentPhoto;
        RelativeLayout rl_Buckets;
        ProgressBar pb;


        public Rv_ViewHolder(View itemView) {
            super(itemView);
            studentName = itemView.findViewById(R.id.student_name_soc);
            studentPhoto = itemView.findViewById(R.id.student_imgae_soc);
            rl_Buckets = itemView.findViewById(R.id.rl_buckets_soc);
            pb = itemView.findViewById(R.id.progressBar_buckets);
            studentClass=itemView.findViewById(R.id.class_textView_buckets);

        }

    }

}
