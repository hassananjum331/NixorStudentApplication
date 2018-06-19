package com.btb.nixorstudentapplication.BookMyTa;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.btb.nixorstudentapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;


public class Requests_To_Book_Ta_Fragment extends Fragment {

    View view;
    TextView statusText;

    public Requests_To_Book_Ta_Fragment() {
    }

    String TAG = "Requests_To_Book_Ta_Fragment";
    String StudentName = "";
    String Status;

    public void DisplayRequest() {
        CollectionReference cr = FirebaseFirestore.getInstance().collection("BookMyTa");
        Query NameQuery = cr.whereEqualTo("StudentName", "Talha");
        cr.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        Status = document.get("StudentName").toString();

                    }
                }


            }
        });

    }
        @Nullable
        @Override
        public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState){

            view = inflater.inflate(R.layout.requests_to_book_ta, container, false);
           statusText=view.findViewById(R.id.booked_ta_request);

        DisplayRequest();
            //statusText.setText(Status);
          Log.i(TAG,Status);
            return view;

        }
    }








