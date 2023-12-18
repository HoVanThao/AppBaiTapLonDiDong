package com.example.navigationbottom.adaper;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.navigationbottom.R;

import com.example.navigationbottom.activity.AddPostActivity;
import com.example.navigationbottom.activity.ChatActivity;
import com.example.navigationbottom.activity.ThereProfileActivity;
import com.example.navigationbottom.model.User;
import com.squareup.picasso.Picasso;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;


public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> implements Filterable {

    public static ArrayList<User> users;
    public static ArrayList<User> usersCopy;
    private Context mContext;

    public UsersAdapter(ArrayList<User> users, Context mContext) {
        this.users = users;
        this.usersCopy = users;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public UsersAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_item, parent, false);

        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        if(user == null){
            return;
        }

        try{
            Glide.with(holder.itemView.getContext())
                    .load(user.getImage() != null ? user.getImage() : R.drawable.ic_action_face)
                    .into(holder.ivItem);
        }catch (Exception e){
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.ic_action_face)
                    .into(holder.ivItem);
        }

        holder.tvName.setText(user.getName());
        holder.tvEmail.setText(user.getEmail());
        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomAlertDialog(Gravity.CENTER, user.getUid());
            }
        });

    }

    private void showCustomAlertDialog(int gravity, String i) {
        Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_items_user);
        Window window = dialog.getWindow();
        if(window == null){
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);


        AppCompatButton appCompatButtonProfile = dialog.findViewById(R.id.btn_digProfile_userFragment);
        AppCompatButton appCompatButtonChat = dialog.findViewById(R.id.btn_digChat_userFragment);

        appCompatButtonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ThereProfileActivity.class);
                intent.putExtra("uid", i);
                mContext.startActivity(intent);
            }
        });

        appCompatButtonChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("hisUid", i);
                mContext.startActivity(intent);
            }
        });

        dialog.show();
    }

    @Override
    public int getItemCount() {
        if(users != null){
            return users.size();
        }
        return 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String input = charSequence.toString().toLowerCase();
                List<User> filteredUsers = new ArrayList<>();


                if (input.isEmpty()) {
                    filteredUsers.addAll(usersCopy);
                } else {
                    for (User u : usersCopy) {
                        String normalizedNoiDung = removeAccents(u.getEmail().toLowerCase());
                        String normalizedSearchText = removeAccents(input).toLowerCase();
                        if (normalizedNoiDung.contains(normalizedSearchText)) {
                            filteredUsers.add(u);
                        }
                    }
                }


                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredUsers;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                users = new ArrayList<>();
                users.addAll((List<User>) results.values);
                notifyDataSetChanged();

            }
        };
    }

    // Hàm để loại bỏ dấu tiếng Việt
    public String removeAccents(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return normalized;
    }



    public class UserViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivItem;
        private TextView tvName, tvEmail;
        private RelativeLayout layoutItem;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutItem = itemView.findViewById(R.id.layout_item);
            ivItem = itemView.findViewById(R.id.iv_itemUser);
            tvEmail = itemView.findViewById(R.id.tv_emailItemUser);
            tvName = itemView.findViewById(R.id.tv_nameItemUser);
        }
    }
}
