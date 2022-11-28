package com.example.chatingapp.Fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.chatingapp.Adapter.UsersAdapter;
import com.example.chatingapp.ChatDetailsActivity;
import com.example.chatingapp.MainActivity;
import com.example.chatingapp.Models.Users;
import com.example.chatingapp.NewsActivity;
import com.example.chatingapp.R;
import com.example.chatingapp.WeatherActivity;
import com.example.chatingapp.databinding.ActivityMainBinding;
import com.example.chatingapp.databinding.FragmentChatsBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ChatsFragment extends Fragment {

    public ChatsFragment() {
        // Required empty public constructor
    }

    FragmentChatsBinding binding;
    ArrayList<Users> list = new ArrayList< >();
    FirebaseDatabase database;
    FloatingActionButton floatingActionButton,floatingActionButton1,fab;
    boolean isFABOpen = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatsBinding.inflate(inflater, container, false);

        database = FirebaseDatabase.getInstance();
        UsersAdapter adapter = new UsersAdapter(list, getContext());
        binding.charRecyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.charRecyclerView.setLayoutManager(layoutManager);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFABOpen){
                    showFABMenu();
                }else{
                    closeFABMenu();
                }
            }
        });

        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(), NewsActivity.class);
                startActivity(intent);
                Toast.makeText(requireContext(), "Welcome to news", Toast.LENGTH_SHORT).show();
            }
        });
        binding.floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(), WeatherActivity.class);
                startActivity(intent);
                Toast.makeText(requireContext(), "Welcome to todays weather forecast", Toast.LENGTH_SHORT).show();
            }
        });

        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Users users = dataSnapshot.getValue(Users.class);
                    users.setUserid(dataSnapshot.getKey());
                    if(!users.getUserid().equals(FirebaseAuth.getInstance().getUid())){
                    list.add(users);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return binding.getRoot();
    }

    private void closeFABMenu() {
        isFABOpen=false;
        binding.floatingActionButton.animate().translationY(0);
        binding.floatingActionButton1.animate().translationY(0);

    }

    private void showFABMenu() {
        isFABOpen=true;
        binding.floatingActionButton.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        binding.floatingActionButton1.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
    }
}