package com.example.chatingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.chatingapp.Adapter.ChatAdapter;
import com.example.chatingapp.Models.MessageModel;
import com.example.chatingapp.databinding.ActivityChatDetailsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ChatDetailsActivity extends AppCompatActivity {

    ActivityChatDetailsBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        getSupportActionBar().hide();

        final String senderId = auth.getUid();
        String receiverId = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("userName");
        String profilePic = getIntent().getStringExtra("profilePic");

        binding.userName.setText(userName);
        Picasso.get().load(profilePic).placeholder(R.drawable.avatar).into(binding.profileImage);

        binding.backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatDetailsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });



        final ArrayList<MessageModel> messageModels = new ArrayList<>();
        final ChatAdapter chatAdapter = new ChatAdapter(messageModels, this, receiverId);
        binding.charRecyclerView.setAdapter(chatAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.charRecyclerView.setLayoutManager(layoutManager);

        final String senderRoom = senderId + receiverId;
        final String recieverRoom = receiverId + senderId;

        database.getReference().child("chats")
                .child(senderRoom)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                messageModels.clear();
                                for (DataSnapshot snapshot1 : snapshot.getChildren())
                                {
                                    MessageModel model = snapshot1.getValue(MessageModel.class);
                                    model.setMessageId(snapshot1.getKey());
                                    messageModels.add(model);
                                }
                                chatAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.etMessage.getText().toString().isEmpty()){
                    binding.etMessage.setError("message is empty");
                    return;
                }
                String message = binding.etMessage.getText().toString();
                final MessageModel model = new MessageModel(senderId, message);
                model.setTimestamp(new Date().getTime());
                binding.etMessage.setText("");

                database.getReference().child("chats")
                        .child(senderRoom)
                        .push()
                        .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                database.getReference().child("chats")
                                        .child(recieverRoom)
                                        .push()
                                        .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                            }
                                        });
                            }
                        });
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}