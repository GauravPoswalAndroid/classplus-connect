package com.fankonnect.app.login.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private float pageWidth = 1.f;
    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> fragmentTitleList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public void setFragmentTitleList(List<String> fragmentTitleList) {
        this.fragmentTitleList = fragmentTitleList;
        notifyDataSetChanged();
    }

    public void addFragment(Fragment fragment, String title) {
        fragmentList.add(fragment);
        fragmentTitleList.add(title);
    }

    public void addFragment(Fragment fragment, String title, int index) {
        fragmentList.add(index, fragment);
        fragmentTitleList.add(index, title);
    }

    public void addFragment(Fragment fragment) {
        fragmentList.add(fragment);
    }

    public void addFragmentTitle(String title) {
        fragmentTitleList.add(title);
    }

    public void removeAllFragments() {
        fragmentList.clear();
        fragmentTitleList.clear();
    }

    public int getItemIndexFromName(String name) {
        return fragmentTitleList.indexOf(name);
    }

    public void setPageWidth(float pageWidth) {
        this.pageWidth = pageWidth;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitleList.get(position);
    }

    @Override
    public float getPageWidth(int position) {
        return this.pageWidth;
    }

    public static Fragment findFragment(FragmentManager fragmentManager, int viewId, int index) {
        return fragmentManager.findFragmentByTag(ViewPagerAdapter.constructFragmentName(viewId, index));
    }

    public static String constructFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }
}