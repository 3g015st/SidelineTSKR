package com.example.benedictlutab.sidelinetskr.modules.openTransactionSummary;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.benedictlutab.sidelinetskr.R;
import com.example.benedictlutab.sidelinetskr.helpers.apiRouteUtil;
import com.example.benedictlutab.sidelinetskr.helpers.fontStyleCrawler;
import com.example.benedictlutab.sidelinetskr.modules.tasksFeed.viewTaskDetails.taskDetailsActivity;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class openTransSummActivity extends AppCompatActivity
{
    private String TASK_ID, TRANS_CODE, TOTAL_AMOUNT, TASK_FEE, SERVICE_CHARGE, END_DATE;

    @BindView(R.id.tvTotalAmount) TextView tvTotalAmount;
    @BindView(R.id.tvTaskFee) TextView tvTaskFee;
    @BindView(R.id.tvDateTime) TextView tvDateTime;
    @BindView(R.id.tvTaskCategory) TextView tvTaskCategory;
    @BindView(R.id.tvServiceCharge) TextView tvServiceCharge;
    @BindView(R.id.tvLocation) TextView tvLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opentranssummary_activity_open_trans_summ);
        ButterKnife.bind(this);

        fetchPassedValues();
        changeFontFamily();

        fetchTransSummary();
    }

    private void changeFontFamily()
    {
        // Change Font Style.
        fontStyleCrawler fontStyleCrawler = new fontStyleCrawler(getAssets(), "fonts/avenir.otf");
        fontStyleCrawler.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));
    }

    private void fetchPassedValues()
    {
        TASK_ID = getIntent().getStringExtra("TASK_ID");
        TRANS_CODE = getIntent().getStringExtra("transaction_code");
        Log.e("fetchPValues: ", "Got the data!");
    }

    private void fetchTransSummary()
    {
        Log.e("fetchTransSummary: ", "START!");

        apiRouteUtil apiRouteUtil = new apiRouteUtil();

        StringRequest StringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_LOAD_TRANS_SUMMARY,
                new Response.Listener<String>()
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

                                tvTotalAmount.setText("PHP "+jsonObject.getString("totalfee"));
                                TOTAL_AMOUNT = jsonObject.getString("totalfee");

                                tvTaskFee.setText("PHP "+jsonObject.getString("task_fee"));
                                TASK_FEE = jsonObject.getString("task_fee");

                                tvServiceCharge.setText("PHP "+jsonObject.getString("comm_fee"));
                                SERVICE_CHARGE = jsonObject.getString("comm_fee");

                                tvDateTime.setText("DATE | TIME COMPLETED: "+jsonObject.getString("date_time_end"));
                                tvTaskCategory.setText(jsonObject.getString("name"));
                                tvLocation.setText(jsonObject.getString("line_one")+", "+jsonObject.getString("city"));
                            }
                        }
                        catch(JSONException e)
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
                        // Showing error message if something goes wrong.
                        Log.e("Error Response:", volleyError.toString());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                // Creating Map String Params.
                Map<String, String> Parameter = new HashMap<String, String>();

                Parameter.put("task_id", TASK_ID);

                return Parameter;
            }
        };
        // Initialize requestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(openTransSummActivity.this);

        // Send the StringRequest to the requestQueue.
        requestQueue.add(StringRequest);
    }

    @OnClick(R.id.btnCollect)
    public void collectPayment()
    {
        Log.e("collectPayment: ", "START!");

        apiRouteUtil apiRouteUtil = new apiRouteUtil();

        // Init loading dialog.
        final SweetAlertDialog swalDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        swalDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        swalDialog.setTitleText("");
        swalDialog.setCancelable(false);

        StringRequest StringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_LOAD_TRANS_SUMMARY,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String ServerResponse)
                    {
                        swalDialog.hide();
                        try
                        {
                            Log.e("SERVER RESPONSE: ", ServerResponse);

                            if(ServerResponse.contains("SUCCESS"))
                            {
                                // Display prompt then exit activity.
                            }

                        }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                            swalDialog.hide();
                            Log.e("Catch Response: ", e.toString());
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        // Showing error message if something goes wrong.
                        swalDialog.hide();
                        Log.e("Error Response:", volleyError.toString());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                // Creating Map String Params.
                Map<String, String> Parameter = new HashMap<String, String>();

                Parameter.put("task_fee", TASK_FEE);
                Parameter.put("service_fee", SERVICE_CHARGE);
                Parameter.put("end_date", END_DATE);
                Parameter.put("total_amount", TOTAL_AMOUNT);
                Parameter.put("trans_code", TRANS_CODE);
                Parameter.put("task_id", TASK_ID);

                return Parameter;
            }
        };
        // Initialize requestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(openTransSummActivity.this);

        // Send the StringRequest to the requestQueue.
        requestQueue.add(StringRequest);

        // Display progress dialog.
        swalDialog.show();
    }
}
