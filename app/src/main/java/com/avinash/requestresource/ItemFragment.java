package com.avinash.requestresource;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.avinash.requestresource.dummy.DummyContent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.time.*;

/**
 * A fragment representing a list of Items.
 */
public class ItemFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private String TAG = "in Item Fragment";
    RecyclerView recyclerView;
    private ProgressDialog nDialog;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();





    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ItemFragment newInstance(int columnCount) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);




        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;

            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.hideFABbuttons();

            nDialog = new ProgressDialog(getActivity());

            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);


        }
        return view;
    }

    public void getQueryResults() {
        nDialog.show();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("requests")
                .whereEqualTo("userID", currentUser.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task< QuerySnapshot > task) {
                        nDialog.dismiss();
                        if (task.isSuccessful()) {
                            ArrayList < Requests > requests = new ArrayList < Requests > ();
                            for (QueryDocumentSnapshot document: task.getResult()) {
                                Requests request = new Requests();
                                request.setQuantity(Integer.parseInt((document.get("quantity").toString())));
                                request.setCompleted(Boolean.parseBoolean(document.get("completed").toString()));
                                request.setTitle(document.get("title").toString());
                                request.setDescription(document.get("description").toString());
                                request.setUserID(document.get("userID").toString());
                                request.setPriority(document.get("priority").toString());
                                requests.add(request);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            recyclerView.setAdapter(new MyItemRecyclerViewAdapter(requests));
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }





    @Override
    public void onResume() {
        super.onResume();
        MainActivity mainActivity = (MainActivity) getActivity();
        if(mainActivity.getUserRole().equals("staff")) {
            getQueryResults();
        }else{
            getQueryResultsForAdmin();
        }
        mainActivity.hideFABbuttons();
    }

    private void getQueryResultsForAdmin() {
        nDialog.show();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("requests")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task< QuerySnapshot > task) {
                        nDialog.dismiss();
                        if (task.isSuccessful()) {
                            ArrayList < Requests > requests = new ArrayList < Requests > ();
                            for (QueryDocumentSnapshot document: task.getResult()) {
                                Requests request = new Requests();
                                request.setQuantity(Integer.parseInt((document.get("quantity").toString())));
                                request.setCompleted(Boolean.parseBoolean(document.get("completed").toString()));
                                request.setTitle(document.get("title").toString());
                                request.setDescription(document.get("description").toString());
                                request.setUserID(document.get("userID").toString());
                                request.setPriority(document.get("priority").toString());
                                requests.add(request);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            recyclerView.setAdapter(new MyItemRecyclerViewAdapter(requests));
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

//    @Override
//    public void onStop() {
//        super.onStop();
//        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
//    }
}