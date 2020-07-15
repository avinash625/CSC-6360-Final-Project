package com.avinash.requestresource;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    boolean isRotate = false;
    private static final String TAG = "In Main Activity ";
    private FloatingActionButton fabMic;
    private FloatingActionButton fabCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        fabMic = (FloatingActionButton) findViewById(R.id.fabMic);
        fabCall = (FloatingActionButton) findViewById(R.id.fabCall);
        ViewAnimation.init((FloatingActionButton) findViewById(R.id.fabCall));
        ViewAnimation.init((FloatingActionButton) findViewById(R.id.fabMic));

        setSupportActionBar(toolbar);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FloatingActionButton fab = findViewById(R.id.fab);

        fabCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Calling", Toast.LENGTH_SHORT).show();
            }
        });

        fabMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "mic", Toast.LENGTH_SHORT).show();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //                        .setAction("Action", null).show();
                isRotate = ViewAnimation.rotateFab(view, !isRotate);
                if (isRotate) {
                    ViewAnimation.showIn(fabCall);
                    ViewAnimation.showIn(fabMic);
                } else {
                    ViewAnimation.showOut(fabCall);
                    ViewAnimation.showOut(fabMic);
                }
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    private void retrieveRequests() {
        getQueryResults();
    }

    public void getQueryResults() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("requests")
                .get()
                .addOnCompleteListener(new OnCompleteListener < QuerySnapshot > () {
                    @Override
                    public void onComplete(@NonNull Task < QuerySnapshot > task) {
                        if (task.isSuccessful()) {
                            ArrayList < Requests > requests = new ArrayList < Requests > ();
                            for (QueryDocumentSnapshot document: task.getResult()) {
                                Requests request = new Requests();
                                request.setQuantity(Integer.parseInt((document.get("quantity").toString())));
                                request.setCompleted(Boolean.parseBoolean(document.get("completed").toString()));
                                request.setTitle(document.get("title").toString());
                                request.setDescription(document.get("description").toString());
                                request.setUserID(document.get("userID").toString());
                                requests.add(request);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            updateUI(requests, "requestslist");
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void updateUI(ArrayList < Requests > requests, String UITag) {
        if (UITag.equals("requestslist")) {
            //set adapter and list out the requests.
        } else {

        }
    }

    private void insertOneRequest() {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map < String, Object > request = new HashMap < > ();
        request.put("quantity", "2");
        request.put("completed", "");
        request.put("userID", user.getUid());
        request.put("title", "testtitle");
        request.put("description", "test description");
        request.put("priority", "Highest");
        request.put("addressedBy", "");

        //        Requests request = new Requests();
        //        request.setTitle("test request");
        //        request.setUserID(user.getUid());
        //        request.setDescription("a test Description");
        //        request.setPriority("Highest");
        //        request.setCompleted(false);
        //        request.setQuantity(3);


        db.collection("requests")
                .add(request)
                .addOnSuccessListener(new OnSuccessListener < DocumentReference > () {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
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
    public void onBackPressed() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingsActivityIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsActivityIntent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) ||
                super.onSupportNavigateUp();
    }
}