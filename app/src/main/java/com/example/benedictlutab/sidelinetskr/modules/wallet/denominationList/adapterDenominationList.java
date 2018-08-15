package com.example.benedictlutab.sidelinetskr.modules.wallet.denominationList;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.benedictlutab.sidelinetskr.R;
import com.example.benedictlutab.sidelinetskr.helpers.fontStyleCrawler;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Benedict Lutab on 8/15/2018.
 */

public class adapterDenominationList extends RecyclerView.Adapter<adapterDenominationList.ViewHolder>
{
    private ArrayList<String> arrlistItemNames = new ArrayList<>();
    private Context context;

    public adapterDenominationList(Context context, ArrayList<String> arrlistItemNames)
    {
        this.context = context;
        this.arrlistItemNames = arrlistItemNames;
    }

    @Override
    public adapterDenominationList.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.denominationlist_layout_rv_denomination_list, parent, false);
        if(view != null)
        {
            fontStyleCrawler fontStyleCrawler = new fontStyleCrawler(view.getContext().getAssets(), "fonts/avenir.otf");
            fontStyleCrawler.replaceFonts((ViewGroup)view);
        }
        adapterDenominationList.ViewHolder viewHolder = new adapterDenominationList.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(adapterDenominationList.ViewHolder holder, final int position)
    {
        Log.d("onBindViewHolder", "onBindViewHolder: called");
        holder.tvDenomination.setText(arrlistItemNames.get(position));
        holder.clDenomination.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d("onBindViewHolder", "onClick: called "+arrlistItemNames.get(position)+" "+position);
                goToActivity(position);
            }
        });
    }

    public void goToActivity(final int position)
    {
        final Intent intent;
        switch(position)
        {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
        }
    }

    @Override
    public int getItemCount()
    {
        return arrlistItemNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.tvDenomination) TextView tvDenomination;
        @BindView(R.id.clDenomination) LinearLayout clDenomination;

        public ViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}



