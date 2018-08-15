package com.example.benedictlutab.sidelinetskr.modules.tasksFeed.viewTaskDetails;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
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
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

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

    @BindView(R.id.swipeRefLayout_id) SwipeRefreshLayout swipeRefLayout;

    @BindView(R.id.civTaskGiverPhoto) CircleImageView civTaskGiverPhoto;

    @BindView(R.id.vfTaskImages) ViewFlipper vfTaskImages;

    private final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
    private Date postedDate = new Date();

    private String TASK_ID, USER_ID, TASKER_ID, STATUS;
    private SharedPreferences sharedPreferences;
    private String[] taskImages = new String[2];

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
        if (sharedPreferences.contains("USER_ID"))
        {
            USER_ID = sharedPreferences.getString("USER_ID", "");
            Log.e("USER_ID:", USER_ID);
        }

        initSwipeRefLayout();

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

    @OnClick(R.id.btnPlaceOffer)
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
                    etMessage.setError("Message is less than 40 characters!");
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
                        tvTaskGiver.setText(jsonObject.getString("first_name") +" "+ jsonObject.getString("last_name"));
                        tvTaskDescription.setText(jsonObject.getString("description"));
                        tvTaskAddress.setText(jsonObject.getString("line_one") +", "+ jsonObject.get("city"));
                        tvTaskDueDate.setText(jsonObject.getString("date_time_end"));
                        tvTaskCategory.setText(jsonObject.getString("category_name"));
                        tvTaskFee.setText("PHP " + jsonObject.getString("task_fee"));

                        TASKER_ID = jsonObject.getString("tasker_id");
                        Log.e("TASKER_ID: ", TASKER_ID);

                        STATUS = jsonObject.getString("status");

                        // Fetch task photos
                        taskImages[0] = jsonObject.getString("image_one");
                        taskImages[1] = jsonObject.getString("image_two");

                        // Embed Photos
                        for (int i = 0; i < taskImages.length; i++)
                        {
                            setImageInViewFlipper(apiRouteUtil.DOMAIN + taskImages[i]);
                            Log.e("TASKIMGS: ", apiRouteUtil.DOMAIN + taskImages[i]);
                        }
                    }

                    if(isTaskAlreadyAssigned())
                    {
                        // No network connection.
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
        Log.e("sendOffer:", "START!");
        apiRouteUtil apiRouteUtil = new apiRouteUtil();

        // Init loading dialog.
        final SweetAlertDialog swalDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        swalDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        swalDialog.setTitleText("");
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
}

