package com.example.benedictlutab.sidelinetskr.modules.openTransactionSummary;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.benedictlutab.sidelinetskr.R;
import com.example.benedictlutab.sidelinetskr.helpers.apiRouteUtil;
import com.example.benedictlutab.sidelinetskr.helpers.fontStyleCrawler;
import com.example.benedictlutab.sidelinetskr.helpers.networkUtil;
import com.example.benedictlutab.sidelinetskr.modules.sendEvaluation.sendEvaluationActivity;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class openTransSummActivity extends AppCompatActivity
{
    private String TASK_ID, TRANS_CODE, TOTAL_AMOUNT, TASK_FEE, SERVICE_CHARGE, END_DATE, TASK_GIVER_ID;

    @BindView(R.id.tvTotalAmount) TextView tvTotalAmount;
    @BindView(R.id.tvTaskFee) TextView tvTaskFee;
    @BindView(R.id.tvDateTime) TextView tvDateTime;
    @BindView(R.id.tvTaskCategory) TextView tvTaskCategory;
    @BindView(R.id.tvServiceCharge) TextView tvServiceCharge;
    @BindView(R.id.tvLocation) TextView tvLocation;
    @BindView(R.id.tvTransCode) TextView tvTransCode;

    @BindView(R.id.btnBack) Button btnBack;

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
        fontStyleCrawler.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
    }

    private void fetchPassedValues()
    {
        TASK_ID = getIntent().getStringExtra("TASK_ID");
        TRANS_CODE = getIntent().getStringExtra("transaction_code");
        TASK_GIVER_ID = getIntent().getStringExtra("TASK_GIVER_ID");
        Log.e("fetchPValues: ", "Got the data!");
    }

    @OnClick(R.id.btnBack)
    public void onBackPressed()
    {
        this.finish();
    }

    private void fetchTransSummary()
    {
        Log.e("fetchTransSummary: ", "STARTED!");

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
                            for (int x = 0; x < jsonArray.length(); x++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(x);

                                tvTotalAmount.setText("PHP " + jsonObject.getString("totalfee"));
                                TOTAL_AMOUNT = jsonObject.getString("totalfee");

                                tvTaskFee.setText("PHP " + jsonObject.getString("task_fee"));
                                TASK_FEE = jsonObject.getString("task_fee");

                                tvServiceCharge.setText("PHP " + jsonObject.getString("comm_fee"));
                                SERVICE_CHARGE = jsonObject.getString("comm_fee");

                                tvDateTime.setText("DATE | TIME COMPLETED: " + jsonObject.getString("date_time_end"));
                                END_DATE = jsonObject.getString("date_time_end");

                                tvTaskCategory.setText(jsonObject.getString("name"));
                                tvLocation.setText(jsonObject.getString("line_one") + ", " + jsonObject.getString("city"));

                                tvTransCode.setText("TSK-" + TRANS_CODE);
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            Log.e("CATCH RESPONSE: ", e.toString());
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        Log.e("ERROR RESPONSE: ", volleyError.toString());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> Parameter = new HashMap<String, String>();
                Parameter.put("task_id", TASK_ID);

                return Parameter;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(openTransSummActivity.this);
        requestQueue.add(StringRequest);
    }

    @OnClick(R.id.btnCollect)
    public void showPromptIsFeeCollected()
    {
        Log.e("shwPrmptIsFeeCollctd: ", "STARTED!");

        final SweetAlertDialog swalDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        swalDialog.setTitleText("Payment Collection")
                                    .setContentText(" Are you sure that you have collected the payment for the task?  ")
                                    .setCancelText("NO")
                                    .setConfirmText(" YES ")
                                    .showCancelButton(true)
                                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener()
                                    {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog)
                                        {
                                            sDialog.hide();
                                        }
                                    })
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                                    {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog)
                                        {
                                            // Send POST Request to finally complete the task.
                                            setTaskCompleted();
                                        }
                                    })
                                    .show();
    }

    private void setTaskCompleted()
    {
        Log.e("setTaskCompleted: ", "START!");
        Log.e("TASK_FEE: ", TASK_FEE);
        Log.e("SERVICE_CHARGE: ", SERVICE_CHARGE);
        Log.e("END_DATE: ", END_DATE);
        Log.e("TOTAL_AMOUNT: ", TOTAL_AMOUNT);
        Log.e("TRANS_CODE: ", TRANS_CODE);
        Log.e("TASK_ID: ", TASK_ID);

        apiRouteUtil apiRouteUtil = new apiRouteUtil();

        final SweetAlertDialog swalDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        swalDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        swalDialog.setTitleText(" ");
        swalDialog.setContentText("Please wait while we are processing the completion of the task :)");
        swalDialog.setCancelable(false);

            StringRequest StringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_COLLECT_PAYMENT,
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
                            // Go to Evaluation Activity.
                            Log.e("setTaskCompleted: ", "GO TO EVAL ACTIVITY!");
                            Intent intent = new Intent(getApplicationContext(), sendEvaluationActivity.class);
                            intent.putExtra("TASK_ID", TASK_ID);
                            finish();
                            startActivity(intent);
                        }
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                        swalDialog.hide();
                        TastyToast.makeText(getApplicationContext(), "There has been an error in completing your request. :(", TastyToast.LENGTH_LONG, TastyToast.ERROR). show();
                        Log.e("CATCH RESPONSE: ", e.toString());
                    }
                }
            },
            new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError volleyError)
                {
                    swalDialog.hide();
                    networkUtil networkUtil = new networkUtil(openTransSummActivity.this);
                    networkUtil.showNetworkError();
                    Log.e("ERROR RESPONSE: ", volleyError.toString());
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
    RequestQueue requestQueue = Volley.newRequestQueue(openTransSummActivity.this);

        StringRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(StringRequest);
        swalDialog.show();
    }
}
