package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyChatActivity extends AppCompatActivity {
    private ImageView imageViewBack;
    private TextView textViewChat;
    private EditText editTextMessage;
    private FloatingActionButton fab;
    private RecyclerView rvChat;

    String userName,otherName;

    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseUser fUser;

    MessageAdapter adapter;
    List<ModelClass> list;

    String message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_chat);
        imageViewBack = findViewById(R.id.imageViewBack);
        textViewChat = findViewById(R.id.textViewChat);
        editTextMessage = findViewById(R.id.editTextMessage);
        fab = findViewById(R.id.fab);
        rvChat = findViewById(R.id.rvChat);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
//        fUser = FirebaseAuth.getInstance().getCurrentUser();

        rvChat.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();

        userName = getIntent().getStringExtra("userName");
        otherName = getIntent().getStringExtra("otherName");
        textViewChat.setText(otherName);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MyChatActivity.this,MainActivity.class);
                startActivity(i);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                notify = true;
                message = editTextMessage.getText().toString();
                if (!message.equals(""))
                {
                    sendMessage(message);
                    editTextMessage.setText("");
                }
            }
        });
        getMessage();
    }

    public void sendMessage(String message) {
        final String key = reference.child("Messages").child(userName).child(otherName).push().getKey();
        final Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("message", message);
        messageMap.put("from", userName);
        reference.child("Messages").child(userName).child(otherName).child(key)
                .setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            reference.child("Messages").child(otherName).child(userName)
                                    .child(key).setValue(messageMap);
                        }
                    }
                });

    }

    public void getMessage()
    {
        reference.child("Messages").child(userName).child(otherName)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        ModelClass modelClass = snapshot.getValue(ModelClass.class);
                        list.add(modelClass);
                        adapter.notifyDataSetChanged();
                        rvChat.scrollToPosition(list.size()-1);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        adapter = new MessageAdapter(list,userName);
        rvChat.setAdapter(adapter);
    }

}