package com.example.benedictlutab.sidelinetskr.modules.updateAboutMe;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class updateAboutMeActivity extends AppCompatActivity
{
    @BindView(R.id.etShortBio) EditText etShortBio;
    @BindView(R.id.btnSubmit) Button btnSubmit;
    @BindView(R.id.btnBack) Button btnBack;

    private SharedPreferences sharedPreferences;
    private String USER_ID;

    apiRouteUtil apiRouteUtil = new apiRouteUtil();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updateaboutme_activity_update_about_me);
        ButterKnife.bind(this);

        changeFontFamily();

        // Get USER_ID
        sharedPreferences = getSharedPreferences("userPreferences", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("USER_ID"))
        {
            USER_ID = sharedPreferences.getString("USER_ID", "");
            Log.e("USER_ID:", USER_ID);
        }

        loadShortBio();
    }

    private void changeFontFamily()
    {
        // Change Font Style.
        fontStyleCrawler fontStyleCrawler = new fontStyleCrawler(getAssets(), "fonts/avenir.otf");
        fontStyleCrawler.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
    }

    @OnClick(R.id.btnBack)
    public void onBackPressed()
    {
        this.finish();
    }

    @OnClick(R.id.btnSubmit)
    public void updateShortBio()
    {
        Log.e("updateShortBio: ", "STARTED!");

        RequestQueue requestQueue = Volley.newRequestQueue(updateAboutMeActivity.this);

        // Init loading dialog.
        final SweetAlertDialog swalDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        swalDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        swalDialog.setTitleText(" ");
        swalDialog.setContentText("Please wait while we are updating your short bio :)");
        swalDialog.setCancelable(false);

        StringRequest StringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_UPDATE_SHORT_BIO,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String ServerResponse)
                    {
                        // Showing response message coming from server.
                        String SERVER_RESPONSE = ServerResponse.replaceAll("\\s+","");
                        Log.e("RESPONSE: ", SERVER_RESPONSE);

                        if(SERVER_RESPONSE.equals("SUCCESSSUCCESS") || SERVER_RESPONSE.equals("SUCCESS"))
                        {
                            // Exit this activity then prompt success
                            swalDialog.hide();
                            TastyToast.makeText(getApplicationContext(), "Your short bio has been successfully updated!", TastyToast.LENGTH_LONG, TastyToast.SUCCESS).show();
                            finish();
                        }
                        else
                        {
                            // Prompt error
                            swalDialog.hide();
                            TastyToast.makeText(getApplicationContext(), "There has been an error updating your short bio!", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
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
                        Log.e("ERROR RESPONSE: ", volleyError.toString());
                        // No network connection.
                        new SweetAlertDialog(updateAboutMeActivity.this, SweetAlertDialog.ERROR_TYPE).setTitleText("Connection Timeout").setContentText("There seems to have a connection error, please try again later. :(")
                                .setConfirmText("OK")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                                {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog)
                                    {
                                        // Exit application.
                                        finish();
                                    }
                                })
                                .show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                // Creating Map String Params.
                Map<String, String> Parameter = new HashMap<String, String>();

                Parameter.put("short_bio", etShortBio.getText().toString());
                Parameter.put("USER_ID", USER_ID);

                return Parameter;
            }
        };
        StringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Send the StringRequest to the requestQueue.
        requestQueue.add(StringRequest);

        // Display progress dialog.
        swalDialog.show();
    }

    private void loadShortBio()
    {
        Log.e("loadUserInfo: ", "STARTED !");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_PROFILE_DETAILS, new Response.Listener<String>()
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

                        etShortBio.setText(jsonObject.getString("short_bio"));

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

                Parameter.put("USER_ID", USER_ID);

                return Parameter;
            }
        };
        // Add the StringRequest to Queue.
        Volley.newRequestQueue(this).add(stringRequest);
    }

}
