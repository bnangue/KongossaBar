package com.bricenangue.nextgeneration.kongossabar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class MainPage extends AppCompatActivity {

    private String regionName;
    private RecyclerView recyclerViewPublication;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ProgressDialog progressBar;
    private DatabaseReference root;
    private UserLocalStore userLocalStore;
    private MenuItem item;
    private LinearLayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth=FirebaseAuth.getInstance();
        if(auth!=null){
            user = auth.getCurrentUser();
        }

        userLocalStore=new UserLocalStore(this);
        root= FirebaseDatabase.getInstance().getReference();

         recyclerViewPublication= (RecyclerView) findViewById(R.id.list_of_publication);

        mLayoutManager = new LinearLayoutManager(this);

        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
       // recyclerViewPublication.setHasFixedSize(true);
        recyclerViewPublication.setLayoutManager(mLayoutManager);

        regionName=userLocalStore.getUserLocation();

        setTitle(regionName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_page,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();

        if(id==R.id.action_add_post){
            startActivity(new Intent(this, CreateNewPostActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
           return true;
        }else if (id==R.id.action_view_Karma){
            this.item=item;
            item.setTitle(String.valueOf(userLocalStore.getUserKarma()));

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();

        final DatabaseReference reference= root.child(regionName);
        FirebaseRecyclerAdapter<Publication,PublicationViewHolder> adapter=
                new FirebaseRecyclerAdapter<Publication, PublicationViewHolder>(
                        Publication.class,
                        R.layout.itemview_publication,
                        PublicationViewHolder.class,
                        reference
                ) {
                    @Override
                    protected void populateViewHolder(final PublicationViewHolder viewHolder, final Publication model, int position) {

                        if(model.getMedia()!=null && !model.getMedia().isEmpty()){
                            viewHolder.imageViewOfpublication.setVisibility(View.VISIBLE);

                            Picasso.with(getApplicationContext()).load(model.getMedia()).networkPolicy(NetworkPolicy.OFFLINE)
                                    .fit().centerCrop()
                            .into(viewHolder.imageViewOfpublication, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                    Picasso.with(getApplicationContext()).load(model.getMedia())
                                            .fit().centerCrop().into(viewHolder.imageViewOfpublication);
                                }
                            });
                        }
                        viewHolder.mylocation.setText(model.getLocation());
                        viewHolder.publicationtext.setText(model.getContaint());

                        viewHolder.thumbDown.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onStarClickeddown(reference.child(model.getFirebaseUniqueid()));
                                viewHolder.thumbUp.setEnabled(false);
                                viewHolder.thumbDown.setEnabled(false);
                                /**
                                long rating = model.getRating();
                                rating = rating - 1;
                                model.setRating(rating);
                                reference.child(model.getFirebaseUniqueid()).setValue(model);
                                **/
                            }
                        });

                        viewHolder.thumbUp.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onStarClickedup(reference.child(model.getFirebaseUniqueid()));
                                viewHolder.thumbUp.setEnabled(false);
                                viewHolder.thumbDown.setEnabled(false);
                                /**
                                long rating = model.getRating();
                                rating = rating + 1;
                                model.setRating(rating);
                                reference.child(model.getFirebaseUniqueid()).setValue(model);
                                **/
                            }
                        });

                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(MainPage.this,ViewCommentsActivity.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        .putExtra("uniqueid",model.getFirebaseUniqueid()));
                            }
                        });

                        viewHolder.numberofLikes.setText(String.valueOf(model.getRating()));
                        if(model.getCommentSet()!=null){
                            viewHolder.numberofCommments.setText(String.valueOf(model.getCommentSet().size()));
                        }else {
                            viewHolder.numberofCommments.setText(String.valueOf(0));

                        }
                        viewHolder.comments.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(MainPage.this,ViewCommentsActivity.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .putExtra("uniqueid",model.getFirebaseUniqueid()));
                            }
                        });
                        Date date = new Date(model.getTime());
                        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                        String dateFormatted = formatter.format(date);
                        viewHolder.time.setText(dateFormatted);

                    }
                };

        recyclerViewPublication.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(progressBar.isShowing()){
            progressBar.dismiss();
        }
    }

    private static class PublicationViewHolder extends RecyclerView.ViewHolder{
        ImageButton thumbUp,thumbDown,comments;
        TextView publicationtext, time, numberofCommments, numberofLikes, mylocation;
        private View view;
        private ImageView imageViewOfpublication;


        public PublicationViewHolder(View itemView) {
            super(itemView);
            view=itemView;
            imageViewOfpublication=(ImageView)itemView.findViewById(R.id.imageViewpicturefromPublication);
            thumbUp=(ImageButton) itemView.findViewById(R.id.imagebuttonThumbUP_viewComment);
            thumbDown=(ImageButton) itemView.findViewById(R.id.imagebuttonThumbDOWN_viewComment);
            comments=(ImageButton) itemView.findViewById(R.id.imageButtonOpencomments);

            publicationtext=(TextView) itemView.findViewById(R.id.textViewTpublicationtext_viewComment);
            time=(TextView) itemView.findViewById(R.id.textViewtime_viewComment);
            numberofCommments=(TextView) itemView.findViewById(R.id.textViewnumberofComments);
            numberofLikes=(TextView) itemView.findViewById(R.id.textViewnumberofLikes_viewComments);
            mylocation=(TextView) itemView.findViewById(R.id.textViewmylocation);


        }
    }
    private void onStarClickeddown(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Publication p = mutableData.getValue(Publication.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                DatabaseReference ref =root.child(ConfigApp.FIREBASE_APP_URL_USERS)
                        .child(user.getUid()).child(ConfigApp.FIREBASE_APP_URL_USERS_KARMA);

                if (p.getRater()!=null && !p.getRater().containsKey(auth.getCurrentUser().getUid())
                        ) {
                    // Unstar the post and remove self from stars
                    long rating = p.getRating();
                    rating = rating - 1;
                    p.setRating(rating);

                    HashMap<String,String> map=p.getRater();

                    map.put(auth.getCurrentUser().getUid(),auth.getCurrentUser().getUid());
                    p.setRater(map);
                    if(p.getCreator().equals(user.getUid())){
                        userLocalStore.reducekarma();
                        ref.setValue(userLocalStore.getUserKarma());
                    }

                }else if (p.getRater()!=null && p.getRater().containsKey(auth.getCurrentUser().getUid())){

                }else if (p.getRater()==null){
                    HashMap<String,String> map=new HashMap<String, String>();
                    long rating = p.getRating();
                    rating = rating - 1;
                    p.setRating(rating);

                    map.put(auth.getCurrentUser().getUid(),auth.getCurrentUser().getUid());
                    p.setRater(map);
                    if(p.getCreator().equals(user.getUid())){
                        userLocalStore.reducekarma();
                        ref.setValue(userLocalStore.getUserKarma());
                    }

                }

                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
               // Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    private void onStarClickedup(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Publication p = mutableData.getValue(Publication.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                DatabaseReference ref =root.child(ConfigApp.FIREBASE_APP_URL_USERS)
                        .child(user.getUid()).child(ConfigApp.FIREBASE_APP_URL_USERS_KARMA);
                if (p.getRater()!=null && !p.getRater().containsKey(auth.getCurrentUser().getUid()) ) {
                    // Unstar the post and remove self from stars
                    long rating = p.getRating();
                    rating = rating + 1;
                    p.setRating(rating);

                    HashMap<String,String> map=p.getRater();

                    map.put(auth.getCurrentUser().getUid(),auth.getCurrentUser().getUid());
                    p.setRater(map);
                    if(p.getCreator().equals(user.getUid())){
                        userLocalStore.addkarma();
                        ref.setValue(userLocalStore.getUserKarma());
                    }

                }else if (p.getRater()!=null && p.getRater().containsKey(auth.getCurrentUser().getUid())){


                }else {
                    HashMap<String,String> map=new HashMap<String, String>();
                    long rating = p.getRating();
                    rating = rating + 1;
                    p.setRating(rating);

                    map.put(auth.getCurrentUser().getUid(),auth.getCurrentUser().getUid());
                    p.setRater(map);
                    if(p.getCreator().equals(user.getUid())){
                        userLocalStore.addkarma();
                        ref.setValue(userLocalStore.getUserKarma());
                    }

                }

                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                // Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    @Override
    public void onBackPressed() {
        this.finishAffinity();
        System.exit(0);

    }
}
