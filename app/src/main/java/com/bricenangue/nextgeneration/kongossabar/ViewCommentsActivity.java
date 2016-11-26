package com.bricenangue.nextgeneration.kongossabar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.model.UriLoader;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ViewCommentsActivity extends AppCompatActivity implements View.OnClickListener {

    private String uniqueId;
    private DatabaseReference root;
    private FirebaseAuth auth;
    private UserLocalStore userLocalStore;
    private RecyclerView recyclerView;
    private ImageButton thumbUp,thumbDown;
    private ProgressDialog progressBar;
    private FirebaseUser user;

    private ImageView imageViewofPublication;
    private String publicationcontaint,location;
    private long numberofLikes;
    private long timeMillis;
    private TextView containtPubTextview, numberofLikeTextview,timeTextview,mylocationTexview;
    private RelativeLayout layoutpublication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_comments);
        Bundle extras=getIntent().getExtras();

        auth =FirebaseAuth.getInstance();
        if(auth!=null){
            user=auth.getCurrentUser();
        }
        userLocalStore=new UserLocalStore(this);
        recyclerView=(RecyclerView)findViewById(R.id.list_of_comments);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        imageViewofPublication=(ImageView) findViewById(R.id.imageViewpicturefromPublicationAndComments);
        layoutpublication=(RelativeLayout)findViewById(R.id.relativeLayout_view_comment_up);
        containtPubTextview=(TextView)findViewById(R.id.textViewTpublicationtext_viewComment);
        timeTextview=(TextView)findViewById(R.id.textViewtime_viewComment);
        numberofLikeTextview=(TextView) findViewById(R.id.textViewnumberofLikes_viewComments);
        mylocationTexview=(TextView) findViewById(R.id.textViewmylocation);


        thumbDown=(ImageButton)findViewById(R.id.imagebuttonThumbDOWN_viewComment) ;
        thumbUp=(ImageButton)findViewById(R.id.imagebuttonThumbUP_viewComment) ;
        thumbDown.setOnClickListener(this);
        thumbUp.setOnClickListener(this);
        root= FirebaseDatabase.getInstance().getReference();
        if(extras!=null){
            uniqueId=extras.getString("uniqueid");
        }
    }

    public void tapToComment(View v){
        startActivity(new Intent(this, CreateNewPostActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        .putExtra("publicationkey",uniqueId));
    }
    @Override
    protected void onStart() {
        super.onStart();

        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();

        final DatabaseReference reference=root.child(userLocalStore.getUserLocation()).child(uniqueId);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.hasChild("media")){
                    // layoutpublication
                    LayoutWrapContentUpdater.wrapContentAgain(layoutpublication,true, RelativeLayout.LayoutParams.MATCH_PARENT,260);

                }else {

                    LayoutWrapContentUpdater.wrapContentAgain(layoutpublication,true, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                }
                if(dataSnapshot.getKey().equals("containt")){
                    publicationcontaint=dataSnapshot.getValue(String.class);
                    containtPubTextview.setText(publicationcontaint);
                }

                if(dataSnapshot.getKey().equals("rating")){
                    numberofLikes=dataSnapshot.getValue(Long.class);
                    numberofLikeTextview.setText(String.valueOf(numberofLikes));
                }
                if(dataSnapshot.getKey().equals("time")){
                    timeMillis=dataSnapshot.getValue(Long.class);
                    Date date = new Date(timeMillis);
                    DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                    String dateFormatted = formatter.format(date);
                    timeTextview.setText(dateFormatted);
                }

                if(dataSnapshot.getKey().equals("location")){
                    location=dataSnapshot.getValue(String.class);
                    mylocationTexview.setText(location);
                }
                if(dataSnapshot.getKey().equals("media")){

                    imageViewofPublication.setVisibility(View.VISIBLE);
                    final String uriofPublication=dataSnapshot.getValue(String.class);
                    Picasso.with(getApplicationContext()).load(uriofPublication).networkPolicy(NetworkPolicy.OFFLINE)
                            .fit().centerInside()
                            .into(imageViewofPublication, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                    Picasso.with(getApplicationContext()).load(uriofPublication)
                                            .fit().centerInside().into(imageViewofPublication);
                                }
                            });
                }
                if(progressBar.isShowing()){
                    progressBar.dismiss();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                // get change on likes

                if(dataSnapshot.getKey().equals("rating")){
                    numberofLikes=dataSnapshot.getValue(Long.class);
                    numberofLikeTextview.setText(String.valueOf(numberofLikes));

                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });

        final DatabaseReference commentRef=reference.child(ConfigApp.FIREBASE_APP_URL_REGION_POST_COMMENTS);
        FirebaseRecyclerAdapter<Comments,CommentsViewHolder> adapter=new FirebaseRecyclerAdapter<Comments, CommentsViewHolder>(
                Comments.class,
                R.layout.itemview_comment,
                CommentsViewHolder.class,
                commentRef

        ) {
            @Override
            protected void populateViewHolder(CommentsViewHolder viewHolder, final Comments model, int position) {

                viewHolder.mylocation.setText(userLocalStore.getUserLocation());
                viewHolder.publicationtext.setText(model.getContaint());

                viewHolder.thumbDown.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onStarClickeddown(commentRef.child(model.getFirebaseUniqueid()));
                        /**
                        long rating = model.getRating();
                        rating = rating - 1;
                        model.setRating(rating);
                        commentRef.child(model.getFirebaseUniqueid()).setValue(model);
                        **/
                    }
                });

                viewHolder.thumbUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onStarClickedup(commentRef.child(model.getFirebaseUniqueid()));
                        /**
                        long rating = model.getRating();
                        rating = rating + 1;
                        model.setRating(rating);
                        commentRef.child(model.getFirebaseUniqueid()).setValue(model);
                        **/
                    }
                });



                viewHolder.numberofLikes.setText(String.valueOf(model.getRating()));

                Date date = new Date(model.getTime());
                DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                String dateFormatted = formatter.format(date);
                viewHolder.time.setText(dateFormatted);


            }
        };


        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        DatabaseReference reference=root.child(userLocalStore.getUserLocation())
                .child(uniqueId).child(ConfigApp.FIREBASE_APP_URL_REGION_POST_RATING);
        DatabaseReference ref=root.child(userLocalStore.getUserLocation()).child(uniqueId);

        switch (id){
            case R.id.imagebuttonThumbDOWN_viewComment:
                /**
                long rating = numberofLikes;
                rating = rating - 1;
                reference.setValue(rating);
                **/
                onStarClickeddown(ref);
                break;
            case R.id.imagebuttonThumbUP_viewComment:
                /**
                 long ratings = numberofLikes;
                ratings = ratings + 1;
                reference.setValue(ratings);
                **/
                onStarClickedup(ref);
                break;

        }
    }

    private static class CommentsViewHolder extends RecyclerView.ViewHolder{
        ImageButton thumbUp,thumbDown;
        TextView publicationtext, time, numberofLikes, mylocation;

        public CommentsViewHolder(View itemView) {
            super(itemView);
            thumbUp=(ImageButton) itemView.findViewById(R.id.imagebuttonThumbUP_viewComment);
            thumbDown=(ImageButton) itemView.findViewById(R.id.imagebuttonThumbDOWN_viewComment);

            publicationtext=(TextView) itemView.findViewById(R.id.textViewTpublicationtext_viewComment);
            time=(TextView) itemView.findViewById(R.id.textViewtime_viewComment);
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
                        userLocalStore.reducekarmaoncomment();
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
                        userLocalStore.reducekarmaoncomment();
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
                    if(p.getFirebaseUniqueid().equals(user.getUid())){
                        userLocalStore.addkarmaoncomment();
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
                    if(p.getFirebaseUniqueid().equals(user.getUid())){
                        userLocalStore.addkarmaoncomment();
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
        startActivity(new Intent(ViewCommentsActivity.this, MainPage.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }


}
