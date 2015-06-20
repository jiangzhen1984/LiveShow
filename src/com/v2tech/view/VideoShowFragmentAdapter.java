package com.v2tech.view;

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
		int realPos = position % fragmentCounts;
		if (fragments[realPos] == null) {
			fragments[realPos] = new VideoShowFragment();
		}
		return fragments[realPos];
	}

	@Override
	public int getCount() {
		return 6;
	}

	
	
}
