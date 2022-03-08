package com.alvarenstudio.infosaham;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;

import com.alvarenstudio.infosaham.model.User;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.alvarenstudio.infosaham.ui.main.SectionsPagerAdapter;
import com.alvarenstudio.infosaham.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FloatingActionButton fab1, fab2, fabf1;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private FirebaseUser fBaseUser;
    private DatabaseReference reference;
    private int pos;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        fab1 = binding.fab1;
        fab2 = binding.fab2;
        fabf1 = binding.fabf;

        fabf1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pos == 0){
                    startActivity(new Intent(MainActivity.this, FavSahamActivity.class));
                }
                else{
                    startActivity(new Intent(MainActivity.this, FavReksadanaActivity.class));
                }
            }
        });

        fab2.setVisibility(View.INVISIBLE);

        fBaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(fBaseUser != null) {
            reference = FirebaseDatabase.getInstance().getReference("users").child(fBaseUser.getUid());
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);

                    if(user.getIs_admin() != null) {
                        savePreferencesString("is_admin", "yes");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pos = position;
                if(position == 0){
                    fab1.setVisibility(View.VISIBLE);
                    fab2.setVisibility(View.INVISIBLE);
                }
                else{
                    fab1.setVisibility(View.INVISIBLE);
                    fab2.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(fBaseUser == null) {
            toolbar.inflateMenu(R.menu.right_menu);
        }
        else {
            toolbar.inflateMenu(R.menu.right_menu_login);
        }
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.signin){
                    startActivity(new Intent(MainActivity.this, SignInActivity.class));
                }
                else if (item.getItemId() == R.id.chat) {
                    startActivity(new Intent(MainActivity.this, MsgActivity.class));
                }
                else if (item.getItemId() == R.id.signout) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                    finish();
                }

                return false;
            }
        });

        MobileAds.initialize(this, "ca-app-pub-9017780985696575~3750944100");

        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    public FloatingActionButton getFab(int idx){
        if(idx == 1){
            return this.fab1;
        }
        else{
            return this.fab2;
        }
    }

    private void savePreferencesString(String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }
}