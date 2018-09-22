package com.example.benedictlutab.sidelinetskr.modules.tasksFeed.displaySkills;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.benedictlutab.sidelinetskr.R;
import com.example.benedictlutab.sidelinetskr.helpers.apiRouteUtil;
import com.example.benedictlutab.sidelinetskr.helpers.fontStyleCrawler;
import com.example.benedictlutab.sidelinetskr.models.Skill;
import com.example.benedictlutab.sidelinetskr.modules.showNearbyTasks.nearbyTasksActivity;

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

/**
 * A simple {@link Fragment} subclass.
 */
public class displaySkillsFragment extends Fragment
{
    @BindView(R.id.rv_skillset) RecyclerView recyclerView;
    @BindView(R.id.tvNearbyTasks) TextView tvNearbyTasks;

    private View rootView;
    private List<Skill> skillList = new ArrayList<>();
    private int listSize;

    private SharedPreferences sharedPreferences;
    private String USER_ID;

    public static displaySkillsFragment newInstance()
    {
        displaySkillsFragment displaySkillsFragment = new displaySkillsFragment();
        return displaySkillsFragment;
    }

    public displaySkillsFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Log.d("displaySkillsFragment","onCreateView: ON!");

        rootView = inflater.inflate(R.layout.displayskills_fragment_display_skills, container, false);
        ButterKnife.bind(this, rootView);

        // Get USER_ID
        sharedPreferences = getActivity().getSharedPreferences("userPreferences", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("USER_ID"))
        {
            USER_ID = sharedPreferences.getString("USER_ID", "");
            Log.e("USER_ID: ", USER_ID);
        }

        fetchSkills();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        fontStyleCrawler fontStyleCrawler = new fontStyleCrawler(getActivity().getAssets(), "fonts/avenir.otf");
        fontStyleCrawler.replaceFonts((ViewGroup) this.getView());
    }

    @OnClick(R.id.tvNearbyTasks)
    public void showNearbyTasks()
    {
        Log.e("showNearbyTasks: ", "Show nearby tasks activity");

        Intent intent = new Intent(getActivity(), nearbyTasksActivity.class);
        startActivity(intent);
    }

    private void initRecyclerView()
    {
        Log.d("listSize: ", String.valueOf(listSize));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        adapterDisplaySkills adapterDisplaySkills = new adapterDisplaySkills(getActivity().getApplicationContext(), skillList);
        recyclerView.setAdapter(adapterDisplaySkills);
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
    }

    private void fetchSkills()
    {
        apiRouteUtil apiRouteUtil = new apiRouteUtil();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_MY_SKILLS, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String ServerResponse)
            {
                try
                {
                    Log.d("Server Response: ", ServerResponse);
                    JSONArray jsonArray = new JSONArray(ServerResponse);
                    for(int x = 0; x < jsonArray.length(); x++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(x);
                        // Adding the jsonObject to the List.
                        skillList.add(new Skill(jsonObject.getString("skill_id"),
                                jsonObject.getString("name")));
                        listSize = skillList.size();
                        Log.d("listSize: ", String.valueOf(listSize));
                    }
                    initRecyclerView();
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    Log.d("Catch Response: ", e.toString());
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

                Parameter.put("tasker_id", USER_ID);

                return Parameter;
            }
        };

        // Add the StringRequest to Queue.
        Volley.newRequestQueue(getActivity().getApplicationContext()).add(stringRequest);
    }
}
