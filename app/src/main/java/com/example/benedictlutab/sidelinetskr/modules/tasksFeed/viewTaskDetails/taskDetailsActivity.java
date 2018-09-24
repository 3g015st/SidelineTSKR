package com.example.benedictlutab.sidelinetskr.modules.tasksFeed.viewTaskDetails;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.benedictlutab.sidelinetskr.R;
import com.example.benedictlutab.sidelinetskr.helpers.apiRouteUtil;
import com.example.benedictlutab.sidelinetskr.helpers.fontStyleCrawler;
import com.example.benedictlutab.sidelinetskr.helpers.validationUtil;
import com.example.benedictlutab.sidelinetskr.modules.openTransactionSummary.openTransSummActivity;
import com.example.benedictlutab.sidelinetskr.modules.viewTgProfile.tgProfileActivity;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Picasso;
import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class taskDetailsActivity extends AppCompatActivity
{
    @BindView(R.id.btnBack) Button btnBack;
    @BindView(R.id.btnPlaceOffer) Button btnPlaceOffer;

    @BindView(R.id.tvTaskTitle) TextView tvTaskTitle;
    @BindView(R.id.tvTaskGiver) TextView tvTaskGiver;
    @BindView(R.id.tvTaskPostedDate) TextView tvTaskPostedDate;
    @BindView(R.id.tvTaskAddress) TextView tvTaskAddress;
    @BindView(R.id.tvTaskDueDate) TextView tvTaskDueDate;
    @BindView(R.id.tvTaskFee) TextView tvTaskFee;
    @BindView(R.id.tvTaskCategory) TextView tvTaskCategory;
    @BindView(R.id.tvTaskDescription) TextView tvTaskDescription;
    @BindView(R.id.tvTaskStatus) TextView tvTaskStatus;

    @BindView(R.id.swipeRefLayout_id) SwipeRefreshLayout swipeRefLayout;

    @BindView(R.id.civTaskGiverPhoto) CircleImageView civTaskGiverPhoto;

    @BindView(R.id.vfTaskImages) ViewFlipper vfTaskImages;

    @BindView(R.id.btnStart) Button btnStart;

    private final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
    private Date postedDate = new Date();

    private String TASK_ID, TASK_GIVER_ID, USER_ID, TASKER_ID, STATUS, BALANCE, COMM_FEE;
    private SharedPreferences sharedPreferences;
    private String[] taskImages = new String[2];

    private SmsVerifyCatcher smsVerifyCatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewtaskdetails_activity_task_details);
        ButterKnife.bind(this);

        changeFontFamily();
        fetchPassedValues();

        // Get USER_ID
        sharedPreferences = getSharedPreferences("userPreferences", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("USER_ID") && sharedPreferences.contains("BALANCE"))
        {
            USER_ID = sharedPreferences.getString("USER_ID", "");
            BALANCE = sharedPreferences.getString("BALANCE", "");

            Log.e("USER_ID:", USER_ID);
            Log.e("BALANCE:", BALANCE);
        }

        initSwipeRefLayout();

        //init SmsVerifyCatcher
        smsVerifyCatcher = new SmsVerifyCatcher(this, new OnSmsCatchListener<String>()
        {
            @Override
            public void onSmsCatch(String message)
            {
                String code = parseCode(message);
                Log.e("TRANS_CODE: ", code);
                openTransSummary(code);
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        smsVerifyCatcher.onStart();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        smsVerifyCatcher.onStop();
    }

    /**
     * need for Android 6 real time permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private String parseCode(String message)
    {
        Pattern p = Pattern.compile("\\b\\d{5}\\b");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find())
        {
            code = m.group(0);
        }
        return code;
    }

    private void changeFontFamily()
    {
        // Change Font Style.
        fontStyleCrawler fontStyleCrawler = new fontStyleCrawler(getAssets(), "fonts/avenir.otf");
        fontStyleCrawler.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));
    }

    @OnClick(R.id.btnBack)
    public void onBackPressed()
    {
        this.finish();
    }

    public void showPlaceOfferDialog()
    {
        Log.e("showPlaceOfferDialog:", "START!");

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(taskDetailsActivity.this);
        View view = getLayoutInflater().inflate(R.layout.viewtaskdetails_dialog_placeanoffer, null);

        dialogBuilder.setView(view);
        final AlertDialog offerDialog = dialogBuilder.create();
        offerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final TextView tvDialogTitle = view.findViewById(R.id.tvDialogTitle);
        final TextInputLayout tilAmount = view.findViewById(R.id.tilAmount);
        final TextInputLayout tilMessage = view.findViewById(R.id.tilMessage);
        final EditText etAmount = view.findViewById(R.id.etAmount);
        final EditText etMessage = view.findViewById(R.id.etMessage);
        final Button btnSubmit =  view.findViewById(R.id.btnSubmit);

        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/avenir.otf");
        tvDialogTitle.setTypeface(typeface);
        tilAmount.setTypeface(typeface);
        tilMessage.setTypeface(typeface);
        etAmount.setTypeface(typeface);
        etMessage.setTypeface(typeface);
        btnSubmit.setTypeface(typeface);

        offerDialog.show();

        btnSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                validationUtil validationUtil = new validationUtil();
                if(validationUtil.isValidOfferMessage(etMessage) && !etAmount.getText().toString().isEmpty())
                {
                    sendOffer(etAmount.getText().toString(), etMessage.getText().toString());
                }
                if(!validationUtil.isValidOfferMessage(etMessage))
                {
                    etMessage.setError("Message is less than 20 characters!");
                }
                if(etAmount.getText().toString().isEmpty())
                {
                    etAmount.setError("Amount is required!");
                }
            }
        });
    }

    private void fetchPassedValues()
    {
        TASK_ID = getIntent().getStringExtra("TASK_ID");
        Log.e("fetchPValues: ", "Got the data!");
    }

    private void initSwipeRefLayout()
    {
        swipeRefLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                // Fetching data from server
                fetchTaskDetails();
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
                fetchTaskDetails();
                swipeRefLayout.setRefreshing(false);
            }
        });
    }

    private void fetchTaskDetails()
    {
        Log.e("fetchTaskDetails: ", "STARTED !");

        final apiRouteUtil apiRouteUtil = new apiRouteUtil();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_TASK_DETAILS, new Response.Listener<String>()
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

                        //Convert date to ...ago
                        PrettyTime prettyTime = new PrettyTime(Locale.getDefault());
                        try
                        {
                            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Hong_Kong"));
                            postedDate = simpleDateFormat.parse(jsonObject.getString("date_time_posted"));
                            tvTaskPostedDate.setText(prettyTime.format(postedDate));
                        }
                        catch (ParseException e)
                        {
                            e.printStackTrace();
                        }

                        // Load task giver's prof pic.
                        Picasso.with(taskDetailsActivity.this).load(apiRouteUtil.DOMAIN + jsonObject.getString("profile_picture")).
                                fit().centerInside().into(civTaskGiverPhoto);

                        tvTaskTitle.setText(jsonObject.getString("title"));

                        tvTaskGiver.setText(jsonObject.getString("first_name") +" "+ jsonObject.getString("last_name").substring(0, 1)+".");

                        tvTaskDescription.setText(jsonObject.getString("description"));
                        tvTaskAddress.setText(jsonObject.getString("line_one") +", "+ jsonObject.get("city"));
                        tvTaskDueDate.setText(jsonObject.getString("due_date"));
                        tvTaskCategory.setText(jsonObject.getString("category_name"));

                        // COMPUTE OVERALL TASK FEE
                        COMM_FEE = jsonObject.getString("comm_fee");
                        Log.e("COMM_FEE: ", COMM_FEE);

                        Float TOTAL_FEE = Float.parseFloat(COMM_FEE) + Float.parseFloat(jsonObject.getString("task_fee"));
                        tvTaskFee.setText("PHP " + String.valueOf(TOTAL_FEE));

                        TASKER_ID = jsonObject.getString("tasker_id");
                        Log.e("TASKER_ID: ", TASKER_ID);

                        TASK_GIVER_ID = jsonObject.getString("task_giver_id");
                        Log.e("TASK_GIVER_ID: ", TASK_GIVER_ID);

                        STATUS = jsonObject.getString("status");
                        tvTaskStatus.setText(STATUS);

                        // Fetch task photos
                        taskImages[0] = jsonObject.getString("image_one");
                        taskImages[1] = jsonObject.getString("image_two");

                        // Embed Photos
                        for (int i = 0; i < taskImages.length; i++)
                        {
                            setImageInViewFlipper(apiRouteUtil.DOMAIN + taskImages[i]);
                            Log.e("TASKIMGS: ", apiRouteUtil.DOMAIN + taskImages[i]);
                        }

                        initViewVisiblity();
                        isOfferOpen();
                    }

                    if(isTaskAlreadyAssigned())
                    {
                        new SweetAlertDialog(taskDetailsActivity.this, SweetAlertDialog.ERROR_TYPE).setTitleText("ERROR").setContentText("It seems that there is already an assigned tasker for this task :(")
                                .setConfirmText("OK")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                                {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        // Exit application.
                                        finish();
                                    }
                                })
                                .show();
                    }
                    else if(isTaskAlreadyCompleted())
                    {
                        new SweetAlertDialog(taskDetailsActivity.this, SweetAlertDialog.ERROR_TYPE).setTitleText("ERROR").setContentText("It seems that this task is already completed by you.")
                                .setConfirmText("OK")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                                {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        // Exit application.
                                        finish();
                                    }
                                })
                                .show();
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

                Parameter.put("TASK_ID", TASK_ID);

                return Parameter;
            }
        };
        // Add the StringRequest to Queue.
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void sendOffer(final String amount, final String message)
    {
        Log.e("sendOffer: ", "START!");
        apiRouteUtil apiRouteUtil = new apiRouteUtil();

        // Init loading dialog.
        final SweetAlertDialog swalDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        swalDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        swalDialog.setTitleText(" ");
        swalDialog.setContentText("Please wait while your offer is sending...");
        swalDialog.setCancelable(false);

        StringRequest StringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_SEND_OFFER,
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
                            TastyToast.makeText(getApplicationContext(), "Your offer has been successfully sent!", TastyToast.LENGTH_LONG, TastyToast.SUCCESS).show();
                            finish();
                        }
                        else
                        {
                            // Prompt error
                            swalDialog.hide();
                            TastyToast.makeText(getApplicationContext(), "There has been an error sending your offer!", TastyToast.LENGTH_LONG, TastyToast.SUCCESS).show();
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

                Parameter.put("amount", amount);
                Parameter.put("message", message);
                Parameter.put("tasker_id", USER_ID);
                Parameter.put("task_id", TASK_ID);

                return Parameter;
            }
        };
        // Initialize requestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(taskDetailsActivity.this);

        // Send the StringRequest to the requestQueue.
        requestQueue.add(StringRequest);

        // Display progress dialog.
        swalDialog.show();
    }

    @OnClick(R.id.civTaskGiverPhoto)
    public void viewTgProfile()
    {
        Intent intent = new Intent(this, tgProfileActivity.class);
        intent.putExtra("USER_ID", TASK_GIVER_ID);
        startActivity(intent);
        finish();
    }

    private boolean isTaskAlreadyAssigned()
    {
        Log.e("isTaskAlreadyAssigned: ", "STARTED!");
        Log.e("USER_ID: ", USER_ID);
        Log.e("STATUS: ", STATUS);
        if(!USER_ID.equals(TASKER_ID) && STATUS.equals("ASSIGNED"))
        {
            return true;
        }
        else
            return false;
    }

    private boolean isTaskAlreadyCompleted()
    {
        Log.e("isTaskAlreadyComplted: ", "STARTED!");
        Log.e("USER_ID: ", USER_ID);
        Log.e("STATUS: ", STATUS);
        if(USER_ID.equals(TASKER_ID) && STATUS.equals("COMPLETED"))
        {
            return true;
        }
        else
            return false;
    }

    private void setImageInViewFlipper(String imgUrl)
    {
        ImageView image = new ImageView(getApplicationContext());
        Picasso.with(this).load(imgUrl).into(image);
        vfTaskImages.addView(image);

        // Declare in and out animations and load them using AnimationUtils class
        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);

        // set the animation type's to ViewFlipper
        vfTaskImages.setInAnimation(in);
        vfTaskImages.setOutAnimation(out);

        // set interval time for flipping between views
        vfTaskImages.setFlipInterval(5000);
        // set auto start for flipping between views
        vfTaskImages.setAutoStart(true);
        vfTaskImages.startFlipping();
    }

    private void initViewVisiblity()
    {
        if(!STATUS.equals("AVAILABLE"))
        {
            Log.e("btnPlaceOffer: ", "GONE!");
            btnPlaceOffer.setVisibility(View.GONE);
        }
        else
        {
            Log.e("btnPlaceOffer: ", "VISIBLE!");
            btnPlaceOffer.setVisibility(View.VISIBLE);
        }

        if(STATUS.equals("ASSIGNED"))
        {
            Log.e("btnStart: ", "VISIBLE!");
            btnStart.setVisibility(View.VISIBLE);
        }
        else
        {
            Log.e("btnStart: ", "GONE!");
            btnStart.setVisibility(View.GONE);
        }

    }

    @OnClick(R.id.btnStart)
    public void startTask()
    {
        if(STATUS.equals("ASSIGNED"))
        {
            // Send post request to update task status to ON-GOING and send sms to task giver.
            Log.e("startTask: ", "STARTED!");
            apiRouteUtil apiRouteUtil = new apiRouteUtil();

            //Display loading...

            StringRequest StringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_START_TASK,
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
                                TastyToast.makeText(getApplicationContext(), "Task has been started :)!", TastyToast.LENGTH_LONG, TastyToast.SUCCESS).show();
                                initSwipeRefLayout();
                            }
                            else
                            {
                                Log.e("startTask: ", "ERROR!");
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
                    Parameter.put("task_giver_id", TASK_GIVER_ID);
                    Parameter.put("task_id", TASK_ID);

                    return Parameter;
                }
            };
            // Initialize requestQueue.
            RequestQueue requestQueue = Volley.newRequestQueue(taskDetailsActivity.this);

            // Send the StringRequest to the requestQueue.
            requestQueue.add(StringRequest);
        }
    }

    private void openTransSummary(final String code)
    {
        Log.e("openTransSummary: ", "STARTED!");

        apiRouteUtil apiRouteUtil = new apiRouteUtil();

        StringRequest StringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_VERIFY_CODE,
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
                            // Open transaction summary activity
                            Log.e("openTransSumm: ", "SUCCESS!");

                            Intent intent = new Intent(getApplicationContext(), openTransSummActivity.class);
                            intent.putExtra("TASK_ID", TASK_ID);
                            intent.putExtra("TASK_GIVER_ID", TASK_GIVER_ID);
                            intent.putExtra("transaction_code", code);
                            finish();
                            startActivity(intent);
                        }
                        else
                        {
                            Log.e("openTransSummary: ", "ERROR!");
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
                Parameter.put("transaction_code", code);
                Parameter.put("task_id", TASK_ID);

                return Parameter;
            }
        };
        // Initialize requestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(taskDetailsActivity.this);

        // Send the StringRequest to the requestQueue.
        requestQueue.add(StringRequest);
    }

    private void isOfferOpen()
    {
        Log.e("isOfferOpen: ", "STARTED!!!");
        Log.e("iOO-BAL: ", BALANCE);
        Log.e("COMM_FEE: ", COMM_FEE);

        // Display prompt that open is closed then hide send offer button.
        if(Double.parseDouble(BALANCE) < Double.parseDouble(COMM_FEE))
        {
            btnPlaceOffer.setVisibility(View.GONE);
            final SweetAlertDialog swalDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
            swalDialog.setTitleText("Insufficient Balance").setContentText("Offer is temporarily closed due to the insufficient balance available (Required Balance: PHP " + COMM_FEE +" above.)")
                    .setConfirmText("OK")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                    {
                        @Override
                        public void onClick(SweetAlertDialog sDialog)
                        {
                            sDialog.hide();
                        }
                    })
                    .show();
        }
        else
        {
            Log.e("isOfferOpen: ", "OFFER IS OPEN!!!");
        }
    }

    @OnClick(R.id.btnPlaceOffer)
    public void getOfferCount()
    {
        Log.e("getOfferCount: ", "START!");
        Log.e("TASKER_ID: ", USER_ID);
        Log.e("TASK_ID: ", TASK_ID);

        apiRouteUtil apiRouteUtil = new apiRouteUtil();

        StringRequest StringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_GET_OFFER_COUNT,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String ServerResponse)
                    {
                        // Showing response message coming from server.
                        String SERVER_RESPONSE = ServerResponse.replaceAll("\\s+","");
                        Log.e("RESPONSE: ", SERVER_RESPONSE);

                        int MAX_COUNT = 1;

                        if(Integer.parseInt(SERVER_RESPONSE) > MAX_COUNT || Integer.parseInt(SERVER_RESPONSE) == MAX_COUNT)
                        {
                            showPromptOfferExceeded();
                        }
                        else if(Integer.parseInt(SERVER_RESPONSE) < MAX_COUNT)
                        {
                            showPlaceOfferDialog();
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
                Parameter.put("tasker_id", USER_ID);
                Parameter.put("task_id", TASK_ID);

                return Parameter;
            }
        };
        // Initialize requestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(taskDetailsActivity.this);

        // Send the StringRequest to the requestQueue.
        requestQueue.add(StringRequest);
    }

    private void showPromptOfferExceeded()
    {
        final SweetAlertDialog swalDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
        swalDialog.setTitleText("Offer Exceeded").setContentText("You have already placed an offer in this task!")
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                {
                    @Override
                    public void onClick(SweetAlertDialog sDialog)
                    {
                        sDialog.hide();
                    }
                })
                .show();
    }
}

