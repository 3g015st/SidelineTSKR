package com.example.benedictlutab.sidelinetskr.modules.viewHome;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.benedictlutab.sidelinetskr.R;
import com.example.benedictlutab.sidelinetskr.helpers.apiRouteUtil;
import com.example.benedictlutab.sidelinetskr.modules.messages.loadChatRooms.chatRoomsFragment;
import com.example.benedictlutab.sidelinetskr.modules.more.moreFragment;
import com.example.benedictlutab.sidelinetskr.modules.myTasks.myTasksFragment;
import com.example.benedictlutab.sidelinetskr.modules.tasksFeed.displaySkills.displaySkillsFragment;
import com.example.benedictlutab.sidelinetskr.modules.viewMyProfile.myProfileFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class homeActivity extends AppCompatActivity
{

    private String USER_ID, BALANCE;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewhome_activity_home);

        // Get USER_ID
        sharedPreferences = getSharedPreferences("userPreferences", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("USER_ID"))
        {
            USER_ID = sharedPreferences.getString("USER_ID", "");
            Log.e("USER_ID:", USER_ID);
        }

        fetchBalance();

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
                        fetchBalance();
                        break;
                    case R.id.action_messages:
                        fragmentManager.beginTransaction().replace(R.id.frmlayout_fragment, chatRoomsFragment.newInstance()).commit();
                        fetchBalance();
                        break;
                    case R.id.action_tasks:
                        fragmentManager.beginTransaction().replace(R.id.frmlayout_fragment, myTasksFragment.newInstance()).commit();
                        fetchBalance();
                        break;
                    case R.id.action_profile:
                        fragmentManager.beginTransaction().replace(R.id.frmlayout_fragment, myProfileFragment.newInstance()).commit();
                        fetchBalance();
                        break;
                    case R.id.action_more:
                        fragmentManager.beginTransaction().replace(R.id.frmlayout_fragment, moreFragment.newInstance()).commit();
                        fetchBalance();
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

    private void fetchBalance()
    {
        Log.e("fetchBalance: ", "STARTED !");

        final apiRouteUtil apiRouteUtil = new apiRouteUtil();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_FETCH_BALANCE, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String ServerResponse)
            {
                try
                {
                    Log.e("SERVER RESPONSE: ", ServerResponse);
                    JSONArray jsonArray = new JSONArray(ServerResponse);
                    for(int x = 0; x < jsonArray.length(); x++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(x);

                        // Put balance to sharedPreferences.
                        BALANCE = jsonObject.getString("amount").toString();
                        sharedPreferences = getSharedPreferences("userPreferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("BALANCE", BALANCE);
                        editor.commit();
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    Log.e("Catch Response: ", e.toString());

                }
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        Log.d("Error Response: ", volleyError.toString());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                // Creating Map String Params.
                Map<String, String> Parameter = new HashMap<String, String>();

                Parameter.put("user_id", USER_ID);

                return Parameter;
            }
        };
        // Add the StringRequest to Queue.
        Volley.newRequestQueue(this).add(stringRequest);
    }
}
