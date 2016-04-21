package com.v2tech.frag;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.v2tech.v2liveshow.R;

public class CardScanFragment extends Fragment {

	private View root;
	
	public CardScanFragment() {
		super();
	}
	

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		root = LayoutInflater.from(getActivity()).inflate(R.layout.card_scan_layout, null);
		return root;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}


	public void show(boolean flag) {
		root.setVisibility(flag ? View.VISIBLE : View.GONE);
	}

}
