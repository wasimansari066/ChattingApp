package com.example.chatingapp.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.chatingapp.Fragments.CallsFragment;
import com.example.chatingapp.Fragments.ChatsFragment;
import com.example.chatingapp.Fragments.StatusFragment;

public class FragmentsAdapter extends FragmentPagerAdapter {
    private int totalTabs;
    private Context context;

    public FragmentsAdapter(Context context, FragmentManager fm, int totalTabs, int behaviour) {
        super(fm, behaviour);
        this.totalTabs = totalTabs;
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new ChatsFragment();
        } else if (position == 1) {
            return new StatusFragment();
        } else if (position == 2) {
            return new CallsFragment();
        } else {
            return new ChatsFragment();
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}

