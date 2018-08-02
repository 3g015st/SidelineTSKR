package com.example.benedictlutab.sidelinetskr.modules.tasksFeed.displayTasks;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.benedictlutab.sidelinetskr.R;
import com.example.benedictlutab.sidelinetskr.helpers.apiRouteUtil;
import com.example.benedictlutab.sidelinetskr.helpers.fontStyleCrawler;
import com.example.benedictlutab.sidelinetskr.models.availableTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class displayTasksActivity extends Activity
{
    @BindView(R.id.rv_tasks) RecyclerView recyclerView;
    @BindView(R.id.btnBack) Button btnBack;
    @BindView(R.id.tvSkillName) TextView tvSkillName;
    @BindView(R.id.swipeRefLayout_id) SwipeRefreshLayout swipeRefLayout;

    private int listSize;
    private List<availableTask> availableTaskList = new ArrayList<>();

    private String SKILL;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.displaytasks_activity_display_tasks);
        ButterKnife.bind(this);
        replaceFontStyle();

        Bundle Extras = getIntent().getExtras();
        if (Extras != null)
        {
            SKILL = Extras.getString("SKILL_NAME");
            Log.e("SKILL: ", SKILL);
        }

           initSwipeRefLayout();
    }

    @OnClick(R.id.btnBack)
    public void onBackPressed()
    {
        this.finish();
    }

    private void initRecyclerView()
    {
        Log.d("listSize: ", String.valueOf(listSize));

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        adapterDisplayTasks adapterDisplayTasks = new adapterDisplayTasks(getApplicationContext(), availableTaskList);
        recyclerView.setAdapter(adapterDisplayTasks);

        if (listSize == 0)
        {
            Log.d("initRecyclerView: ", "GONE-VISIBLE");
            recyclerView.setVisibility(View.GONE);
        }
        else
        {
            Log.d("initRecyclerView: ", "VISIBLE-GONE");
            recyclerView.setVisibility(View.VISIBLE);
        }

        tvSkillName.setText(SKILL +" ("+Integer.toString(listSize)+")");
    }


    public void replaceFontStyle()
    {
        fontStyleCrawler fontStyleCrawler = new fontStyleCrawler(getAssets(), "fonts/avenir.otf");
        fontStyleCrawler.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));
    }

    private void initSwipeRefLayout()
    {
        swipeRefLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                // Fetching data from server
                fetchAvailableTasks();
            }
        });
        swipeRefLayout.setColorSchemeResources(R.color.colorPrimaryDark, android.R.color.holo_green_dark, android.R.color.holo_orange_dark, android.R.color.holo_blue_dark);
        swipeRefLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                swipeRefLayout.setRefreshing(true);
                // Fetching data from server
                fetchAvailableTasks();
                swipeRefLayout.setRefreshing(false);
            }
        });
    }

    private void fetchAvailableTasks()
    {
        Log.e("fetchAVTasks: ", "STARTED !");
        availableTaskList.clear();

        apiRouteUtil apiRouteUtil = new apiRouteUtil();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_AVAILABLE_TASKS, new Response.Listener<String>()
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
                        // Adding the jsonObject to the List.
                        availableTaskList.add(new availableTask(jsonObject.getString("task_id"),
                                jsonObject.getString("title"),
                                jsonObject.getString("line_one"),
                                jsonObject.getString("city"),
                                jsonObject.getString("date_time_end"),
                                jsonObject.getString("task_fee"),
                                jsonObject.getString("status"),
                                jsonObject.getString("profile_picture"),
                                jsonObject.getString("first_name"),
                                jsonObject.getString("last_name"),
                                jsonObject.getString("category_name"),
                                jsonObject.getString("image_one"),
                                jsonObject.getString("image_two"),
                                jsonObject.getString("description"),
                                jsonObject.getString("date_time_posted"))
                        );
                        listSize = availableTaskList.size();
                        Log.e("listSize: ", String.valueOf(listSize));
                    }
                    initRecyclerView();
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    Log.d("Catch Response: ", e.toString());

                }
                swipeRefLayout.setRefreshing(false);
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        Log.d("Error Response: ", volleyError.toString());
                        swipeRefLayout.setRefreshing(false);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                // Creating Map String Params.
                Map<String, String> Parameter = new HashMap<String, String>();

                Parameter.put("category_name", SKILL);

                return Parameter;
            }
        };
        // Add the StringRequest to Queue.
        Volley.newRequestQueue(this).add(stringRequest);
    }
}
