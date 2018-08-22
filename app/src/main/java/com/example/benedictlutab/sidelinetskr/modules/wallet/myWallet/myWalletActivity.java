package com.example.benedictlutab.sidelinetskr.modules.wallet.myWallet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.example.benedictlutab.sidelinetskr.models.loadHistory;
import com.example.benedictlutab.sidelinetskr.modules.tasksFeed.displayTasks.adapterDisplayTasks;
import com.example.benedictlutab.sidelinetskr.modules.tasksFeed.viewTaskDetails.taskDetailsActivity;
import com.example.benedictlutab.sidelinetskr.modules.wallet.denominationList.denominationListActivity;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class myWalletActivity extends AppCompatActivity
{
    @BindView(R.id.tvCoins) TextView tvCoins;
    @BindView(R.id.swipeRefLayout_id) SwipeRefreshLayout swipeRefLayout;
    @BindView(R.id.btnTopup) Button btnTopup;
    @BindView(R.id.btnBack) Button btnBack;
    @BindView(R.id.rv_history) RecyclerView recyclerView;

    private int listSize;
    private List<loadHistory> loadHistories = new ArrayList<>();
    private adapterLoadHistory adapterLoadHistory;

    private SharedPreferences sharedPreferences;
    private String USER_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mywallet_activity_my_wallet);
        ButterKnife.bind(this);

        // Get USER_ID
        sharedPreferences = getSharedPreferences("userPreferences", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("USER_ID"))
        {
            USER_ID = sharedPreferences.getString("USER_ID", "");
            Log.e("USER_ID:", USER_ID);
        }

        changeFontFamily();
        initSwipeRefLayout();
    }

    @OnClick(R.id.btnTopup)
    public void topUpCoins()
    {
        Intent intent = new Intent(this, denominationListActivity.class);
        startActivityForResult(intent, 420);
    }

    @OnClick(R.id.btnBack)
    public void onBackPressed()
    {
        this.finish();
    }

    private void changeFontFamily()
    {
        // Change Font Style.
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
                fetchBalance();
                fetchLoadHistory();
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
                fetchBalance();
                fetchLoadHistory();
                swipeRefLayout.setRefreshing(false);
            }
        });
    }

    private void initRecyclerView()
    {
        Log.d("listSize: ", String.valueOf(listSize));

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        adapterLoadHistory = new adapterLoadHistory(getApplicationContext(), loadHistories);
        recyclerView.setAdapter(adapterLoadHistory);

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

                        tvCoins.setText(jsonObject.getString("amount").toString() + " COINS");
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    Log.e("Catch Response: ", e.toString());

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

                Parameter.put("user_id", USER_ID);

                return Parameter;
            }
        };
        // Add the StringRequest to Queue.
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void fetchLoadHistory()
    {
        Log.e("fetchLoadHistory: ", "STARTED !");
        loadHistories.clear();

        apiRouteUtil apiRouteUtil = new apiRouteUtil();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_FETCH_LOAD_HISTORY, new Response.Listener<String>()
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
                        loadHistories.add(new loadHistory(jsonObject.getString("amount"),
                                jsonObject.getString("date_time_sent"))
                        );
                        listSize = loadHistories.size();
                        Log.e("listSize: ", String.valueOf(listSize));
                    }
                    initRecyclerView();
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    Log.e("Catch Response: ", e.toString());

                }
                swipeRefLayout.setRefreshing(false);
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        Log.e("Error Response: ", volleyError.toString());
                        swipeRefLayout.setRefreshing(false);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                // Creating Map String Params.
                Map<String, String> Parameter = new HashMap<String, String>();

                Parameter.put("USER_ID", USER_ID);

                return Parameter;
            }
        };
        // Add the StringRequest to Queue.
        Volley.newRequestQueue(this).add(stringRequest);
    }
}
