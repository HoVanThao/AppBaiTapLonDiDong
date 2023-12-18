package com.example.navigationbottom.adaper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.navigationbottom.R;
import com.example.navigationbottom.activity.AddPostActivity;
import com.example.navigationbottom.activity.ChatActivity;
import com.example.navigationbottom.activity.ThereProfileActivity;
import com.example.navigationbottom.activity.UpdatePostActivity;
import com.example.navigationbottom.model.ModelPost;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder>{

    private Context context;
    private List<ModelPost> postList;
    private String myUid;

    public AdapterPosts(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_post, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String pId = postList.get(position).getpId();
        String pTitle = postList.get(position).getpTitle();
        String pDescr = postList.get(position).getpDescr();
        String pImage = postList.get(position).getpImage();
        String pTimeStamp = postList.get(position).getpTime();
        String uid = postList.get(position).getUid();
        String uEmail = postList.get(position).getuEmail();
        String uDp = postList.get(position).getuDp();
        String uName = postList.get(position).getuName();


        //set data
        holder.uName.setText(uName);
        holder.pTitleTv.setText(pTitle);
        holder.pDescriptionTv.setText(pDescr);




        //time
        String dateTime = formatDateTime(pTimeStamp);
        holder.pTimeTv.setText(dateTime);

        if(pImage.equals("noImage")){
            holder.pImageIv.setVisibility(View.GONE);
        }else{

            holder.pImageIv.setVisibility(View.VISIBLE);

            try {
                Picasso.get().load(pImage).into(holder.pImageIv);
            }catch (Exception e){

            }
        }

        try {
                Picasso.get().load(uDp).placeholder(R.drawable.ic_action_face).into(holder.uPictureIv);
        }catch (Exception e){

        }

        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreOptions(holder.moreBtn, uid, myUid, pId, pImage);
            }
        });

        holder.btnLienHeNB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("hisUid", uid);
                context.startActivity(intent);
            }
        });

        holder.profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ThereProfileActivity.class);
                intent.putExtra("uid", uid);
                context.startActivity(intent);
            }
        });

    }

    private void showMoreOptions(ImageButton moreBtn, String uid, String myUid, String pId, String pImage) {

        PopupMenu popupMenu = new PopupMenu(context, moreBtn, Gravity.END);

        if(uid.equals(myUid)){
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Delete");
            popupMenu.getMenu().add(Menu.NONE, 1, 0, "Edit");
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if(id == 0){
                    beginDelete(pId, pImage);
                }
                else if(id == 1){
                    Intent intent = new Intent(context, UpdatePostActivity.class);
                    intent.putExtra("editPostId", pId);
                    context.startActivity(intent);
                }
                return false;
            }
        });

        popupMenu.show();
    }

    private void beginDelete(String pId, String pImage) {

        if(pImage.equals("noImage")){
            deleteWithoutImage(pId);
        }
        else {
            deleteWithImage(pId, pImage);
        }
    }

    private void deleteWithImage(String pId, String pImage) {
        ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Đang xóa...");

        DatabaseReference postsReference = FirebaseDatabase.getInstance().getReference("Posts");

        // Sử dụng phương thức child để điều hướng đến bài đăng cụ thể
        DatabaseReference specificPostReference = postsReference.child(pId);

        // Xóa bài đăng cụ thể
        specificPostReference.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Xóa hình ảnh từ lưu trữ
                        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
                        picRef.delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();
                                        pd.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.dismiss();
                                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
    }

    private void deleteWithoutImage(String pId) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Đang xóa...");

        DatabaseReference postsReference = FirebaseDatabase.getInstance().getReference("Posts");

        // Sử dụng phương thức child để điều hướng đến bài đăng cụ thể
        DatabaseReference specificPostReference = postsReference.child(pId);

        // Xóa bài đăng cụ thể
        specificPostReference.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }



    @Override
    public int getItemCount() {
        return postList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        ImageView pImageIv;
        ShapeableImageView uPictureIv;
        ImageButton moreBtn;
        TextView uName, pTimeTv, pTitleTv, pDescriptionTv;
        AppCompatButton btnLienHeNB;

        LinearLayout profileLayout;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            pImageIv = itemView.findViewById(R.id.iv_pImage_itemPost);
            uPictureIv = itemView.findViewById(R.id.iv_User_itemPost);
            uName = itemView.findViewById(R.id.tv_uName_itemPost);
            pTimeTv = itemView.findViewById(R.id.tv_pTime_itemPost);
            pTitleTv = itemView.findViewById(R.id.tv_pTitle_itemPost);
            pDescriptionTv = itemView.findViewById(R.id.tv_pDescription_itemPost);
            btnLienHeNB = itemView.findViewById(R.id.btn_lienHeNguoiBan_itemPost);
            moreBtn = itemView.findViewById(R.id.btnIv_more_itemPost);
            profileLayout = itemView.findViewById(R.id.profile_layout);

        }
    }

    private String formatDateTime(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) {
            return "time"; // hoặc một giá trị mặc định dựa trên yêu cầu của bạn
        }

        try {
            long timeMillis = Long.parseLong(timestamp);

            // Bạn có thể tùy chỉnh định dạng ngày-giờ dựa trên sở thích của mình
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm aa", Locale.getDefault());

            return sdf.format(new Date(timeMillis));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return "time 2"; // hoặc một giá trị mặc định dựa trên yêu cầu của bạn
        }
    }
}
