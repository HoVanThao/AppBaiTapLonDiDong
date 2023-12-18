package com.example.navigationbottom.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.navigationbottom.R;
import com.example.navigationbottom.adaper.AdapterPosts;
import com.example.navigationbottom.model.ModelPost;
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

import java.util.ArrayList;
import java.util.List;

public class ThereProfileActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView tvName, tvPhone, tvEmail;
    private ImageView ivCover;
    private ShapeableImageView ivAvataUser;
    private FirebaseAuth firebaseAuth;
    private RecyclerView postsRecyclerView;
    private List<ModelPost> postList;
    private AdapterPosts adapterPosts;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_there_profile);
        toolbar = findViewById(R.id.toolbar_profile_thereActivity);
        setSupportActionBar(toolbar);

        tvEmail = findViewById(R.id.tv_email_profile_thereActivity);
        tvName = findViewById(R.id.tv_name_profile_thereActivity);
        tvPhone = findViewById(R.id.tv_phone_profile_thereActivity);
        ivAvataUser = findViewById(R.id.iv_avata_user_profile_thereActivity);
        ivCover = findViewById(R.id.iv_anhnen_home_profile_thereActivity);
        
        firebaseAuth = FirebaseAuth.getInstance();

        postsRecyclerView = findViewById(R.id.rv_profile_thereActivity);

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");

        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String name = "" + ds.child("name").getValue();
                    String email = "" + ds.child("email").getValue();
                    String phone = "" + ds.child("phone").getValue();
                    String image = "" + ds.child("image").getValue();
                    String cover = "" + ds.child("cover").getValue();

                    tvName.setText(name);
                    tvEmail.setText(email);
                    tvPhone.setText(phone);

                    try {
                        Picasso.get().load(image).into(ivAvataUser);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.ic_action_name).into(ivAvataUser);
                    }

                    try {
                        Picasso.get().load(cover).into(ivCover);
                    } catch (Exception e) {

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        postList = new ArrayList<>();

        checkUserStatus();

        loadHistPost();
    }

    private void loadHistPost() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        postsRecyclerView.setLayoutManager(linearLayoutManager);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = ref.orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    ModelPost modelPost = ds.getValue(ModelPost.class);
                    String postId = ds.getKey();
                    modelPost.setpId(postId);
                    postList.add(modelPost);
                    adapterPosts = new AdapterPosts(ThereProfileActivity.this, postList);
                    postsRecyclerView.setAdapter(adapterPosts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchHistPost(String s) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    ModelPost mylPost = ds.getValue(ModelPost.class);

                    if(mylPost.getpTitle().toLowerCase().contains(s.toLowerCase()) ||
                            mylPost.getpDescr().toLowerCase().contains(s.toLowerCase())){

                        postList.add(mylPost);

                    }

                    adapterPosts = new AdapterPosts(ThereProfileActivity.this, postList);
                    postsRecyclerView.setAdapter(adapterPosts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){


        }else{
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_there_profile_activity, menu);

        // su ly tim kiem
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.mi_search_there_profile_activity));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!TextUtils.isEmpty(query)){
                    searchHistPost(query);
                }else{
                    loadHistPost();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!TextUtils.isEmpty(newText)){
                    searchHistPost(newText);
                }else{
                    loadHistPost();
                }
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.mi_logout_there_profile_activity) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}