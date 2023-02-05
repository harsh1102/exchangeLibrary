package com.example.exchangelibrary;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class AddPost extends AppCompatActivity {
    String name, userId;
    Spinner spinner;
    String[] statusList = {"In hand","In-progress", "Completed", "Open to exchange", "Swapped"};
    ImageView imageView;
    ArrayList<PostFeed> postFeedsList = new ArrayList<>();
    Upload uploadedFile;

    StorageTask mUploadTask;
    EditText title,author,genre,review,summary,location;
    RatingBar rating;
    Uri imageUri;

    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("postFeeds");
    StorageReference storageRef = FirebaseStorage.getInstance().getReference("uploads");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            name = user.getDisplayName();
            userId = user.getUid();
        }

        spinner= findViewById(R.id.spin);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(AddPost.this, android.R.layout.simple_list_item_1, statusList);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        imageView = findViewById(R.id.bookCover);
        title =  findViewById(R.id.title);
        author = findViewById(R.id.author);
        genre = findViewById(R.id.genre);
        review = findViewById(R.id.review);
        summary = findViewById(R.id.summary);
        location = findViewById(R.id.location);
        rating = findViewById(R.id.ratingbar);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String value=adapterView.getItemAtPosition(i).toString();
//                Toast.makeText(AddPost.this,value, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //get posts from database
        getPostFeeds();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContent.launch("image/*");
            }
        });

        Button postBtn = (Button) findViewById(R.id.post);
        postBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if( TextUtils.isEmpty(title.getText()) || TextUtils.isEmpty(author.getText()) || TextUtils.isEmpty(genre.getText()) || TextUtils.isEmpty(review.getText()) || TextUtils.isEmpty(summary.getText()) || TextUtils.isEmpty(location.getText())) {
                    Toast.makeText(getApplication(),"Field should not be empty", Toast.LENGTH_LONG).show();
                }
                else{
                    //Add image to storage
                    uploadImage();
                }
            }
        });

        ImageView goBack = (ImageView) findViewById(R.id.goBackTo);
        goBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(AddPost.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

}

    private void getPostFeeds() {
        databaseRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        DataSnapshot dataSnapshot = task.getResult();
                        for (DataSnapshot ALL_USERS: dataSnapshot.getChildren()) {
                            String userId = ALL_USERS.child("userId").getValue().toString();
                            String username = ALL_USERS.child("username").getValue().toString();
                            String title = ALL_USERS.child("title").getValue().toString();
                            String author = ALL_USERS.child("author").getValue().toString();
                            String summary = ALL_USERS.child("summary").getValue().toString();
                            String genre = ALL_USERS.child("genre").getValue().toString();
                            String review = ALL_USERS.child("review").getValue().toString();
                            String rating = ALL_USERS.child("rating").getValue().toString();
                            String location = ALL_USERS.child("location").getValue().toString();
                            String coverpage = ALL_USERS.child("coverPage").getValue().toString();
                            String status = ALL_USERS.child("status").getValue().toString();
                            postFeedsList.add(new PostFeed(userId,username,title,author,summary,genre,review,rating,status,location,coverpage));
                        }
                    }
                }
                else{
                    Toast.makeText(AddPost.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeType = MimeTypeMap.getSingleton();
        return mimeType.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(){
        if(imageUri != null){
            StorageReference fileRef = storageRef.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            uploadPost(uri);
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    Toast.makeText(AddPost.this,"Upload in progress", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("error",e.getMessage());
                }
            });
        }
    }

    private void uploadPost(Uri uri){
        //Add post ot Database
        postFeedsList.add(new PostFeed(userId, name, title.getText().toString(), author.getText().toString(), summary.getText().toString(), genre.getText().toString(), review.getText().toString(), Float.toString(rating.getRating()), spinner.getSelectedItem().toString(), location.getText().toString(), uri.toString()));
        databaseRef.setValue(postFeedsList);
        Intent intent = new Intent(AddPost.this, ProfileActivity.class);
        startActivity(intent);
        Toast.makeText(getApplication(),"Post added successfully",Toast.LENGTH_LONG).show();
    }

    ActivityResultLauncher<String> getContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if(result != null){
                        imageUri = result;
                        imageView.setImageURI(result);

                    }
                }
            }
    );


}