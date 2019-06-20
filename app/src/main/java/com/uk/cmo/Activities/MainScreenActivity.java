package com.uk.cmo.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.uk.cmo.Adapters.ViewPagerAdapter;
import com.uk.cmo.Model.CreatedUser;
import com.uk.cmo.R;
import com.uk.cmo.Utility.Constants;

public class MainScreenActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference check_ref;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private android.support.v7.widget.Toolbar toolbar;
    private FloatingActionButton add_post;
    private ViewPagerAdapter viewPagerAdapter;
    private TextView msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.CustomTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        check_ref = FirebaseDatabase.getInstance().getReference(Constants.USERS).child(firebaseAuth.getCurrentUser().getUid());

        check_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CreatedUser user = dataSnapshot.getValue(CreatedUser.class);
                if (user != null) {
                    Log.d("Check",String.valueOf(user.isLegit()));
                    if (!user.isLegit()) {
                        tabLayout.setVisibility(View.GONE);
                        msg.setVisibility(View.VISIBLE);
                        viewPager.setAdapter(null);
                    } else {
                        subscribeToPosts();
                        viewPager.setAdapter(viewPagerAdapter);
                        tabLayout.setVisibility(View.VISIBLE);
                        msg.setVisibility(View.INVISIBLE);
                        initializeToken();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        add_post = findViewById(R.id.add_posts);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        msg = findViewById(R.id.auth_msg);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        tabLayout.setupWithViewPager(viewPager);



        add_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent add_post_intent = new Intent(MainScreenActivity.this, AddPostActivity.class);
                startActivity(add_post_intent);

            }
        });



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.profile:
                startActivity(new Intent(MainScreenActivity.this,MyProfileActivity.class));
                break;

            case R.id.sign_out:
                FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.POSTS);
                firebaseAuth.signOut();
                startActivity(new Intent(MainScreenActivity.this, MainActivity.class));
                finish();

        }

        return super.onOptionsItemSelected(item);
    }
    private void initializeToken() {

        String token= FirebaseInstanceId.getInstance().getToken();
        saveRegistrationTokenToDb(token);

    }

    private void saveRegistrationTokenToDb(String token){

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        if (firebaseUser!=null) {
            Log.d("TAG", "user not null");
            reference.child(Constants.USERS).child(firebaseUser.getUid()).child(Constants.USERS_TOKEN).setValue(token);
        }else
            Log.d("TAG", "user null in method due to some reason");

    }



    private void subscribeToPosts(){

        FirebaseMessaging.getInstance().subscribeToTopic(Constants.POSTS);
        Log.d("Subscribed : ","This guy subscribed to Posts !");

    }



}
