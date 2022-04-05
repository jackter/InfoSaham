package com.alvarenstudio.infosaham.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.alvarenstudio.infosaham.MsgActivity;
import com.alvarenstudio.infosaham.R;
import com.alvarenstudio.infosaham.model.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.viewHolder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Chat> mChats;

    FirebaseUser fbaseUser;

    public MsgAdapter(Context mContext, List<Chat> mChats) {
        this.mContext = mContext;
        this.mChats = mChats;
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        public TextView show_msg;
        public TextView user_msg;
        public LinearLayout id_msg;
        public String name_msg;
        public Button show_more;

        public viewHolder(View itemView) {
            super(itemView);

            show_msg = itemView.findViewById(R.id.show_msg);
            user_msg = itemView.findViewById(R.id.nama_msg);
            id_msg = itemView.findViewById(R.id.id_msg);
            show_more = itemView.findViewById(R.id.btn_show_more);

            fbaseUser = FirebaseAuth.getInstance().getCurrentUser();
        }

    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new viewHolder(view);
        }
        else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new viewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder holder, int position) {
        final Chat chat = mChats.get(position);

        holder.show_msg.setText(chat.getMsg());

        if(chat.getIs_admin() == 1) {
            holder.show_msg.setTextColor(Color.RED);
            holder.user_msg.setTextColor(Color.RED);
        }
        else{
            holder.show_msg.setTextColor(Color.BLACK);
            holder.user_msg.setTextColor(Color.GRAY);
        }

        holder.user_msg.setText(chat.getSender() + " at " + timestampMsgToDT(chat.getTimestamp()));
        holder.name_msg = chat.getSender();

        holder.show_msg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(fbaseUser != null) {
                    ((MsgActivity) mContext).removeMsg(chat.getUserid(), chat.getTimestamp(), chat.getTime(), chat.getSender());
                }
                return false;
            }
        });

        holder.id_msg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(fbaseUser != null) {
                    ((MsgActivity) mContext).removeMsg(chat.getUserid(), chat.getTimestamp(), chat.getTime(), chat.getSender());
                }
                return false;
            }
        });

        holder.user_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MsgActivity) mContext).onClickCalled(holder.name_msg);
            }
        });

        holder.show_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.show_more.getText().toString().equals("View More...")){
                    holder.show_msg.setMaxLines(Integer.MAX_VALUE);
                    holder.show_more.setText("View Less");
                }
                else{
                    holder.show_msg.setMaxLines(5);
                    holder.show_more.setText("View More...");
                }
            }
        });

        holder.show_msg.post(new Runnable() {
            @Override
            public void run() {
                if(holder.show_msg.getLineCount() <= 5){
                    holder.show_more.setVisibility(View.GONE);
                }
                else{
                    holder.show_more.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private String timestampMsgToDT(Long timestamp) {
        long yourmilliseconds = timestamp;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date resultdate = new Date(yourmilliseconds);

        return sdf.format(resultdate).toString();
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    @Override
    public int getItemViewType(int position) {
        fbaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(fbaseUser == null) {
            return MSG_TYPE_LEFT;
        }
        else {
            if(mChats.get(position).getUserid().equals(fbaseUser.getUid())){
                return MSG_TYPE_RIGHT;
            }
            else {
                return MSG_TYPE_LEFT;
            }
        }
    }
}
