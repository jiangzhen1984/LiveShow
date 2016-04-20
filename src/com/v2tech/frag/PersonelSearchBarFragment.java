package com.v2tech.frag;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.v2tech.v2liveshow.R;

public class PersonelSearchBarFragment extends Fragment implements TextWatcher {

	private EditText et;
	private PersonelSearchBarTextListener listener;
	
	public PersonelSearchBarFragment() {
		super();
	}
	
	
	public PersonelSearchBarFragment(PersonelSearchBarTextListener listener) {
		super();
		this.listener = listener;
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
		View root = LayoutInflater.from(getActivity()).inflate(R.layout.personel_search_bar, null);
		et = (EditText)root.findViewById(R.id.personel_search_text_et);
		et.addTextChangedListener(this);
		//TODO update image position
		return root;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		et.removeTextChangedListener(this);
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		
	}

	@Override
	public void afterTextChanged(Editable s) {
		listener.onTextChanged(s.toString());
	}
	
	
	
	
	public PersonelSearchBarTextListener getListener() {
		return listener;
	}


	public void setListener(PersonelSearchBarTextListener listener) {
		this.listener = listener;
	}




	public interface PersonelSearchBarTextListener {
		public void onTextChanged(String content);
	}

}
