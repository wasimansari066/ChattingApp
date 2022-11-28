package com.example.chatingapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatingapp.Models.MessageModel;
import com.example.chatingapp.R;
import com.example.chatingapp.databinding.SampleRecieverBinding;
import com.example.chatingapp.databinding.SampleSenderBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatAdapter extends RecyclerView.Adapter {

    ArrayList<MessageModel> messageModels;
    Context context;
    String recId;
    int SENDER_VIEW_TYPE=1;
    int RECEIVER_VIEW_TYPE=2;


    public ChatAdapter(ArrayList<MessageModel> messageModels, Context context) {
        this.messageModels = messageModels;
        this.context = context;
    }

    public ChatAdapter(ArrayList<MessageModel> messageModels, Context context, String recId) {
        this.messageModels = messageModels;
        this.context = context;
        this.recId = recId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==SENDER_VIEW_TYPE){
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender, parent, false);
            return new SenderViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_reciever, parent, false);
            return new RecieverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(messageModels.get(position).getuId().equals(FirebaseAuth.getInstance().getUid()))
        {
            return SENDER_VIEW_TYPE;
        }
        else {
            return RECEIVER_VIEW_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel messageModel = messageModels.get(position);

        if(holder.getClass() == SenderViewHolder.class){
            SenderViewHolder viewHolder = (SenderViewHolder)holder; 
            
            if(messageModel.getMessage().equals("photo")){
                viewHolder.binding.images.setVisibility(View.VISIBLE);
                viewHolder.binding.senderText.setVisibility(View.GONE);
                Picasso.get().load(messageModel.getImageUrl())
                        .placeholder(R.drawable.avatar)
                        .into(viewHolder.binding.images);
            }
        }
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete")
                        .setMessage("Are you sure you want to delete this message")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                String sender = FirebaseAuth.getInstance().getUid() + recId;
                                database.getReference().child("chats").child(sender).child("messages")
                                        .child(messageModel.getMessageId())
                                        .setValue(null);
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                return false;
            }
        });

        if(holder.getClass() == SenderViewHolder.class){
            SenderViewHolder viewHolder = (SenderViewHolder)holder;
            ((SenderViewHolder)holder).binding.senderText.setText(messageModel.getMessage());

            Date currentTime = new Date(messageModel.getTimestamp());
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM hh:mm aa");
            String dateString = formatter.format(currentTime);

            ((SenderViewHolder)holder).binding.senderTime.setText(dateString);
        }
        else{
            RecieverViewHolder viewHolder = (RecieverViewHolder)holder;
            ((RecieverViewHolder)holder).binding.recieverText.setText(messageModel.getMessage());


            Date currentTime = new Date(messageModel.getTimestamp());
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM hh:mm aa");
            String dateString = formatter.format(currentTime);

            ((RecieverViewHolder)holder).binding.receiverTime.setText(dateString);
            if(messageModel.getMessage().equals("photo")){
                viewHolder.binding.images.setVisibility(View.VISIBLE);
                viewHolder.binding.recieverText.setVisibility(View.GONE);
                Picasso.get().load(messageModel.getImageUrl())
                        .placeholder(R.drawable.avatar)
                        .into(viewHolder.binding.images);
            }
        }

    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    public class RecieverViewHolder extends RecyclerView.ViewHolder {

        SampleRecieverBinding binding;

        public RecieverViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SampleRecieverBinding.bind(itemView);
        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder {
        SampleSenderBinding binding;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SampleSenderBinding.bind(itemView);
        }
    }
}
