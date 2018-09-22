package com.example.benedictlutab.sidelinetskr.modules.messages.loadChatRooms;

import android.app.Activity;
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
import com.example.benedictlutab.sidelinetskr.models.ChatRoom;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Benedict Lutab on 9/22/2018.
 */

public class adapterChatRooms extends RecyclerView.Adapter<adapterChatRooms.ViewHolder>
{
    public Activity context;
    public List<ChatRoom> chatRoomList;

    public adapterChatRooms(Activity context, List<ChatRoom> chatRoomList)
    {
        this.context = context;
        this.chatRoomList = chatRoomList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.loadchatrooms_layout_rv_chatrooms, null);
        if(view != null)
        {
            fontStyleCrawler fontStyleCrawler = new fontStyleCrawler(view.getContext().getAssets(), "fonts/avenir.otf");
            fontStyleCrawler.replaceFonts((ViewGroup)view);
        }
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        Log.e("onBindViewHolder:", "STARTED!");
        apiRouteUtil apiRouteUtil = new apiRouteUtil();

        ChatRoom chatRoom = chatRoomList.get(position);

        // Bind data.
        holder.TASK_GIVER_ID = chatRoom.getTask_giver_id();
        holder.TASKER_ID = chatRoom.getTasker_id();
        holder.TASK_ID = chatRoom.getTask_id();

        Log.e("IDs: ", holder.TASK_GIVER_ID + holder.TASKER_ID + holder.TASK_ID);

        holder.tvTaskgiverName.setText(chatRoom.getFirst_name() +" "+ chatRoom.getLast_name().substring(0, 1)+".");
        holder.tvTaskName.setText(chatRoom.getTitle());

        holder.IMAGE_URL = apiRouteUtil.DOMAIN + chatRoom.getProfile_picture();
        Log.e("IMAGE URL: ", holder.IMAGE_URL);

        //Bind fetched image url from server
        Picasso.with(context).load(holder.IMAGE_URL).fit().centerInside().into(holder.civTaskgiverPhoto);

    }

    @Override
    public int getItemCount()
    {
        return chatRoomList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.llChatRoom) LinearLayout llChatRoom;
        @BindView(R.id.civTaskgiverPhoto) CircleImageView civTaskgiverPhoto;
        @BindView(R.id.tvTaskgiverName) TextView tvTaskgiverName;
        @BindView(R.id.tvTaskName) TextView tvTaskName;

        String TASKER_ID, TASK_GIVER_ID, TASK_ID, IMAGE_URL;


        public ViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }



}
