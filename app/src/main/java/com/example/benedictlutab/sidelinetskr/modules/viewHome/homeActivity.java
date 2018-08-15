package com.example.benedictlutab.sidelinetskr.modules.viewHome;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.benedictlutab.sidelinetskr.R;
import com.example.benedictlutab.sidelinetskr.modules.myTasks.myTasksFragment;
import com.example.benedictlutab.sidelinetskr.modules.tasksFeed.displaySkills.displaySkillsFragment;

public class homeActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewhome_activity_home);

        //Bottom navigation controls
        BottomNavigationView btmNavigationBar = findViewById(R.id.btmNavigationBar);
        btmNavigationBar.setOnNavigationItemSelectedListener (new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                FragmentManager fragmentManager = getSupportFragmentManager();
                switch (item.getItemId())
                {
                    case R.id.action_tasks_feed:
                        fragmentManager.beginTransaction().replace(R.id.frmlayout_fragment, displaySkillsFragment.newInstance()).commit();
                        break;
                    case R.id.action_tasks:
                        fragmentManager.beginTransaction().replace(R.id.frmlayout_fragment, myTasksFragment.newInstance()).commit();
                        break;
                }
                return true;
            }
        });

        // Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frmlayout_fragment, displaySkillsFragment.newInstance());
        transaction.commit();
    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }
}
