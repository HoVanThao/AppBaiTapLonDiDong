package com.example.navigationbottom.adaper;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.navigationbottom.R;
import com.example.navigationbottom.model.ModelChat;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder>{

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    private Context context;
    private List<ModelChat> chatList;
    private String imageUrl;
    FirebaseUser fUser;

    public AdapterChat(Context context, List<ModelChat> chatList, String imageUrl) {
        this.context = context;
        this.chatList = chatList;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, parent, false);
            return new MyHolder(view);
        }
        else{
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, parent, false);
            return new MyHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, @SuppressLint("RecyclerView") final int position) {
        String message = chatList.get(position).getMessage();
        String timeStamp = chatList.get(position).getTimestamp();

        Log.d("truongdeptrai", "Timestamp: " + timeStamp);

        String dateTime = formatDateTime(timeStamp);
        holder.timeTv.setText(dateTime);

        holder.messageTv.setText(message);


        try {
            Picasso.get().load(imageUrl).placeholder(R.drawable.ic_action_face).into(holder.profileIv);
        }catch (Exception e){

        }

        if(position == chatList.size() - 1){
            if(chatList.get(position).isSeen()){
                Log.d("hai", "1");
                holder.isSeenTv.setText("Seen");
            }else {
                Log.d("hai", "1.1");
                holder.isSeenTv.setText("Delivered");
            }
        }else {
            Log.d("hai", "1.2");
            holder.isSeenTv.setVisibility(View.GONE);
        }

        holder.messageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure to delete this messge?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteMessage(position);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.create().show();
            }
        });


    }

    private String formatDateTime(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) {
            return "time";
        }

        try {
            long timeMillis = Long.parseLong(timestamp);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm aa", Locale.getDefault());

            return sdf.format(new Date(timeMillis));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return "time 2";
        }
    }

    private void deleteMessage(int position) {
        String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String msgTimeStamp = chatList.get(position).getTimestamp();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
        Query query = dbRef.orderByChild("timestamp").equalTo(msgTimeStamp);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds : snapshot.getChildren()){

                    if(ds.child("sender").getValue().equals(myUID)){
                        //1
                        //ds.getRef().removeValue();

                        //2
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("message", "This message was delete...");
                        ds.getRef().updateChildren(hashMap);

                        Toast.makeText(context, "Message delete...", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context, "You can delete only your message...", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if(chatList.get(position).getSender().equals(fUser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }
    }

    class MyHolder extends RecyclerView.ViewHolder{
        ShapeableImageView profileIv;
        TextView messageTv, timeTv, isSeenTv;
        LinearLayout messageLayout;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            profileIv = itemView.findViewById(R.id.sv_user_row_chat_left);
            messageTv = itemView.findViewById(R.id.tv_mess_row_chat_left);
            timeTv = itemView.findViewById(R.id.tv_time_row_chat_left);
            isSeenTv = itemView.findViewById(R.id.tv_seen_row_chat_left);
            messageLayout = itemView.findViewById(R.id.message_layout);
        }
    }

}
