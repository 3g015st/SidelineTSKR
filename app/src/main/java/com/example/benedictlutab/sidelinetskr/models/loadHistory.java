package com.example.benedictlutab.sidelinetskr.models;

/**
 * Created by Benedict Lutab on 8/21/2018.
 */

public class loadHistory
{
    String amount, date_time_sent;

    public loadHistory(String amount, String date_time_sent)
    {
        this.amount = amount;
        this.date_time_sent = date_time_sent;
    }

    public String getAmount() {
        return amount;
    }

    public String getDate_time_sent() {
        return date_time_sent;
    }
}
