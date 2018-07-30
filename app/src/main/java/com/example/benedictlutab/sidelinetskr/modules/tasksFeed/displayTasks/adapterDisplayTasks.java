package com.example.benedictlutab.sidelinetskr.modules.tasksFeed.displayTasks;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.benedictlutab.sidelinetskr.R;
import com.example.benedictlutab.sidelinetskr.helpers.apiRouteUtil;
import com.example.benedictlutab.sidelinetskr.helpers.fontStyleCrawler;
import com.example.benedictlutab.sidelinetskr.models.availableTask;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Benedict Lutab on 7/30/2018.
 */

public class adapterDisplayTasks extends RecyclerView.Adapter<adapterDisplayTasks.ViewHolder>
{
    private Context context;
    private List<availableTask> availableTaskList;

    public adapterDisplayTasks(Context context, List<availableTask> availableTaskList)
    {
        this.context = context;
        this.availableTaskList = availableTaskList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.displaytasks_layout_rv_tasks, null);
        if(view != null)
        {
            fontStyleCrawler fontStyleCrawler = new fontStyleCrawler(view.getContext().getAssets(), "fonts/ralewayRegular.ttf");
            fontStyleCrawler.replaceFonts((ViewGroup)view);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        Log.e("onBindViewHolder:", "STARTED!");
        apiRouteUtil apiRouteUtil = new apiRouteUtil();

        availableTask availableTask = availableTaskList.get(position);

        // Bind data.
        holder.TASK_ID = availableTask.getTask_id();

        holder.tvTaskTitle.setText(availableTask.getTitle());
        holder.tvTaskAddress.setText(availableTask.getLine_one() +", "+ availableTask.getCity());
        holder.tvTaskDueDate.setText(availableTask.getDate_time_end());
        holder.tvTaskStatus.setText(availableTask.getStatus());
        holder.tvTaskFee.setText("PHP " + availableTask.getTask_fee());

        holder.IMAGE_URL = apiRouteUtil.DOMAIN + "api" + availableTask.getProfile_picture();
        Log.e("IMAGE URL: ", holder.IMAGE_URL);

        //Bind fetched image url from server
        Picasso.with(context).load(holder.IMAGE_URL).fit().centerInside().into(holder.civTaskGiverPhoto);

        holder.llTask.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Go to Task Details.
                Log.e("TASK ID: ", holder.TASK_ID);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return availableTaskList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.llTask) LinearLayout llTask;
        @BindView(R.id.tvTaskTitle) TextView tvTaskTitle;
        @BindView(R.id.tvTaskAddress) TextView tvTaskAddress;
        @BindView(R.id.tvTaskDueDate) TextView tvTaskDueDate;
        @BindView(R.id.tvTaskStatus) TextView tvTaskStatus;
        @BindView(R.id.tvTaskFee) TextView tvTaskFee;
        @BindView(R.id.civTaskGiverPhoto) CircleImageView civTaskGiverPhoto;

        private String TASK_ID, IMAGE_URL;

        public ViewHolder(final View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
