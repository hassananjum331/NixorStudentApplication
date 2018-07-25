package com.btb.nixorstudentapplication.Sharks_on_cloud.Navigation_Classes;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.btb.nixorstudentapplication.Misc.common_util;
import com.btb.nixorstudentapplication.R;
import com.btb.nixorstudentapplication.Sharks_on_cloud.Adaptors.Buckets_Adaptor;
import com.btb.nixorstudentapplication.Sharks_on_cloud.MyBucket;
import com.btb.nixorstudentapplication.Sharks_on_cloud.Objects.BucketsObject;
import com.btb.nixorstudentapplication.Sharks_on_cloud.Soc_Main;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;


import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import info.hoang8f.android.segmented.SegmentedGroup;

public class Buckets implements View.OnClickListener {
    private static ArrayList<String> myClassBuckets;
    private static ArrayList<BucketsObject> allBuckets;
    private static Buckets_Adaptor bucketsAdaptor;
    private static BucketsObject bucketsObject;
    public static SegmentedGroup bucketsButtons;// so that BucketData class can turn on/off these buttons.
    private Button myClasses;
    private Button mostRecent;
    private Button topRated;
    public static RelativeLayout my_Bucket;//// so that BucketData class can turn on/off this.
    public static String subjectName;// need to access it in BucketDclass
    Activity context;
    private Boolean isInitialData;

    common_util cu = new common_util();

    public Buckets(Activity context, View v, String subjectName) {
        Soc_Main.ShowLoading();
        this.context = context;
        RemovePreviousButtons(v);
        this.subjectName = subjectName;
        GetAllBuckets(v);
        isInitialData = true;

    }


    private static void AddPreviousButtons(View v) {
        Subjects_homescreen.subjectButtons.setVisibility(View.VISIBLE);
        if (bucketsButtons != null && my_Bucket != null) {
            bucketsButtons.setVisibility(View.GONE);
            my_Bucket.setVisibility(View.GONE);
        }
    }

    private void RemovePreviousButtons(View v) {
        Subjects_homescreen.subjectButtons = v.findViewById(R.id.segmented_soc_subjects);
        Subjects_homescreen.subjectButtons.setVisibility(View.GONE);
    }

    private void initializeButtons(View v) {
        myClasses = v.findViewById(R.id.my_classes_soc);
        mostRecent = v.findViewById(R.id.most_recent_soc);
        topRated = v.findViewById(R.id.top_rated_soc);
        bucketsButtons = v.findViewById(R.id.segmented_soc_classes);
        my_Bucket = v.findViewById(R.id.my_bucket_layout);
        bucketsButtons.setVisibility(View.VISIBLE);
        my_Bucket.setVisibility(View.VISIBLE);
        my_Bucket.setOnClickListener(this);
        myClasses.setOnClickListener(this);
        mostRecent.setOnClickListener(this);
        topRated.setOnClickListener(this);
    }


