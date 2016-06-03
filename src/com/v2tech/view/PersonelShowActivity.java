/**
 * 
 */
package com.v2tech.view;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.v2tech.presenter.PersonelShowPresenter;
import com.v2tech.presenter.PersonelShowPresenter.PersonelShowPresenterUI;
import com.v2tech.v2liveshow.R;

/**
 * @author jiangzhen
 * 
 */
public class PersonelShowActivity extends BaseActivity implements OnClickListener,
		PersonelShowPresenterUI {

	private TextView titleBarName;

	private PersonelShowPresenter presenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.personal_show_activity);
		presenter = new PersonelShowPresenter(this, this);

		findViewById(R.id.title_bar_left_btn).setOnClickListener(this);
		titleBarName = (TextView) findViewById(R.id.title_bar_center_tv);

		this.overridePendingTransition(R.animator.left_to_right_in,
				R.animator.left_to_right_out);

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.title_bar_left_btn:
			presenter.returnBtnClicked();
			break;
		default:
		}

	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(0, R.animator.left_to_right_out);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void finishMainUI() {
		finish();

	}

	@Override
	public void updateTitleBar() {
		titleBarName.setText(R.string.personal_show_title_text);
	}

}
