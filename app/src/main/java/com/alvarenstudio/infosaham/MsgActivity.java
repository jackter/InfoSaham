package com.alvarenstudio.infosaham;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alvarenstudio.infosaham.adapter.MsgAdapter;
import com.alvarenstudio.infosaham.model.Chat;
import com.alvarenstudio.infosaham.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class MsgActivity extends Activity {
    private String sender;
    private EditText edittextnama;
    private TextView tvUserOnline;
    private ProgressBar ivLoading;
    private RelativeLayout relativeLayoutBottom;
    private DatabaseReference reference;
    private FirebaseUser fBaseUser;

    private ImageButton btn_send, btn_arrow_back;
    private EditText text_send;

    private MsgAdapter msgAdapter;
    private List<Chat> mChat;

    private RecyclerView recyclerView;
    private ImageView img_chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg);

        btn_arrow_back = findViewById(R.id.btn_arrow_back);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);
        tvUserOnline = findViewById(R.id.useronline);
        ivLoading = findViewById(R.id.ivLoading);

        recyclerView = findViewById(R.id.recycler_view);
        relativeLayoutBottom = findViewById(R.id.bottom);

        img_chat = findViewById(R.id.img_chat);
        fBaseUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        text_send.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                if (text_send.hasFocus()) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK){
                        case MotionEvent.ACTION_SCROLL:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            return true;
                    }
                }
                return false;
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = text_send.getText().toString();

                if(isTimeAutomatic(getApplicationContext())) {
                    if (!msg.trim().isEmpty()) {
                        sendMsg(sender, msg);
                    }

                    text_send.setText("");
                }
                else {
                    Toast.makeText(getBaseContext(), "Kami merekomendasikan untuk mengatur tanggal dan waktu Anda ke Otomatis terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_arrow_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        readMsg(0);
        getChatSize();
        showUserOnline();

        sender = String.valueOf(loadPreferences("nama"));

        edittextnama = new EditText(getApplicationContext());
        edittextnama.setText(sender);
        int pos = edittextnama.getText().length();
        edittextnama.setSelection(pos);
        edittextnama.setFocusableInTouchMode(true);
        edittextnama.requestFocus();

        if(fBaseUser == null) {
            relativeLayoutBottom.setVisibility(View.GONE);
        }
        else {
            reference = FirebaseDatabase.getInstance().getReference("users").child(fBaseUser.getUid());
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);

                    if(user.getName() != null && !user.getName().toString().trim().isEmpty()){
                        sender = user.getName().toString();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void showUserOnline(){
        FirebaseFirestore.getInstance().collection("parameter").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getData().get("visible_user_online").toString().equals("show")){
                                    reference.child("user_online").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            long count = dataSnapshot.getChildrenCount();

                                            tvUserOnline.setVisibility(View.VISIBLE);
                                            tvUserOnline.setText(count + " Online");
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        } else {
                            Log.d("FIRESTORE QUERY", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void sendMsg(String sender, String msg){
        reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        if(fBaseUser != null) {
            hashMap.put("userid", fBaseUser.getUid());
        }
        hashMap.put("sender", sender);
        hashMap.put("msg", msg);
        if(fBaseUser != null) {
            hashMap.put("userid", fBaseUser.getUid());
            hashMap.put("timestamp", ServerValue.TIMESTAMP);
        }
        else{
            hashMap.put("timestamp", System.currentTimeMillis());
        }

        reference.child("chats").push().setValue(hashMap);
    }

    private void readMsg(Integer length){
        mChat = new ArrayList<>();
        Query reference;

        ivLoading.setVisibility(View.VISIBLE);

        if(length == 0){
            reference = FirebaseDatabase.getInstance().getReference("chats").limitToLast(100);
        }
        else{
            reference = FirebaseDatabase.getInstance().getReference("chats").limitToLast(length);
        }
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();

                ivLoading.setVisibility(View.GONE);

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);

                    mChat.add(chat);

                    msgAdapter = new MsgAdapter(MsgActivity.this, mChat);
                    recyclerView.setAdapter(msgAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getChatSize(){
        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("chats").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long chatSize = 0;
                chatSize = dataSnapshot.getChildrenCount();

                savePreferences2("chatReadSize", chatSize);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void onClickCalled(String nama) {
        text_send.setText("@" + nama + " " + text_send.getText().toString());
        int pos = text_send.getText().length();
        text_send.setSelection(pos);
        text_send.setFocusableInTouchMode(true);
        text_send.requestFocus();
    }

    public void removeMsg(String userid, Long timestamp, String time, String sender){
        if(userid != null && userid.equals(fBaseUser.getUid())) {
            AlertDialog.Builder alert = new AlertDialog.Builder(MsgActivity.this);
            alert.setTitle("Info Saham");
            alert.setMessage("Apakah anda yakin ingin menghapus pesan ini?");
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    Query postQuery = reference.child("chats").orderByChild("timestamp").equalTo(timestamp);

                    postQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (final DataSnapshot tsSnapshot: dataSnapshot.getChildren()) {
                                Chat mChat = tsSnapshot.getValue(Chat.class);

                                if(mChat.getUserid().equals(userid)){
                                    tsSnapshot.getRef().removeValue();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            });
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            alert.show();
        }
        else if(loadPreferences("is_admin").equals("yes")) {
            AlertDialog.Builder alert = new AlertDialog.Builder(MsgActivity.this);
            alert.setTitle("Info Saham");
            alert.setMessage("Apakah admin yakin ingin menghapus pesan ini?");
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    Query postQuery;
                    if(timestamp != null){
                        postQuery = reference.child("chats").orderByChild("timestamp").equalTo(timestamp);
                    }
                    else{
                        postQuery = reference.child("chats").orderByChild("time").equalTo(time);
                    }

                    postQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (final DataSnapshot tsSnapshot: dataSnapshot.getChildren()) {
                                Chat mChat = tsSnapshot.getValue(Chat.class);

                                if(timestamp != null && sender.equals(mChat.getSender())){
                                    tsSnapshot.getRef().removeValue();
                                }
                                else if(sender.equals(mChat.getSender())){
                                    tsSnapshot.getRef().removeValue();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            });
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            alert.show();
        }
    }

    private String loadPreferences(String key) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        String value = sharedPreferences.getString(key, "");

        return value;
    }

    private void savePreferences2(String key, Long value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addUserOnline(){
        if(fBaseUser != null) {
            TimeZone tz = TimeZone.getTimeZone("GMT+07:00");
            Calendar c = Calendar.getInstance(tz);

            String time = String.format("%02d" , c.get(Calendar.DAY_OF_MONTH)) + "-" + String.format("%02d" , c.get(Calendar.MONTH) + 1) + "-" + String.format("%02d" , c.get(Calendar.YEAR)) + " " + String.format("%02d" , c.get(Calendar.HOUR_OF_DAY))+":"+String.format("%02d" , c.get(Calendar.MINUTE))+":"+String.format("%02d" , c.get(Calendar.SECOND));


            reference = FirebaseDatabase.getInstance().getReference();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("userid", fBaseUser.getUid());
            hashMap.put("time", time);
            hashMap.put("status", "online");
            reference.child("user_online").push().setValue(hashMap);
        }
    }

    public void delUserOnline(){
        if(fBaseUser != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query postQuery = reference.child("user_online").orderByChild("userid").equalTo(fBaseUser.getUid());

            postQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (final DataSnapshot tsSnapshot: dataSnapshot.getChildren()) {
                        tsSnapshot.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    public static boolean isTimeAutomatic(Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.Global.getInt(c.getContentResolver(), Settings.Global.AUTO_TIME, 0) == 1;
        } else {
            return android.provider.Settings.System.getInt(c.getContentResolver(), android.provider.Settings.System.AUTO_TIME, 0) == 1;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        addUserOnline();
    }

    @Override
    protected void onStop() {
        super.onStop();
        delUserOnline();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        delUserOnline();
    }
}