package com.coderstory.Purify.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.List;

public class TabFragmentAdapter extends FragmentPagerAdapter {

    private Bundle args;
    private List<Fragment> fragments;

    TabFragmentAdapter(FragmentManager fm, List<Fragment> fragments, Bundle args) {
        super(fm);
        this.fragments = fragments;
        this.args = args;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = fragments.get(position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    @NonNull
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }
}
