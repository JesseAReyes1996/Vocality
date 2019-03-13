package edu.jreye039.vocality;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class Main extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //check if the is the user is logged in
        SharedPreferences userInfo = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String username = userInfo.getString("username", "");

        //if they aren't logged in, take them to the registration/login activity
        if(username.equals("")){
            Intent startIntent = new Intent(getApplicationContext(), NotLoggedIn.class);
            startActivity(startIntent);
        }
        else{
            BottomNavigationView bottomNavView = findViewById(R.id.bottom_navigation);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FeedFragment()).commit();

            bottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;

                    switch (menuItem.getItemId()) {
                        case R.id.nav_feed:
                            selectedFragment = new FeedFragment();
                            break;
                        case R.id.nav_search:
                            selectedFragment = new SearchFragment();
                            break;
                        case R.id.nav_new_recording:
                            Intent startIntent = new Intent(getApplicationContext(), SearchSongs.class);
                            startActivity(startIntent);
                            selectedFragment = new FeedFragment();
                            break;
                        case R.id.nav_notifications:
                            selectedFragment = new NotificationsFragment();
                            break;
                        case R.id.nav_profile:
                            selectedFragment = new ProfileFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

                    return true;
                }
            });
        }
    }

    //update feed when activity comes back into focus
    @Override
    public void onResume() {
        super.onResume();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FeedFragment()).commit();
    }

}
