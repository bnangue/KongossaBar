package com.bricenangue.nextgeneration.kongossabar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Picture;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class CreateNewPostActivity extends AppCompatActivity {

    private static final int GALLERY_INTENT = 2;
    private static final int GALLERY_INTENT_CAPTURE = 1;

    private EditText editText;
    private FirebaseAuth auth;
    private DatabaseReference root;
    private UserLocalStore userLocalStore;
    private ProgressDialog progressBar;
    private String publicationkey;
    private ImageButton buttonPostpicture;
    private StorageReference storageRoot;
    private ImageView imagetoupload;
    private Uri imageuri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_post);
        userLocalStore=new UserLocalStore(this);
        editText=(EditText)findViewById(R.id.editText_createpost);
        auth=FirebaseAuth.getInstance();
        root= FirebaseDatabase.getInstance().getReference();
        storageRoot= FirebaseStorage.getInstance().getReference();
        imagetoupload=(ImageView)findViewById(R.id.imageViewpictureTopost);

        buttonPostpicture= (ImageButton)findViewById(R.id.imageButtonpostpicture);
        buttonPostpicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");

                startActivityForResult(intent, GALLERY_INTENT);
            }
        });


        Bundle extras=getIntent().getExtras();
        if(extras!=null && extras.containsKey("publicationkey")){
           publicationkey=extras.getString("publicationkey");
            buttonPostpicture.setEnabled(false);
            buttonPostpicture.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_INTENT && resultCode==RESULT_OK){
            imageuri=data.getData();
            imagetoupload.setImageURI(imageuri);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_new_post,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();

        if(id==R.id.action_post){
            //Post to Firebase
            final String containt= editText.getText().toString();
            progressBar = new ProgressDialog(this);
            progressBar.setCancelable(false);
            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressBar.show();

            if(TextUtils.isEmpty(containt) && imageuri==null){
                editText.setError("write what is on your mind");
                progressBar.dismiss();
            }else {
                DatabaseReference reference;
                if(publicationkey!=null && !publicationkey.isEmpty()){
                    reference=root.child(userLocalStore.getUserLocation())
                            .child(publicationkey).child(ConfigApp.FIREBASE_APP_URL_REGION_POST_COMMENTS);
                    String tempKey=reference.push().getKey();
                    Comments publication =new Comments();
                    publication.setContaint(containt);
                    assert auth!=null;
                    publication.setCreator(auth.getCurrentUser().getUid());
                    publication.setFirebaseUniqueid(tempKey);
                    publication.setTime(System.currentTimeMillis());
                    publication.setLocation(userLocalStore.getUserLocation());


                    reference.child(tempKey).setValue(publication).addOnCompleteListener(CreateNewPostActivity.this,
                            new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        progressBar.dismiss();
                                        finish();
                                    }else {
                                        progressBar.dismiss();
                                    }
                                }
                            });
                }else {
                    if(imageuri!=null){
                        StorageReference filepath=storageRoot.child("Picture").child(auth.getCurrentUser().getUid())
                                .child(imageuri.getLastPathSegment());
                        filepath.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri uri= taskSnapshot.getDownloadUrl();
                                // Picasso.with(CreateNewPostActivity.this).load(uri).fit().centerCrop().into(imagetoupload);
                                DatabaseReference reference=root.child(userLocalStore.getUserLocation());
                                String tempKey=reference.push().getKey();
                                Publication publication =new Publication();
                                publication.setContaint(containt);
                                assert auth!=null;
                                publication.setCreator(auth.getCurrentUser().getUid());
                                publication.setFirebaseUniqueid(tempKey);
                                publication.setTime(System.currentTimeMillis());
                                publication.setLocation(userLocalStore.getUserLocation());
                                publication.setMedia(uri.toString());


                                reference.child(tempKey).setValue(publication).addOnCompleteListener(CreateNewPostActivity.this,
                                        new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    progressBar.dismiss();
                                                    startActivity(new Intent(CreateNewPostActivity.this, MainPage.class)
                                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                                }else {
                                                    progressBar.dismiss();
                                                }
                                            }
                                        });
                            }
                        });
                    }else {
                        reference=root.child(userLocalStore.getUserLocation());
                        String tempKey=reference.push().getKey();
                        Publication publication =new Publication();
                        publication.setContaint(containt);
                        assert auth!=null;
                        publication.setCreator(auth.getCurrentUser().getUid());
                        publication.setFirebaseUniqueid(tempKey);
                        publication.setTime(System.currentTimeMillis());
                        publication.setLocation(userLocalStore.getUserLocation());


                        reference.child(tempKey).setValue(publication).addOnCompleteListener(CreateNewPostActivity.this,
                                new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            progressBar.dismiss();
                                            startActivity(new Intent(CreateNewPostActivity.this, MainPage.class)
                                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                        }else {
                                            progressBar.dismiss();
                                        }
                                        if(task.isComplete()){
                                            if(progressBar!=null && progressBar.isShowing()){
                                                progressBar.dismiss();
                                            }
                                        }
                                    }
                                });
                    }
                }
            }

            if(!new ConfigApp(this).haveNetworkConnection() && progressBar.isShowing()){
                progressBar.dismiss();
                Toast.makeText(getApplicationContext(),"No internet connection. Your publication will be post as soon the connection is rehestablished",Toast.LENGTH_SHORT).show();
                onBackPressed();
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(CreateNewPostActivity.this, MainPage.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

    }
}
