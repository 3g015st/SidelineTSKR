package com.example.benedictlutab.sidelinetskr.modules.messages.loadChatRooms;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.benedictlutab.sidelinetskr.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class chatRoomsFragment extends Fragment
{


    public chatRoomsFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.loadchatrooms_fragment_chat_rooms, container, false);
    }

}
