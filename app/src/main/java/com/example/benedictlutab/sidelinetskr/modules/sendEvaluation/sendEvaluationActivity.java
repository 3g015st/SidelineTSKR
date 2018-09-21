package com.example.benedictlutab.sidelinetskr.modules.sendEvaluation;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Picasso;
import com.willy.ratingbar.BaseRatingBar;
import com.willy.ratingbar.RotationRatingBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class sendEvaluationActivity extends AppCompatActivity
{
    private String TASK_ID, TASK_GIVER_ID, USER_ID, RATING;

    @BindView(R.id.tvTaskGiver) TextView tvTaskGiver;
    @BindView(R.id.civTaskGiverPhoto) CircleImageView civTaskGiverPhoto;
    @BindView(R.id.srbStar) RotationRatingBar srbStar;
    @BindView(R.id.etReview) EditText etReview;
    @BindView(R.id.btnSend) Button btnSend;
    @BindView(R.id.btnClose) Button btnClose;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sendevaluation_activity_main);
        ButterKnife.bind(this);

        // Get USER_ID
        sharedPreferences = getSharedPreferences("userPreferences", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("USER_ID"))
        {
            USER_ID = sharedPreferences.getString("USER_ID", "");
            Log.e("USER_ID:", USER_ID);
        }

        changeFontFamily();
        fetchPassedValues();

        fetchEvalDetails();

        srbStar.setOnRatingChangeListener(new BaseRatingBar.OnRatingChangeListener()
        {
            @Override
            public void onRatingChange(BaseRatingBar ratingBar, float rating)
            {
                RATING = Float.toString(rating);
                Log.e("RATING: ", "" + RATING);
            }
        });
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
        Log.e("fetchPValues: ", "Got the data!");
    }

    @OnClick(R.id.btnClose)
    public void onClosePressed()
    {
        Log.e("onClosePressed: ", "START!");
        final SweetAlertDialog swalDialog = new SweetAlertDialog(sendEvaluationActivity.this, SweetAlertDialog.SUCCESS_TYPE);
        swalDialog.setTitleText("Transaction Complete").setContentText("Thank you for using Sideline :)!")
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                {
                    @Override
                    public void onClick(SweetAlertDialog sDialog)
                    {
                        finish();
                    }
                })
                .show();
    }

    @OnClick(R.id.btnSend)
    public void sendEvaluation()
    {
        Log.e("sendEvaluation: ", "START!");
        apiRouteUtil apiRouteUtil = new apiRouteUtil();

        // Init loading dialog.
        final SweetAlertDialog swalDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        swalDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        swalDialog.setTitleText(" ");
        swalDialog.setContentText("Please wait while your evaluation is sending...");
        swalDialog.setCancelable(false);

        StringRequest StringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_SEND_EVAL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String ServerResponse)
                    {
                        // Showing response message coming from server.
                        String SERVER_RESPONSE = ServerResponse.replaceAll("\\s+","");
                        Log.e("RESPONSE: ", SERVER_RESPONSE);

                        if(SERVER_RESPONSE.equals("SUCCESS"))
                        {
                            // Exit this activity then prompt success
                            swalDialog.hide();
                            final SweetAlertDialog swalDialog = new SweetAlertDialog(sendEvaluationActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                            swalDialog.setTitleText("Transaction Complete").setContentText("Thank you for using Sideline :)!")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                                    {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog)
                                        {
                                            finish();
                                        }
                                    })
                                    .show();
                        }
                        else
                        {
                            // Prompt error
                            swalDialog.hide();
                            TastyToast.makeText(getApplicationContext(), "There has been an error sending your evaluation!", TastyToast.LENGTH_LONG, TastyToast.SUCCESS).show();
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

                if(etReview.getText().toString().isEmpty())
                {
                    String review = " ";
                    Parameter.put("review", review);
                }
                else
                {
                    Parameter.put("review", etReview.getText().toString());
                }

                Parameter.put("receiver_id", TASK_GIVER_ID);
                Parameter.put("rating", RATING);
                Parameter.put("sender_id", USER_ID);
                Parameter.put("task_id", TASK_ID);

                return Parameter;
            }
        };
        // Initialize requestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(sendEvaluationActivity.this);

        // Send the StringRequest to the requestQueue.
        requestQueue.add(StringRequest);

        // Display progress dialog.
        swalDialog.show();
    }

    private void fetchEvalDetails()
    {
        Log.e("fetchTaskDetails: ", "STARTED !");

        final apiRouteUtil apiRouteUtil = new apiRouteUtil();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_EVAL_DETAILS, new Response.Listener<String>()
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

                        // Load task giver's prof pic.
                        Picasso.with(sendEvaluationActivity.this).load(apiRouteUtil.DOMAIN + jsonObject.getString("profile_picture")).
                                fit().centerInside().into(civTaskGiverPhoto);

                        tvTaskGiver.setText(jsonObject.getString("full_name").toString());
                        TASK_GIVER_ID = jsonObject.getString("task_giver_id");
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
                        Log.e("Error Response: ", volleyError.toString());
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
        // Add the StringRequest to Queue.
        Volley.newRequestQueue(this).add(stringRequest);
    }

}
