package com.uk.cmo.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.uk.cmo.Adapters.ViewPagerAdapter;
import com.uk.cmo.R;

public class MainScreenActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        if(!MainActivity.called){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            MainActivity.called=true;
        }
        firebaseAuth=FirebaseAuth.getInstance();
        tabLayout=findViewById(R.id.tab_layout);
        viewPager=findViewById(R.id.view_pager);
        viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_screen_options,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out:
                firebaseAuth.signOut();
                Intent sign_in_Intent=new Intent(MainScreenActivity.this,MainActivity.class);
                startActivity(sign_in_Intent);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
