package com.bricenangue.nextgeneration.kongossabar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Firstscreen extends AppCompatActivity {

    private Spinner spinnerRegion;
    private String [] regionsNames=new String[11];
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener authStateListener;
    private ProgressDialog progressBar;
    private UserLocalStore userLocalStore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstscreen);

        userLocalStore=new UserLocalStore(this);
        auth=FirebaseAuth.getInstance();

        authStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user=firebaseAuth.getCurrentUser();

            }
        };
        regionsNames[0]="choisissez la region dans laquelle vous vous trouvez";
        regionsNames[1]= getString(R.string.adamawa);
        regionsNames[2]=getString(R.string.center);
        regionsNames[4]=getString(R.string.east);
        regionsNames[5]=getString(R.string.far_north);
        regionsNames[3]=getString(R.string.littoral);
        regionsNames[6]=getString(R.string.north);
        regionsNames[7]=getString(R.string.north_west);
        regionsNames[8]=getString(R.string.west);
        regionsNames[9]=getString(R.string.south);
        regionsNames[10]=getString(R.string.south_west);

        spinnerRegion=(Spinner)findViewById(R.id.spinnerRegionsfirstscreen);
        SpinnerAdapter adapter =new ArrayAdapter<>(this,R.layout.spinnerlayout,regionsNames);
        spinnerRegion.setAdapter(adapter);
    }

    public void OnbuttoncontinuetoMainpage(View view){

        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();
        auth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isComplete()&& task.isSuccessful()){
                    String location =spinnerRegion.getSelectedItem().toString();
                    userLocalStore.storeUserLocation(location);
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    reference.push().child(location);
                   DatabaseReference ref= reference.child(ConfigApp.FIREBASE_APP_URL_USERS).child(user.getUid());
                    People ppl=new People();
                    ppl.setLocation(location);
                    ppl.setFirebaseuniqueId(user.getUid());
                    ppl.setKarma(200);
                    ref.setValue(ppl);
                    userLocalStore.storeUserKarma(200l);
                    startActivity(new Intent(Firstscreen.this, MainPage.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    progressBar.dismiss();
                }else{
                    //authentication failed
                    progressBar.dismiss();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
        if(!userLocalStore.getUserLocation().isEmpty()){
            startActivity(new Intent(Firstscreen.this, MainPage.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(authStateListener!=null){
            auth.removeAuthStateListener(authStateListener);
        }
    }
}
