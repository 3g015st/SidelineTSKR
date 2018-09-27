package com.example.benedictlutab.sidelinetskr.modules.more;

import android.app.Activity;
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
import com.example.benedictlutab.sidelinetskr.modules.changePassword.changePasswordActivity;
import com.example.benedictlutab.sidelinetskr.modules.loadTaskHistory.taskHistoryActivity;
import com.example.benedictlutab.sidelinetskr.modules.login.loginActivity;
import com.example.benedictlutab.sidelinetskr.modules.updateAboutMe.updateAboutMeActivity;
import com.example.benedictlutab.sidelinetskr.modules.viewDashboard.dashboardActivity;
import com.example.benedictlutab.sidelinetskr.modules.wallet.myWallet.myWalletActivity;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Benedict Lutab on 6/17/2018.
 */

public class adapterMore extends RecyclerView.Adapter<adapterMore.ViewHolder>
{
    private ArrayList<String> arrlistItemNames = new ArrayList<>();
    private Activity context;

    public adapterMore(Activity context, ArrayList<String> arrlistItemNames)
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
                intent = new Intent(context, dashboardActivity.class);
                context.startActivity(intent);
                break;
            case 1:
                intent = new Intent(context, myWalletActivity.class);
                context.startActivity(intent);
                break;
            case 2:
                intent = new Intent(context, changePasswordActivity.class);
                context.startActivity(intent);
                break;
            case 3:
                intent = new Intent(context, updateAboutMeActivity.class);
                context.startActivity(intent);
                break;
            case 4:
                intent = new Intent(context, taskHistoryActivity.class);
                context.startActivity(intent);
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                Log.e("goToActivity: ", "SIGNED OUT!");
                displaySignoutDialog();
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

    private void displaySignoutDialog()
    {
        final SweetAlertDialog swalDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        swalDialog.setTitleText("Signing out?")
                .setContentText(" Are you sure that you want to sign out?  ")
                .setCancelText(" NO ")
                .setConfirmText(" YES ")
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener()
                {
                    @Override
                    public void onClick(SweetAlertDialog sDialog)
                    {
                        sDialog.hide();
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                {
                    @Override
                    public void onClick(SweetAlertDialog sDialog)
                    {
                        Intent intent = new Intent(context, loginActivity.class);
                        context.startActivity(intent);
                        context.finish();
                    }
                })
                .show();
    }
}
