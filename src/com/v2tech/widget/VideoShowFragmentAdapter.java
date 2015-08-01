package com.v2tech.widget;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class VideoShowFragmentAdapter extends FragmentPagerAdapter {

	private int fragmentCounts;
	private VideoShowFragment[] fragments;

	public VideoShowFragmentAdapter(FragmentManager fm, int fragmentCounts) {
		super(fm);
		this.fragmentCounts = fragmentCounts;
		fragments = new VideoShowFragment[fragmentCounts];
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

	@Override
	public int getItemPosition(Object object) {
		for (VideoShowFragment frag : fragments) {
			if (frag == object) {
				return POSITION_UNCHANGED;
			}
		}
		return POSITION_NONE;
	}

	public void removeItem(int position) {
		VideoShowFragment[] newFrgArr = new VideoShowFragment[--fragmentCounts];
		for (int i = 0; i < position; i++) {
			newFrgArr[i] = fragments[i];
			newFrgArr[i].setIndex(i + 1);
		}
		for (int i = position + 1; i < fragments.length; i++) {
			newFrgArr[i - 1] = fragments[i];
			newFrgArr[i - 1].setIndex(i);
		}
		fragments = newFrgArr;

	}
	
	
	
	public Fragment createFragment() {
		fragmentCounts ++;
		VideoShowFragment fragment = new VideoShowFragment();
		VideoShowFragment[] newFrgArr = new VideoShowFragment[fragmentCounts];
		System.arraycopy(fragments, 0, newFrgArr, 0, fragments.length);
		
		newFrgArr[fragments.length] = fragment;
		
		fragments = newFrgArr;
		
		return fragment;
	}

}
