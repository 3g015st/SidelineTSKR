package com.example.benedictlutab.sidelinetskr.modules.viewTgProfile;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.benedictlutab.sidelinetskr.R;
import com.example.benedictlutab.sidelinetskr.helpers.apiRouteUtil;
import com.example.benedictlutab.sidelinetskr.helpers.fontStyleCrawler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.picasso.transformations.BlurTransformation;

public class tgProfileActivity extends AppCompatActivity
{
    @BindView(R.id.swipeRefLayout_id)
    SwipeRefreshLayout swipeRefLayout;
    @BindView(R.id.ivCoverPhoto) ImageView ivCoverPhoto;
    @BindView(R.id.civTaskerPhoto) CircleImageView civTaskerPhoto;
    @BindView(R.id.tvTaskerName) TextView tvTaskerName;
    @BindView(R.id.tvTskrName) TextView tvTskrName;
    @BindView(R.id.tvMemberSince) TextView tvMemberSince;
    @BindView(R.id.tvCity) TextView tvCity;
    @BindView(R.id.tvGender) TextView tvGender;
    @BindView(R.id.tvAge) TextView tvAge;

    final apiRouteUtil apiRouteUtil = new apiRouteUtil();

    private String USER_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewtgprofile_activity_tg_profile);
        ButterKnife.bind(this);

        Bundle Extras = getIntent().getExtras();
        if (Extras != null)
        {
            USER_ID = Extras.getString("USER_ID");
            Log.e("USER_ID: ", USER_ID);
        }

        changeFontFamily();
        initSwipeRefLayout();
    }

    private void initSwipeRefLayout()
    {
        swipeRefLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                // Fetching data from server
                fetchTaskgiverDetails();

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
                fetchTaskgiverDetails();

                swipeRefLayout.setRefreshing(false);
            }
        });
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

    private void fetchTaskgiverDetails()
    {
        Log.e("fetchTaskgiverDetails: ", "STARTED !");

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

                        // Load tasker's prof pic.
                        Picasso.with(tgProfileActivity.this).load(apiRouteUtil.DOMAIN + jsonObject.getString("profile_picture")).
                                fit().centerInside().into(civTaskerPhoto);

                        // Load cover photo.
                        Picasso.with(tgProfileActivity.this).load(apiRouteUtil.DOMAIN + jsonObject.getString("profile_picture")).transform(new BlurTransformation(tgProfileActivity.this, 25, 1)).
                                fit().centerInside().into(ivCoverPhoto);

                        tvTaskerName.setText(jsonObject.getString("first_name")+"'s"+" PROFILE");
                        tvTskrName.setText(jsonObject.getString("first_name") +" "+ jsonObject.getString("last_name").substring(0, 1)+".");
                        tvMemberSince.setText("Task giver since "+jsonObject.getString("date_created"));
                        tvCity.setText(jsonObject.getString("city")+ " City");
                        tvAge.setText(jsonObject.getString("age")+" years old");
                        tvGender.setText(jsonObject.getString("gender"));
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

                Parameter.put("USER_ID", USER_ID);

                return Parameter;
            }
        };
        // Add the StringRequest to Queue.
        Volley.newRequestQueue(this).add(stringRequest);
    }
}
