package com.example.benedictlutab.sidelinetskr.modules.wallet.myWallet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.interfaces.HttpResponseCallback;
import com.braintreepayments.api.internal.HttpClient;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.example.benedictlutab.sidelinetskr.R;
import com.example.benedictlutab.sidelinetskr.helpers.apiRouteUtil;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;

public class topUpActivity extends AppCompatActivity
{
    private String TOKEN;
    private double AMOUNT, CONVERTED_AMOUNT;

    private final static int REQUEST_CODE = 1234;

    HashMap<String, String> paramsHash;

    private SharedPreferences sharedPreferences;
    private String USER_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mywallet_activity_top_up);
        ButterKnife.bind(this);

        // Get USER_ID
        sharedPreferences = getSharedPreferences("userPreferences", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("USER_ID"))
        {
            USER_ID = sharedPreferences.getString("USER_ID", "");
            Log.e("USER_ID:", USER_ID);
        }

        fetchPassedValues();
        getForeignExchange();

        new getToken().execute();
    }

    private void fetchPassedValues()
    {
        AMOUNT    =  Double.parseDouble(getIntent().getStringExtra("AMOUNT"));
        Log.e("PASSED VALUES: ", Double.toString(AMOUNT));
    }

    private void getForeignExchange()
    {
        Log.e("getForeignExchange: ", "STARTED !");

        final apiRouteUtil apiRouteUtil = new apiRouteUtil();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, apiRouteUtil.URL_FOREX, new Response.Listener<String>() {
            @Override
            public void onResponse(String ServerResponse)
            {
                try
                {
                    Log.e("SERVER RESPONSE: ", ServerResponse);
                    JSONObject complete    = new JSONObject(ServerResponse);
                    JSONObject quotesObj   = complete.getJSONObject("quotes");

                    for (int x = 0; x < quotesObj.length(); x++)
                    {
                        // Conversion to USD.
                        CONVERTED_AMOUNT = AMOUNT / Double.parseDouble(quotesObj.getString("USDPHP").toString());
                        CONVERTED_AMOUNT = Math.round( CONVERTED_AMOUNT * 100.0 ) / 100.0;
                        Log.e("AMOUNT CONVERTED: ", Double.toString(CONVERTED_AMOUNT));
                        Log.e("AMOUNT: ", Double.toString(AMOUNT));
                    }
                }
                catch (JSONException e)
                {
                    Log.e("CATCH RESPONSE: ", e.toString());
                    topUpActivity.this.finish();
                }
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        Log.e("ERROR RESPONSE: ", volleyError.toString());
                        TastyToast.makeText(getApplicationContext(), "Connection timeout, cannot get foreign exchange!", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
                        topUpActivity.this.finish();
                    }
                });

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private class getToken extends AsyncTask
    {
        final apiRouteUtil apiRouteUtil = new apiRouteUtil();

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] objects)
        {
            HttpClient httpClient = new HttpClient();
            httpClient.get(apiRouteUtil.URL_GET_TOKEN, new HttpResponseCallback()
            {
                @Override
                public void success(final String responseBody)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            TOKEN = responseBody;
                            Log.e("TOKEN: ", TOKEN);

                            submitPayment();
                        }
                    });
                }

                @Override
                public void failure(Exception exception)
                {
                    Log.e("GET TOKEN ERROR: ", exception.toString());
                }
            });
                return null;
        }

        @Override
        protected void onPostExecute(Object o)
        {
            super.onPostExecute(o);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == REQUEST_CODE)
        {
            if(resultCode == RESULT_OK)
            {
                DropInResult dropInResult = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                PaymentMethodNonce paymentMethodNonce = dropInResult.getPaymentMethodNonce();

                String nonce = paymentMethodNonce.getNonce();

                paramsHash = new HashMap<>();
                paramsHash.put("amount",  Double.toString(CONVERTED_AMOUNT));
                paramsHash.put("nonce", nonce);

                sendPayments();
            }
            else if(resultCode == RESULT_CANCELED)
            {
                TastyToast.makeText(this, "Cancelled transaction", TastyToast.LENGTH_LONG, TastyToast.WARNING).show();
                this.finish();
            }
            else
            {
                Exception error = (Exception)data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                Log.e("ERROR-OAR: ", error.toString());
            }
        }
    }

    private void sendPayments()
    {
        Log.e("sendPayments: ", "STARTED !");
        RequestQueue requestQueue = Volley.newRequestQueue(topUpActivity.this);

        final apiRouteUtil apiRouteUtil = new apiRouteUtil();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_CHECKOUT, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String ServerResponse)
            {
               if(ServerResponse.contains("Successful"))
               {
                   updateWalletBalance();
               }
               else
               {
                   TastyToast.makeText(topUpActivity.this, "Transaction failed!", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
                   topUpActivity.this.finish();
               }
               Log.e("SERVER RESPONSE: ", ServerResponse);
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        Log.e("ERROR RESPONSE: ", volleyError.toString());
                        TastyToast.makeText(getApplicationContext(), "Transaction cancelled due to slow internet connection", TastyToast.LENGTH_LONG, TastyToast.WARNING).show();
                        topUpActivity.this.finish();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
               Map<String, String> Parameter = new HashMap<>();
               for(String key:paramsHash.keySet())
               {
                   Parameter.put(key, paramsHash.get(key));
               }
               return Parameter;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                Map<String,String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Add the StringRequest to Queue.
        requestQueue.add(stringRequest);
    }

    private void submitPayment()
    {
        Log.e("submitPayment: ", "CALLED!");

        final SweetAlertDialog swalDialog = new SweetAlertDialog(topUpActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        swalDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        swalDialog.setTitleText(" ");
        swalDialog.setContentText("Please wait while we are loading your payment options :)");
        swalDialog.setCancelable(false);

        DropInRequest dropInRequest = new DropInRequest().clientToken(TOKEN);

        if(!TOKEN.isEmpty())
        {
            swalDialog.hide();
        }

        dropInRequest.disablePayPal();
        startActivityForResult(dropInRequest.getIntent(this), REQUEST_CODE);

    }

    private void updateWalletBalance()
    {
        Log.e("updateWalletBalance: ", "START!");
        apiRouteUtil apiRouteUtil = new apiRouteUtil();

        // Init loading dialog.
        final SweetAlertDialog swalDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        swalDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        swalDialog.setTitleText(" ");
        swalDialog.setContentText("Please wait while our system process your top-up transaction...");
        swalDialog.setCancelable(false);

        StringRequest StringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_UPDATE_WALLET,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String ServerResponse)
                    {
                        // Showing response message coming from server.
                        String SERVER_RESPONSE = ServerResponse.replaceAll("\\s+","");
                        Log.e("RESPONSE: ", SERVER_RESPONSE);

                        if(SERVER_RESPONSE.contains("SUCCESS"))
                        {
                            // Exit this activity then prompt success
                            swalDialog.hide();
                            TastyToast.makeText(getApplicationContext(), Double.toString(AMOUNT) + " has been loaded to your eWallet!", TastyToast.LENGTH_LONG, TastyToast.SUCCESS).show();
                            finish();
                        }
                        else
                        {
                            swalDialog.hide();
                            TastyToast.makeText(getApplicationContext(), "There has been an error in top up!", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        swalDialog.hide();
                        Log.e("ERROR RESPONSE: ", volleyError.toString());
                        TastyToast.makeText(getApplicationContext(), "Connection timeout due to slow internet connection", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                // Creating Map String Params.
                Map<String, String> Parameter = new HashMap<String, String>();

                Parameter.put("amount", Double.toString(AMOUNT));
                Parameter.put("USER_ID", USER_ID);

                return Parameter;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(topUpActivity.this);
        StringRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(StringRequest);
        swalDialog.show();
    }
}
