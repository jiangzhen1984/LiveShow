package com.v2tech.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;

import com.v2tech.R;
import com.v2tech.widget.FloatEditText;
import com.v2tech.widget.FloatEditText.OnBackkeyClickedListener;

public class BottomButtonLayoutActivity extends Activity {
	
	
	private View mMapButton;
	private View mWordButton;
	private FloatEditText mEditText;

	@Override
	public void setIntent(Intent newIntent) {
		super.setIntent(newIntent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bottom_button_layout_activity);
		//this.overridePendingTransition(R.animator.bottom_to_up_in, 0);
		mEditText = (FloatEditText) findViewById(R.id.edit_text);
		((FloatEditText) mEditText)
		.setOnBackKeyClickedListener(new OnBackkeyClickedListener() {

			@Override
			public void OnBackkeyClicked(View v) {
				quit(0, null);
			}
		});
		mEditText.requestFocus();
		
		mMapButton = findViewById(R.id.map_button);
		mWordButton = findViewById(R.id.msg_button);
		mMapButton.setOnClickListener(buttonListener);
		mWordButton.setOnClickListener(buttonListener);
		
	}
	
	
	
	private OnClickListener buttonListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int action = 0 ;
			if (v == mWordButton) {
				action = 2;
			} else {
				action = 1;
			}
			quit(action, mEditText.getEditableText().toString());
		}
		
	};
	
	
	
	private void quit(int action, String text) {
		Intent i = new Intent();
		i.putExtra("text", text);
		i.putExtra("action", action);
		if (action !=1 && action != 2) {
			setResult(Activity.RESULT_CANCELED, i);
		} else {
			setResult(Activity.RESULT_OK, i);
		}
		InputMethodManager mIMM = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		mIMM.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
		mEditText.clearFocus();
		finish();
	}
	
	
	 

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	

}
