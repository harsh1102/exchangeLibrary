package com.example.exchangelibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.Arrays;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseAuth mAuth;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ListView listView;
    ArrayList<String>  initName, name, tempArr;
    boolean isSearching = false;
    ArrayAdapter<String> arrayAdapter;
    View feed,searchfeed;
    SearchView searchView;
    RecyclerView recyclerView;
    PostFeedAdapter postAdapter;
    ArrayList<PostFeed> postFeedsList = new ArrayList<PostFeed>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("postFeeds");
    String search_username;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigationview);
        toolbar = findViewById(R.id.toolbar);
        feed = findViewById(R.id.feedview);
        searchfeed = findViewById(R.id.searchinfo);
        initName = new ArrayList<>();
        name = new ArrayList<>();
        tempArr = name;

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigration_open,R.string.navigration_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();

        mAuth = FirebaseAuth.getInstance();

        listView = findViewById(R.id.listview);
        arrayAdapter =new ArrayAdapter<String>( this, android.R.layout.simple_list_item_1,tempArr);
        listView.setAdapter(arrayAdapter);
        tempArr.clear();
        recyclerView = findViewById(R.id.post_feeds_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        this.showPostFeeds();

        FloatingActionButton chatButton = (FloatingActionButton) findViewById(R.id.chatBtn);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logout: {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.profile:{
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        MenuItem menuItem = menu.findItem(R.id.actionsearch);
        menuItem.setOnMenuItemClickListener(
                new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        isSearching = true;
                        return false;
                    }
                }
        );
        tempArr.clear();
        searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Type here to search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()){
                    Log.e("In null search",newText);
                    listView.setVisibility(View.GONE);
                    feed.setVisibility(View.VISIBLE);
                }
                else{
                    Log.e("In search",newText);
                    listView.setVisibility(View.VISIBLE);
                    feed.setVisibility(View.GONE);
                    tempArr.clear();
                    arrayAdapter.notifyDataSetChanged();
                    for (int i = 0; i< initName.size(); i++) {
                        if(initName.get(i).toLowerCase().contains(newText.toString().toLowerCase())){
                            arrayAdapter.add(initName.get(i));
                        }
                    }
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            if (tempArr.size() == 1){
                                search_username = tempArr.get(i);
                                String str = Arrays.toString(tempArr.toArray());
                                Intent intent = new Intent(view.getContext(), ProfileActivity.class);
                                intent.putExtra("USERNAME", search_username);
                                view.getContext().startActivity(intent);
                            }
                        }
                    });
                }
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchView.onActionViewCollapsed();
//                feed.setVisibility(View.VISIBLE);
//                listView.setVisibility(View.GONE);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
//            feed.setVisibility(View.GONE);
            searchView.setIconified(true);
        } else {
//            feed.setVisibility(View.VISIBLE);
            super.onBackPressed();
        }
    }

    private void showPostFeeds() {
        myRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
                            postAdapter = new PostFeedAdapter(HomeActivity.this,postFeedsList);
                            initName.add(username);
                            name.add(username);
                            recyclerView.setAdapter(postAdapter);
                        }
                    }
                }
                else{
                    Toast.makeText(HomeActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//    private ArrayList<PostFeed> createPostFeeds(){
//        postFeedsList = new ArrayList<>();
//        postFeedsList.add(new PostFeed(
//                "Kavitha Pasupuleti",
//                "Title: Ikigai",
//                "Author: Hector Gracia",
//                "Summary: In researching this book, the authors interviewed the residents of the Japanese village.",
//                "Genre: Philosophy",
//                "Review: Good for self learning",
//                "Rating: 4/5",
//                "Status In hand",
//                "Location: Texas"));
//        postFeedsList.add(new PostFeed(
//                "Sibangee Mohanty",
//                "Title: The Intelligent Investor",
//                "Author: Benjamin Graham",
//                "Summary: Over the years, market developments have proven the wisdom of Graham's strategies. While preserving the integrity of Graham's original text, this revised edition includes updated commentary by noted financial journalist Jason Zweig, whose perspective incorporates the realities of today's market, draws parallels between Graham's examples and today's financial headlines. ",
//                "Genre: Economics",
//                "Review: Best for personal finance",
//                "Rating: 5/5",
//                "Status In hand",
//                "Location: Texas"));
//        postFeedsList.add(new PostFeed(
//                "Harsh Muniwala",
//                "Title: Atomic Habits",
//                "Author: James Clear",
//                "Summary: If you're having trouble changing your habits, the problem isn't you. The problem is your system. Bad habits repeat themselves again and again not because you don't want to change, but because you have the wrong system for change. You do not rise to the level of your goals. You fall to the level of your systems.",
//                "Genre: Self-help",
//                "Review: Average",
//                "Rating: 3.5/5",
//                "Status In hand",
//                "Location: Texas"));
//        return postFeedsList;
//    }
}