package com.tdp.main.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;

public class FragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

    private ArrayList<Fragment> fragmentlist = new ArrayList<>();


    public void clear(){
        fragmentlist.clear();
    }

    public void add(Fragment fragment){
        fragmentlist.add(fragment);
    }

    public FragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentlist.get(position);
    }

    @Override
    public int getCount() {
        return fragmentlist.size();
    }
}
