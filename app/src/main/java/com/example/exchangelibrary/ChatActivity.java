package com.example.exchangelibrary;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ChatAdapter chatAdapter;
    ArrayList<ChatMessage> messagesList;
    String name;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("message");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        FloatingActionButton sendBtn = (FloatingActionButton) findViewById(R.id.send);
        recyclerView = findViewById(R.id.list_of_messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesList = new ArrayList<ChatMessage>();

        displayChatMessages();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            name = user.getDisplayName();
        }

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText) findViewById(R.id.input);
                Log.e("test", "" + input.getText().toString());
                String timeStamp = new SimpleDateFormat("dd-MM-yyyy HH:mm aa").format(Calendar.getInstance().getTime());

                messagesList.add(new ChatMessage(input.getText().toString(), name, timeStamp));
                myRef.setValue(messagesList);

                chatAdapter = new ChatAdapter(ChatActivity.this, messagesList);
                recyclerView.setAdapter(chatAdapter);

                input.setText("");
            }
        });

        ImageView goBack = (ImageView) findViewById(R.id.goBackTo);
        goBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void displayChatMessages() {

        myRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        Toast.makeText(ChatActivity.this, "Successfully Read", Toast.LENGTH_SHORT).show();
                        DataSnapshot dataSnapshot = task.getResult();
                        for (DataSnapshot ALL_USERS: dataSnapshot.getChildren()) {
                            String messageText = ALL_USERS.child("messageText").getValue().toString();
                            String messageTime = ALL_USERS.child("messageTime").getValue().toString();
                            String messageUser = ALL_USERS.child("messageUser").getValue().toString();
                            ChatMessage mUser = new ChatMessage(messageText, messageUser, messageTime);
                            messagesList.add(mUser);
                            chatAdapter = new ChatAdapter(ChatActivity.this, messagesList);
                            recyclerView.setAdapter(chatAdapter);
                        }
                    }
                }
                else{
                    Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}