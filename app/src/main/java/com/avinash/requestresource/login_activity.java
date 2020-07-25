package com.avinash.requestresource;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class login_activity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 0;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;

    private static final String TAG = "EmailPassword";

    private View mContentView;
    private View mControlsView;
    private boolean mVisible;
    private EditText username;
    private EditText password;
    private Button login_button;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ProgressDialog nDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_activity);
        final Context context = this;
        mVisible = true;
        mContentView = findViewById(R.id.fullscreen_content);
        username = (EditText) findViewById(R.id.login_activity_username);
        password = (EditText) findViewById(R.id.login_activity_password);
        login_button = (Button) findViewById(R.id.login_activity_button);
        FrameLayout fl= (FrameLayout) findViewById(R.id.login_activity_framelayout);

        fl.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                //left swipe
                moveToRegisterScreen();
                hideSoftKeyboard();
            }

            @Override
            public void onSwipeRight() {
                //right swipe
                super.onSwipeRight();
                hideSoftKeyboard();
            }
        });

        username.setHint("Username(Email)");
        password.setHint("Password");
        mAuth = FirebaseAuth.getInstance();

        login_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                nDialog.show();
                if(validateUserEnteredData(username.getText().toString(), password.getText().toString()) == true){
                    validateUserCrednetials(username.getText().toString(), password.getText().toString());
//                    createUserWithEmail(username.getText().toString(), password.getText().toString());
                }else{
                    Toast.makeText(getApplicationContext(),"Plese enter details!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        nDialog = new ProgressDialog(login_activity.this);
        nDialog.setMessage("Signing you in........");
        nDialog.setTitle("");
        nDialog.setIndeterminate(false);
        nDialog.setCancelable(true);




    }
    protected void hideSoftKeyboard() {
        ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    private void validateUserCrednetials(String username, String password) {
        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "SignInWithEmail:success");
                            user = mAuth.getCurrentUser();
                            getUserDetails(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "SignInWithEmail:failure", task.getException());
                            Toast.makeText(login_activity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void createUserWithEmail(String username, String password){
        mAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            user = mAuth.getCurrentUser();
                            updateUI(user, "usercreation");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            updateUI(null, "usercreation");
                        }

                    }
                });
    }

    private void updateUI(FirebaseUser user, String contextValue) {
        if (contextValue.equals("login")){
            if(user == null){
                loginFailed();
                nDialog.dismiss();
            }else{
                Intent mainActivity = new Intent(this, MainActivity.class);
                startActivity(mainActivity);

            }
        }else if( contextValue.equals("usercreation")){
            if(user == null){
                userCreationFailed();
                nDialog.dismiss();
            }else{
                Intent mainActivity = new Intent(this, MainActivity.class);
                startActivity(mainActivity);
            }
        }

    }

    private void moveToRegisterScreen(){
        Intent registerActivity = new Intent(this, register_activity.class);
        startActivity(registerActivity);
    }

    private void userCreationFailed() {
        Toast.makeText(login_activity.this, "User reation failed. Try again.",
                Toast.LENGTH_LONG).show();
    }

    private void getUserDetails(final FirebaseUser user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").whereEqualTo("userID", user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task< QuerySnapshot > task) {
                        nDialog.dismiss();
                        if (task.isSuccessful()) {
                            ArrayList< Requests > requests = new ArrayList < Requests > ();
                            for (QueryDocumentSnapshot document: task.getResult()) {
                                MainActivity mainActivity = new MainActivity();
                                mainActivity.setUserRole((String)document.get("role"));
                                mainActivity.setUserEmail((String)user.getEmail().toString());
                            }
                            updateUI(user, "login");
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void loginFailed() {
        Toast.makeText(this, "Login Failed. Please check your device internet connection.", Toast.LENGTH_LONG).show();
    }


    public boolean validateUserEnteredData(String username, String password){
        if(username.replace(" ","").equals("") || password.replace(" ", "").equals("")){
            return false;
        }else if(password.length() < 5){
            return false;
        }
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EditText username = (EditText) findViewById(R.id.login_activity_username);
        EditText password = (EditText) findViewById(R.id.login_activity_password);
        username.setText("");
        password.setText("");
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (AUTO_HIDE) {
                        delayedHide(AUTO_HIDE_DELAY_MILLIS);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    view.performClick();
                    break;
                default:
                    break;
            }
            return false;
        }
    };

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };

    private final Handler mHideHandler = new Handler();
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}