package com.v2tech.widget;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

public class VideoShowFragmentAdapter extends FragmentPagerAdapter {

	private int fragmentCounts;
	private VideoShowFragment[] fragments;
	private FragmentManager fm;

	private FragmentTransaction mCurTransaction = null;
	private Fragment mCurrentPrimaryItem = null;

	public VideoShowFragmentAdapter(FragmentManager fm, int fragmentCounts) {
		super(fm);
		this.fm = fm;
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

	@Override
	public void startUpdate(ViewGroup container) {
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		if (mCurTransaction == null) {
			mCurTransaction = fm.beginTransaction();
		}
		//mCurTransaction.detach((Fragment) object);
		mCurTransaction.remove((Fragment) object);
	}

	public void removeItem(int position) {
		if (fragmentCounts == 0) {
			return;
		}
		VideoShowFragment[] newFrgArr = new VideoShowFragment[--fragmentCounts];
		for (int i = 0; i < position; i++) {
			newFrgArr[i] = fragments[i];
			newFrgArr[i].setIndex(i + 1);
		}
		for (int i = position + 1; i < fragments.length; i++) {
			newFrgArr[i - 1] = fragments[i];
			newFrgArr[i - 1].setIndex(i);
		}
		FragmentTransaction ct = fm.beginTransaction();
		//ct.detach(fragments[position].getTargetFragment());
		ct.remove(fragments[position]);
		ct.commit();
		fragments = newFrgArr;

	}

	public Fragment createFragment() {
		fragmentCounts++;
		VideoShowFragment fragment = new VideoShowFragment();
		VideoShowFragment[] newFrgArr = new VideoShowFragment[fragmentCounts];
		System.arraycopy(fragments, 0, newFrgArr, 0, fragments.length);

		newFrgArr[fragments.length] = fragment;
		fragment.setIndex(fragments.length);
		fragments = newFrgArr;

		return fragment;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		if (mCurTransaction == null) {
			mCurTransaction = fm.beginTransaction();
		}

		final long itemId = getItemId(position);
	
		Fragment fragment = getItem(position);
			mCurTransaction.add(container.getId(), fragment,
					makeFragmentName(container.getId(), itemId));
		if (fragment != mCurrentPrimaryItem) {
			fragment.setMenuVisibility(false);
			fragment.setUserVisibleHint(false);
		}

		return fragment;
	}

	@Override
	public void finishUpdate(ViewGroup container) {
		if (mCurTransaction != null) {
			mCurTransaction.commitAllowingStateLoss();
			mCurTransaction = null;
			fm.executePendingTransactions();
		}
	}

	private static String makeFragmentName(int viewId, long id) {
		return "android:switcher:" + viewId + ":" + id;
	}

}
