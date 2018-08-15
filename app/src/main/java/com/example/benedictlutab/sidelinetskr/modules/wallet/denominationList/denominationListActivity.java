package com.example.benedictlutab.sidelinetskr.modules.wallet.denominationList;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.benedictlutab.sidelinetskr.R;
import com.example.benedictlutab.sidelinetskr.helpers.fontStyleCrawler;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class denominationListActivity extends AppCompatActivity
{
    @BindView(R.id.btnBack) Button btnBack;

    private ArrayList<String> arrlistItemNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.denominationlist_activity_denomination_list);
        ButterKnife.bind(this);

        Log.e("onCreate","onCreate: on");
        initItemNames();

        // Change font style
        fontStyleCrawler fontStyleCrawler = new fontStyleCrawler(getAssets(), "fonts/avenir.otf");
        fontStyleCrawler.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));

        RecyclerView recyclerView = findViewById(R.id.rv_denomination);
        adapterDenominationList adapterDenominationList = new adapterDenominationList(denominationListActivity.this, arrlistItemNames);
        recyclerView.setAdapter(adapterDenominationList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @OnClick(R.id.btnBack)
    public void onBackPressed()
    {
        this.finish();
    }

    private void initItemNames()
    {
        Log.d("moreFragment","initItemNames: on");
        arrlistItemNames.add("100");
        arrlistItemNames.add("300");
        arrlistItemNames.add("500");
        arrlistItemNames.add("700");
        arrlistItemNames.add("1000");
    }
}
