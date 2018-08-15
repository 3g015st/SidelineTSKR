package com.example.benedictlutab.sidelinetskr.modules.more;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.benedictlutab.sidelinetskr.helpers.fontStyleCrawler;
import com.example.benedictlutab.sidelinetskr.R;
import com.example.benedictlutab.sidelinetskr.modules.wallet.denominationList.denominationListActivity;
import com.example.benedictlutab.sidelinetskr.modules.wallet.myWallet.myWalletActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Benedict Lutab on 6/17/2018.
 */

public class adapterMore extends RecyclerView.Adapter<adapterMore.ViewHolder>
{
    private ArrayList<String> arrlistItemNames = new ArrayList<>();
    private Context context;

    public adapterMore(Context context, ArrayList<String> arrlistItemNames)
    {
        this.context = context;
        this.arrlistItemNames = arrlistItemNames;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.more_layout_rv_more, parent, false);
        if(view != null)
        {
            fontStyleCrawler fontStyleCrawler = new fontStyleCrawler(view.getContext().getAssets(), "fonts/avenir.otf");
            fontStyleCrawler.replaceFonts((ViewGroup)view);
        }
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position)
    {
        Log.d("onBindViewHolder", "onBindViewHolder: called");
        holder.tvMoreItem.setText(arrlistItemNames.get(position));
        holder.clMore.setOnClickListener(new View.OnClickListener()
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
                intent = new Intent(context, myWalletActivity.class);
                context.startActivity(intent);
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
        @BindView(R.id.tvMoreItem) TextView tvMoreItem;
        @BindView(R.id.clMore)
        ConstraintLayout clMore;

        public ViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