    //MARK: It gets current Students's classes only
    private void GetAllBuckets(final View v) {
        allBuckets = new ArrayList<>();

        Soc_Main.socRoot.document(Subjects_homescreen.button_Selected).collection("Subjects").document(subjectName)
                .collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable final QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (queryDocumentSnapshots.isEmpty() && queryDocumentSnapshots != null) {
                    bucketsObject = new BucketsObject();
                    bucketsObject.setPhotoUrl(null);
                    bucketsObject.setName("Empty List");
                    allBuckets.add(0, bucketsObject);

                    if (Soc_Main.isCurrentlyRunning.equals("Buckets")) {
                        initializeAdaptorAllBuckets(isInitialData);
                        initializeButtons(v);
                        isInitialData = false;
                    }

                } else {
                    if (e == null) {
                        int i = 0;
                        genericGetData(allBuckets, queryDocumentSnapshots.getDocumentChanges(), isInitialData, v, i);

                    } else {
                        cu.ToasterLong(context, "Error retrieving data form Server");
                    }
                }

            }
        });
    }


    //Orrr you could use remote config for this, Because kaafi rare hoga ke update hoon all subjects and if you ever want to tou you can use remote config. Check docs for
    //remote config. Alright best
    //MARK: It gets AS all classes.


    //BucketData calls this methoid to implement onBackPressed feature
    public static void initializeAdaptorAllBuckets(Boolean isInitialData) {
        Soc_Main.HideLoading();
        if (isInitialData) {
            bucketsAdaptor = new Buckets_Adaptor(allBuckets);
            Soc_Main.setAdaptor_Generic(bucketsAdaptor);
        } else {
            bucketsAdaptor.notifyDataSetChanged();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // case R.id.my_classes_soc:
            //   initializeAdaptorAllBuckets();//TODO: Change this
            // break;
            case R.id.my_bucket_layout:
                Intent i = new Intent(Soc_Main.context, MyBucket.class);
                i.putExtra("subject", subjectName);
                Soc_Main.context.startActivity(i);
        }
    }

    //MARK: Main acitivty calls this
    public static void OnBackPressed(View v) {
        Soc_Main.isCurrentlyRunning = "Subjects_homescreen";//Providing back functionality for OnBackPressed in Soc_Main class(mainactvity).
        AddPreviousButtons(v);
        Subjects_homescreen.initializeAdaptorMySubjects();
        return;
    }

    //it compares all data in allbuckets array with the curent document that has been changed and then accordingly if it exists/not exists in allbuckets
    //it adds that result to temparray
    //positon 0 holds true or false(exists or not exists)
    //position 1 holds the position of item in allbuckets only if the docuemnt exists in allbuckets
    //otherwise position 1 hold -1(indicating not exists so no available position in allbuckets array)
    public static ArrayList<String> containsData(ArrayList<BucketsObject> bucketsObjects, String id) {
        ArrayList<String> arr = new ArrayList<>();
        Log.i("AFSAF", id);
        for (int i = 0; i < bucketsObjects.size(); i++) {
            if (bucketsObjects.get(i).getName().equals(id)) {
                arr.add(0, "true");
                arr.add(1, String.valueOf(i));
                return arr;
            }
        }
        arr.add(0, "false");
        arr.add(1, String.valueOf(-1));//-1 means false(just an additional way to know if it is false)
        return arr;
    }


    public void handleNonInitialData(final List<DocumentChange> documentChanges, final int I, String photoUrl) {
        //checking data change and dealing with it accordingly.
        String id = documentChanges.get(I).getDocument().getId();
        ArrayList<String> temparray = new ArrayList();
        temparray = containsData(allBuckets, id);//checking if data already exists in allBuckets array or not and storing the result in temparray

        //  if (containsData(allBuckets,"Empty List")) {
        //    allBuckets.clear();  TODO: WHAT IF EVERYBODY REMOVE THEIR BUCKET
        //}
        switch (documentChanges.get(I).getType()) {
            //checking whther added or removed and then using temparray to add if it does not exist or remove if it exists already
            case ADDED:
                if (temparray.get(0).equals("false")) {
                    bucketsObject.setName(id);
                    bucketsObject.setPhotoUrl(photoUrl);
                    allBuckets.add(bucketsObject);
                }
                break;
            case REMOVED:
                if (temparray.get(0).equals("true")) {
                    int removeIndex = Integer.valueOf(temparray.get(1));
                    allBuckets.remove(removeIndex);
                    Log.i("ASD", "REMOVING");
                }
                break;
        }
    }


    public void genericGetData(final ArrayList<BucketsObject> allBuckets, final List<DocumentChange> documentChanges, final boolean initialdata, final View v, int i) {
        if (Soc_Main.isCurrentlyRunning.equals("Buckets")) {
            bucketsObject = new BucketsObject();
            final int I = i;//just a temp final variable so i can access it in inner class methods
            //TODO:CHANGE SOURCE CACHE.
            Soc_Main.usersRoot.document(documentChanges.get(i).getDocument().getId()).get(Source.CACHE).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    String photoUrl;
                    String id=documentChanges.get(I).getDocument().getId();
                    if (task.getResult().get("photourl") != null) {
                        photoUrl = task.getResult().get("photourl").toString();
                    } else {
                        photoUrl = null;
                    }
                    //if data is initial add all of it without any check as there will be no duplicates
                    if (initialdata && containsData(allBuckets,id).get(0).equals("false")) {
                        bucketsObject.setName(id);
                        bucketsObject.setPhotoUrl(photoUrl);
                        allBuckets.add(bucketsObject);
                    } else {
                        //handlig data if it is not initial
                        handleNonInitialData(documentChanges, I, photoUrl);
                    }


//simple check that whther this class is running and the loop is completed then load the adadptor with this new data.
                    //this prevents for eg 5 items changed so adadptor will refresh 5 times(inefficient)
                    if (Soc_Main.isCurrentlyRunning.equals("Buckets") && I == (documentChanges.size() - 1)) {
                        initializeAdaptorAllBuckets(isInitialData);
                        initializeButtons(v);
                        isInitialData = false;
//simple check that if loop not complete call function again(recursive function, very useful :p)
                    } else if (I < documentChanges.size()) {
                        int index = I;
                        index += 1;
                        genericGetData(allBuckets, documentChanges, initialdata, v, index);
                    }

                }
            });
        }
    }
}








