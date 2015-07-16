package com.v2tech.widget;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class VideoShowFragmentAdapter extends FragmentPagerAdapter {

	private int fragmentCounts;
	private VideoShowFragment[] fragments;
	private List<VideoShowFragment> fragmentsList;
	
	public VideoShowFragmentAdapter(FragmentManager fm, int fragmentCounts) {
		super(fm);
		this.fragmentCounts = fragmentCounts;
		fragmentsList = new ArrayList<VideoShowFragment>();
		fragments  = new VideoShowFragment[fragmentCounts];
	}

	@Override
	public Fragment getItem(int position) {
		if (fragments[position] == null) {
			fragments[position] = new VideoShowFragment();
			fragments[position].setIndex(position + 1);
		}
		return fragments[position];
	}

	@Override
	public int getCount() {
		return fragmentCounts;
	}

	
	
}
