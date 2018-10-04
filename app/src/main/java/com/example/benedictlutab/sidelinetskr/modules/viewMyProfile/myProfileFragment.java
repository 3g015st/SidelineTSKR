package com.example.benedictlutab.sidelinetskr.modules.viewMyProfile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.benedictlutab.sidelinetskr.models.Badge;
import com.example.benedictlutab.sidelinetskr.models.Evaluation;
import com.example.benedictlutab.sidelinetskr.models.Skill;
import com.example.benedictlutab.sidelinetskr.modules.viewEvaluation.evaluationActivity;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.picasso.transformations.BlurTransformation;

/**
 * A simple {@link Fragment} subclass.
 */
public class myProfileFragment extends Fragment
{
    private View rootView;

    @BindView(R.id.swipeRefLayout_id) SwipeRefreshLayout swipeRefLayout;
    @BindView(R.id.ivCoverPhoto) ImageView ivCoverPhoto;
    @BindView(R.id.civTaskerPhoto) CircleImageView civTaskerPhoto;
    @BindView(R.id.tvFullName) TextView tvFullName;
    @BindView(R.id.tvMemberSince) TextView tvMemberSince;
    @BindView(R.id.tvEmail) TextView tvEmail;
    @BindView(R.id.tvMobileNumber) TextView tvMobileNumber;
    @BindView(R.id.tvLineOne) TextView tvLineOne;
    @BindView(R.id.tvCity) TextView tvCity;
    @BindView(R.id.tvGender) TextView tvGender;
    @BindView(R.id.tvAge) TextView tvAge;
    @BindView(R.id.tvShortBio) TextView tvShortBio;
    @BindView(R.id.tvAverageRating) TextView tvAverageRating;
    @BindView(R.id.tvNoLatestReviews) TextView tvNoLatestReviews;
    @BindView(R.id.tvNoBadges) TextView tvNoBadges;

    @BindView(R.id.btnViewReviews) Button btnViewReviews;

    apiRouteUtil apiRouteUtil = new apiRouteUtil();

    private String USER_ID;
    private SharedPreferences sharedPreferences;


    @BindView(R.id.rv_featuredrevs) RecyclerView rv_featuredrevs;
    private List<Evaluation> evaluationList = new ArrayList<>();
    private adapterLatestEval adapterLatestEval;
    private int lsEval;

    @BindView(R.id.rv_skills) RecyclerView rvSkills;
    private List<Skill> skillList = new ArrayList<>();
    private adapterDisplaySkills adapterDisplaySkills;

    private int lsSkills;

    @BindView(R.id.rv_badges) RecyclerView rvBadges;
    private List<Badge> badgeList = new ArrayList<>();
    private adapterBadges adapterBadges;

    private int lsBadges;


    public static myProfileFragment newInstance()
    {
        myProfileFragment myProfileFragment = new myProfileFragment();
        return myProfileFragment;
    }

    public myProfileFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Log.e("onCreateView: ","STARTED!");

        rootView = inflater.inflate(R.layout.viewmyprofile_fragment_my_profile, container, false);
        ButterKnife.bind(this, rootView);

        // Get USER_ID
        sharedPreferences = this.getActivity().getSharedPreferences("userPreferences", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("USER_ID"))
        {
            USER_ID = sharedPreferences.getString("USER_ID", "");
            Log.e("USER_ID:", USER_ID);
        }

