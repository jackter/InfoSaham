package com.alvarenstudio.infosaham;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alvarenstudio.infosaham.model.User;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuCompat;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FloatingActionButton fab1, fab2, fabf;
    private Button menuSort1, menuSort2, menuRefresh1, menuRefresh2;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private FirebaseUser fBaseUser;
    private DatabaseReference reference;
    private int pos;
    private AdView adView;
    private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getFirebase();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        fab1 = binding.fab1;
        fab2 = binding.fab2;
        fabf = binding.fabf;

        fab2.setVisibility(View.INVISIBLE);

        fabf.setOnClickListener(new View.OnClickListener() {
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

        toolbar = findViewById(R.id.toolbar);
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
                    if(fBaseUser != null) {
                        startActivity(new Intent(MainActivity.this, MsgActivity.class));
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Silahkan login terlebih dahulu.", Toast.LENGTH_LONG).show();
                    }
                }
                else if (item.getItemId() == R.id.signout) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                    finish();
                }
                else if (item.getItemId() == R.id.catatan) {
                    if(pos == 0) {
                        if(fBaseUser != null) {
                            startActivity(new Intent(MainActivity.this, CatatanSahamActivity.class));
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Silahkan login terlebih dahulu.", Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Untuk sementara catatan reksadana belum tersedia", Toast.LENGTH_LONG).show();
                    }
                }
                else if (item.getItemId() == R.id.refresh){
                    if(pos == 0) {
                        menuRefresh1.performClick();
                    }
                    else {
                        menuRefresh2.performClick();
                    }
                }
                else if (item.getItemId() == R.id.sort){
                    if(pos == 0) {
                        menuSort1.performClick();
                    }
                    else {
                        menuSort2.performClick();
                    }
                }
                else if (item.getItemId() == R.id.rate){
                    String appPackageName = getPackageName();
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                }
                else if (item.getItemId() == R.id.share){
                    String link = "http://play.google.com/store/apps/details?id=" + getPackageName();
                    Intent myIntent = new Intent(Intent.ACTION_SEND);
                    myIntent.setType("text/plain");
                    String shareBody = "Apakah anda ingin menginstall aplikasi Info Saham? " + link;
                    String shareSub = "Share Apps";
                    myIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                    myIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(myIntent, "Share using"));
                }
                return false;
            }
        });

        menuRefresh1 = findViewById(R.id.menuRefresh1);
        menuSort1 = findViewById(R.id.menuSort1);
        menuRefresh2 = findViewById(R.id.menuRefresh2);
        menuSort2 = findViewById(R.id.menuSort2);

        MobileAds.initialize(this, "ca-app-pub-9017780985696575~3750944100");

        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        WebViewHandler webViewHandler;
        webViewHandler = new WebViewHandler(getApplicationContext());
        webViewHandler.getCalender();
    }

    public FloatingActionButton getFab(int idx){
        if(idx == 1){
            return this.fab1;
        }
        else{
            return this.fab2;
        }
    }

    public Button getMenuRefresh(int idx) {
        if(idx == 1){
            return this.menuRefresh1;
        }
        else{
            return this.menuRefresh2;
        }
    }

    public Button getMenuSort(int idx) {
        if(idx == 1){
            return this.menuSort1;
        }
        else{
            return this.menuSort2;
        }
    }

    private void savePreferencesString(String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private void savePreferencesInt(String key, int value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void getFirebase() {
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
                    else{
                        savePreferencesString("is_admin", "no");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        FirebaseFirestore.getInstance().collection("parameter").get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            savePreferencesString("visible_user_online", document.getData().get("visible_user_online").toString());
                            savePreferencesInt("show_ads", Integer.valueOf(document.get("show_ads").toString()));

                            if(Integer.valueOf(document.get("show_ads").toString()) == 1) {
                                adView.setVisibility(View.VISIBLE);
                            }
                            else{
                                adView.setVisibility(View.INVISIBLE);
                            }
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
    }
}