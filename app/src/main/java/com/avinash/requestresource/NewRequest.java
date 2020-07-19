package com.avinash.requestresource;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewRequest extends AppCompatActivity {

    private  String TAG ="in new request: ";

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    EditText requestTitle;
    EditText requestDescription;
    EditText quantity;
    Spinner priority;
    RadioButton radio_food, radio_medication, radio_ppe;


    private void insertOneRequest() {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();




        Map< String, Object > request = new HashMap< >();
        request.put("quantity", quantity.getText().toString());
        request.put("requestedOn", "");
        request.put("completed", "");
        request.put("userID", user.getUid());
        request.put("title", requestTitle.getText().toString());
        request.put("description", requestDescription.getText().toString());
        request.put("priority", priority.getSelectedItemId());
        request.put("addressedBy", "");

        db.collection("requests")
                .add(request)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(mainActivity);
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_request);
        Spinner spinner = findViewById(R.id.priority_spinner);
        requestTitle = (EditText) findViewById(R.id.requestTitle);
        requestDescription = (EditText) findViewById(R.id.requestDescription);
        quantity = (EditText) findViewById(R.id.requestQuantity);
        priority = (Spinner) findViewById(R.id.priority_spinner);
        radio_food = (RadioButton) findViewById(R.id.radio_food);
        radio_medication = (RadioButton) findViewById(R.id.radio_medication);
        radio_ppe  = (RadioButton) findViewById(R.id.radio_ppe);

        Button requestButton = (Button)findViewById(R.id.requestButton);
        requestButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                insertOneRequest();
            }
        });
        ArrayList<String> arrayList = new ArrayList<>();
        //replace it with the list of priorities from the website.
        arrayList.add("Normal");
        arrayList.add("Critical");
        arrayList.add("Food time(Lunch/Dinner)");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tutorialsName = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(), "Selected: " + tutorialsName,Toast.LENGTH_LONG).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}