 package com.example.benedictlutab.sidelinetskr.modules.viewDashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

 public class dashboardActivity extends AppCompatActivity
{
    private String USER_ID, SWITCH = "NONE";
    private SharedPreferences sharedPreferences;

    @BindView(R.id.swipeRefLayout_id) SwipeRefreshLayout swipeRefLayout;

    @BindView(R.id.btnBack) Button btnBack;

    @BindView(R.id.bcSales) BarChart bcSales;

    @BindView(R.id.tvTotal) TextView tvTotal;

    @BindView(R.id.tvWeek) TextView tvWeek;
    @BindView(R.id.tvYear) TextView tvYear;
    @BindView(R.id.tvMonth) TextView tvMonth;

    private ArrayList<BarEntry> salesAmount = new ArrayList<BarEntry>();
    private ArrayList<String> label = new ArrayList<String>();

    private apiRouteUtil apiRouteUtil = new apiRouteUtil();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewdashboard_activity_dashboard);

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


    @OnClick(R.id.btnBack)
    public void onBackPressed()
    {
        this.finish();
    }

    @OnClick({R.id.tvMonth, R.id.tvWeek, R.id.tvYear})
    public void pickEarningDuration(View v)
    {
        switch(v.getId())
        {
            case R.id.tvMonth:
                tvMonth.setTextColor(getResources().getColor(R.color.colorFontPrimary));
                tvWeek.setTextColor(Color.parseColor("#616161"));
                tvYear.setTextColor(Color.parseColor("#616161"));
                SWITCH = "This Month";

                initSwipeRefLayout();
                break;
            case R.id.tvWeek:
                tvWeek.setTextColor(getResources().getColor(R.color.colorFontPrimary));
                tvYear.setTextColor(Color.parseColor("#616161"));
                tvMonth.setTextColor(Color.parseColor("#616161"));
                SWITCH = "This Week";

                initSwipeRefLayout();
                break;
            case R.id.tvYear:
                tvYear.setTextColor(getResources().getColor(R.color.colorFontPrimary));
                tvMonth.setTextColor(Color.parseColor("#616161"));
                tvWeek.setTextColor(Color.parseColor("#616161"));
                SWITCH = "This Year";

                initSwipeRefLayout();
                break;
        }
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

                fetchEarnings();

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

                fetchEarnings();

                swipeRefLayout.setRefreshing(false);
            }
        });
    }


    private void fetchEarnings()
    {
        Log.e("fetchEarnings: ", "STARTED !");

        //RESET DAILY SALES AMOUNT
        salesAmount.clear();
        label.clear();
        final XAxis xAxis = bcSales.getXAxis();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_FETCH_EARNINGS, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String ServerResponse)
            {
                try
                {
                    Log.e("SERVER RESPONSE: ", ServerResponse.toString());
                    JSONArray jsonArray = new JSONArray(ServerResponse.toString());

                    for(int x = 0; x < jsonArray.length(); x++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(x);
                        Log.e("jsonObject: ", "COUNT - " + x +", "+jsonObject);

                        if(!jsonObject.has("total"))
                        {
                            //KUNIN MO YUNG MGA AMOUNT AT IPASOK SA ARRAYLIST
                            salesAmount.add(new BarEntry(x, Float.parseFloat(jsonObject.getString("amount"))));

                            //GET DAYS
                            label.add(jsonObject.getString("label"));
                            xAxis.setValueFormatter(new IndexAxisValueFormatter(label));

                        }

//                        GET TOTAL EARNINGS
                        if(jsonObject.has("total"))
                        {
                            tvTotal.setText("TOTAL EARNINGS: " + jsonObject.getString("total"));
                        }


                    }
                    // SET BARCHART ATTRIBS
                    bcSales.setDrawBarShadow(false);
                    bcSales.setDrawValueAboveBar(true);
                    bcSales.setMaxVisibleValueCount(7);
                    bcSales.setPinchZoom(false);
                    bcSales.setDrawGridBackground(true);
                    bcSales.getAxisLeft().setTextColor(Color.WHITE);
                    bcSales.getAxisRight().setTextColor(Color.WHITE);
                    bcSales.getXAxis().setTextColor(Color.WHITE);
                    bcSales.getLegend().setTextColor(Color.WHITE);
                    bcSales.setGridBackgroundColor(Color.TRANSPARENT);
                    bcSales.fitScreen();

                    // SET DATASET NAME
                    BarDataSet bdsEarnings = new BarDataSet(salesAmount, "Earnings");
                    bdsEarnings.setColors(ColorTemplate.COLORFUL_COLORS);
                    bdsEarnings.setValueTextColor(Color.WHITE);
                    bdsEarnings.setValueTextSize(10f);

                    // I-INITIALIZE NA YUNG DATASET
                    BarData earningsData = new BarData(bdsEarnings);
                    earningsData.setBarWidth(0.9f);
                    bcSales.setData(earningsData);

                    // SET X-AXIS ATTRIBS
                    xAxis.setGranularity(1f); // only intervals of 1 day
                    xAxis.setTextSize(12);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    Log.e("Catch Response: ", e.toString());
                    swipeRefLayout.setRefreshing(false);

                }
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

                Parameter.put("TASKER_ID", USER_ID);

                if(SWITCH.equals("NONE"))
                {
                    Parameter.put("SWITCH", "This Week");
                }
                else
                {
                    Parameter.put("SWITCH", SWITCH);
                }

                return Parameter;
            }
        };
        // Add the StringRequest to Queue.
        Volley.newRequestQueue(this).add(stringRequest);

        if (swipeRefLayout.isRefreshing()) {
            swipeRefLayout.setRefreshing(false);
        }
    }

}
