package com.example.benedictlutab.sidelinetskr.modules.wallet.myWallet;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.benedictlutab.sidelinetskr.R;
import com.example.benedictlutab.sidelinetskr.helpers.fontStyleCrawler;
import com.example.benedictlutab.sidelinetskr.models.loadHistory;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Benedict Lutab on 8/21/2018.
 */

public class adapterLoadHistory extends RecyclerView.Adapter<adapterLoadHistory.ViewHolder>
{

    public Context context;
    public List<loadHistory> loadHistories;

    public adapterLoadHistory(Context context, List<loadHistory> loadHistories)
    {
        this.context = context;
        this.loadHistories = loadHistories;
    }

    @Override
    public adapterLoadHistory.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.mywallet_layout_rv_history, null);
        if(view != null)
        {
            fontStyleCrawler fontStyleCrawler = new fontStyleCrawler(view.getContext().getAssets(), "fonts/avenir.otf");
            fontStyleCrawler.replaceFonts((ViewGroup)view);
        }
        return new adapterLoadHistory.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final adapterLoadHistory.ViewHolder holder, int position)
    {
        Log.e("onBindViewHolder:", "STARTED!");

        loadHistory loadHistory = loadHistories.get(position);

        // Bind data.
        holder.tvLoadedCoin.setText(loadHistory.getAmount());
        holder.tvDate.setText(loadHistory.getDate_time_sent());
    }

    @Override
    public int getItemCount()
    {
        return loadHistories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.tvLoadedCoin) TextView tvLoadedCoin;
        @BindView(R.id.tvDate) TextView tvDate;

        public ViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}


