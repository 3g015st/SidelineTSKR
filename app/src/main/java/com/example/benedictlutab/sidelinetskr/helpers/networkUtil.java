package com.example.benedictlutab.sidelinetskr.helpers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Benedict Lutab on 8/25/2018.
 */

public class networkUtil
{
    public Activity activity;

   public networkUtil(Activity activity)
   {
       this.activity = activity;
   }

    public void showNetworkError()
    {
        Log.e("showNetworkError:", "STARTED!");
        new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE).setTitleText("NETWORK ERROR").setContentText("It seems there is a problem in our servers, please try again later :(")
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                {
                    @Override
                    public void onClick(SweetAlertDialog sDialog)
                    {
                        // Exit application.
                        activity.finish();
                    }
                })
                .show();
    }
}