        initSwipeRefLayout();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        fontStyleCrawler fontStyleCrawler = new fontStyleCrawler(getActivity().getAssets(), "fonts/avenir.otf");
        fontStyleCrawler.replaceFonts((ViewGroup) this.getView());
    }

    private void initSwipeRefLayout()
    {
        swipeRefLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                // Fetching data from server
                fetchMyDetails();
                fetchEvalList();
                fetchSkills();
                fetchBadges();
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
                fetchMyDetails();
                fetchEvalList();
                fetchSkills();
                fetchBadges();

                swipeRefLayout.setRefreshing(false);
            }
        });
    }

    private void fetchMyDetails()
    {
        Log.e("fetchTaskerDetails: ", "STARTED !");

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
                        Picasso.with(getActivity()).load(apiRouteUtil.DOMAIN + jsonObject.getString("profile_picture")).memoryPolicy(MemoryPolicy.NO_CACHE)
                                .networkPolicy(NetworkPolicy.NO_CACHE).fit().centerInside().into(civTaskerPhoto);

                        // Load cover photo.
                        Picasso.with(getActivity()).load(apiRouteUtil.DOMAIN + jsonObject.getString("profile_picture")).transform(new BlurTransformation(getActivity(), 25, 1)).memoryPolicy(MemoryPolicy.NO_CACHE)
                                .networkPolicy(NetworkPolicy.NO_CACHE).fit().centerInside().into(ivCoverPhoto);

                        tvAverageRating.setText(jsonObject.getString("avg_rating") +" "+ "Average Rating");
                        tvFullName.setText(jsonObject.getString("first_name") +" "+ jsonObject.getString("last_name"));
                        tvMemberSince.setText("Tasker since " + jsonObject.getString("date_created"));
                        tvEmail.setText(jsonObject.getString("email"));
                        tvMobileNumber.setText(jsonObject.getString("mobile_number"));
                        tvLineOne.setText(jsonObject.getString("line_one"));
                        tvCity.setText(jsonObject.getString("city"));
                        tvAge.setText(jsonObject.getString("age")+" years old");
                        tvGender.setText(jsonObject.getString("gender"));
                        tvShortBio.setText(jsonObject.getString("short_bio"));
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
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

    private void initrvEval()
    {
        Log.d("initrvEval: ", String.valueOf(lsEval));

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_featuredrevs.setLayoutManager(layoutManager);

        adapterLatestEval = new adapterLatestEval(getActivity(), evaluationList);
        rv_featuredrevs.setAdapter(adapterLatestEval);

        if (lsEval == 0)
        {
            Log.e("initRecyclerView: ", "No latest");
            rv_featuredrevs.setVisibility(View.GONE);
            tvNoLatestReviews.setVisibility(View.VISIBLE);
        }
        else
        {
            Log.e("initRecyclerView: ", "With latest");
            rv_featuredrevs.setVisibility(View.VISIBLE);
            tvNoLatestReviews.setVisibility(View.GONE);
        }
    }

    private void fetchEvalList()
    {
        Log.e("fetchEvalList: ", "STARTED!");
        evaluationList.clear();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_LOAD_EVAL, new Response.Listener<String>()
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
                        evaluationList.add(new Evaluation(jsonObject.getString("full_name"),
                                jsonObject.getString("profile_picture"),
                                jsonObject.getString("review"),
                                jsonObject.getString("date_time_sent"),
                                jsonObject.getString("rating"),
                                jsonObject.getString("title")));
                        lsEval = evaluationList.size();
                        Log.e("lsEval: ", String.valueOf(lsEval));
                    }
                    initrvEval();
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

                Parameter.put("receiver_id", USER_ID);
                Parameter.put("SWITCH", "LATEST");

                return Parameter;
            }
        };

        // Add the StringRequest to Queue.
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

    private void initrvSkills()
    {
        Log.d("lsSkills: ", String.valueOf(lsSkills));

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvSkills.setLayoutManager(layoutManager);

        adapterDisplaySkills = new adapterDisplaySkills(getActivity(), skillList);
        rvSkills.setAdapter(adapterDisplaySkills);

        if (lsSkills == 0)
        {
            Log.e("initRecyclerView: ", "GONE-VISIBLE");
            rvSkills.setVisibility(View.GONE);
        }
        else
        {
            Log.e("initRecyclerView: ", "VISIBLE-GONE");
            rvSkills.setVisibility(View.VISIBLE);
        }
    }

    private void fetchSkills()
    {
        Log.e("fetchSkills: ", "STARTED!");
        skillList.clear();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_MY_SKILLS, new Response.Listener<String>()
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
                        skillList.add(new Skill(jsonObject.getString("skill_id"),
                                jsonObject.getString("name")));
                        lsSkills = skillList.size();
                        Log.e("lsSkills: ", String.valueOf(lsSkills));
                    }
                    initrvSkills();
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

                Parameter.put("tasker_id", USER_ID);

                return Parameter;
            }
        };

        // Add the StringRequest to Queue.
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

    private void initrvBadges()
    {
        Log.d("initrvBadges: ", String.valueOf(lsBadges));

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvBadges.setLayoutManager(layoutManager);

        adapterBadges = new adapterBadges(getActivity(), badgeList);
        rvBadges.setAdapter(adapterBadges);

        if (lsBadges == 0)
        {
            Log.e("initRecyclerView: ", "GONE-VISIBLE");
            rvBadges.setVisibility(View.GONE);
            tvNoBadges.setVisibility(View.VISIBLE);
        }
        else
        {
            Log.e("initRecyclerView: ", "VISIBLE-GONE");
            rvBadges.setVisibility(View.VISIBLE);
            tvNoBadges.setVisibility(View.GONE);
        }
    }

    private void fetchBadges()
    {
        Log.e("fetchBadges: ", "STARTED!");
        badgeList.clear();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_LOAD_BADGE, new Response.Listener<String>()
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
                        badgeList.add(new Badge(jsonObject.getString("badge_id"),
                                jsonObject.getString("name"),
                                jsonObject.getString("description"),
                                jsonObject.getString("image")));
                        lsBadges = badgeList.size();
                        Log.e("lsBadges: ", String.valueOf(lsBadges));
                    }
                    initrvBadges();
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

                Parameter.put("tasker_id", USER_ID);

                return Parameter;
            }
        };

        // Add the StringRequest to Queue.
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

    @OnClick(R.id.btnViewReviews)
    public void viewEval()
    {
        Intent intent = new Intent(getActivity(), evaluationActivity.class);
        startActivity(intent);
    }
}
