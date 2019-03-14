package com.br.mytasksapp.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.br.mytasksapp.ui.fragment.AccessDataFragment;
import com.br.mytasksapp.ui.fragment.GeneralDataFragment;

public class MyDataAdapter extends FragmentPagerAdapter {
    private int numOfTabs;

    public MyDataAdapter(FragmentManager fm, int numOfTabs){
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return new GeneralDataFragment();
            case 1:
                return new AccessDataFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
