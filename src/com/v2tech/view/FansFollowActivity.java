/**
 * 
 */
package com.v2tech.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.v2tech.presenter.FansFollowPresenter;
import com.v2tech.presenter.FansFollowPresenter.FansFollowPresenterUI;
import com.v2tech.v2liveshow.R;
import com.v2tech.widget.LiverInteractionLayout;

/**
 * @author jiangzhen
 * 
 */
public class FansFollowActivity extends Activity implements OnClickListener,
		FansFollowPresenterUI {

	private TextView titleBarName;

	private FansFollowPresenter presenter;
	private LiverInteractionLayout personelLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.fans_follow_activity);
		presenter = new FansFollowPresenter(this, this);

		findViewById(R.id.title_bar_left_btn).setOnClickListener(this);
		titleBarName = (TextView) findViewById(R.id.title_bar_center_tv);
		personelLayout = (LiverInteractionLayout)  findViewById(R.id.personel_liver_interaction_layout);

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

	
	public void showBox() {
		personelLayout.showInnerBox(true);
	}
}
