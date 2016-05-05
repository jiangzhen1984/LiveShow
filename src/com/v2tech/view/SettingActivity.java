/**
 * 
 */
package com.v2tech.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.v2tech.presenter.SettingPresenter;
import com.v2tech.presenter.SettingPresenter.SettingPresenterUI;
import com.v2tech.v2liveshow.R;

/**
 * @author jiangzhen
 * 
 */
public class SettingActivity extends Activity implements OnClickListener, SettingPresenterUI {



	private TextView mTitleBar;
	private View returnButton;
	private View signoutBtn;
	
	private SettingPresenter presenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.personel_setting_activity);
		presenter = new SettingPresenter(this);
		
		
		returnButton = findViewById(R.id.title_bar_left_btn);
		mTitleBar = (TextView)findViewById(R.id.title_bar_center_tv);
		signoutBtn = findViewById(R.id.personel_setting_quit_btn);
		
		returnButton.setOnClickListener(this);
		signoutBtn.setOnClickListener(this);
		
		presenter.onUICreated();

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.title_bar_left_btn:
			presenter.returnButtonClicked();
			break;
		case R.id.personel_setting_quit_btn:
			presenter.signOutBtnClicked();
			break;
		}

	}


	
	
	
	
	
  ///////////////////////////////////////presenter//////////////
	



	public void doFinish() {
		finish();
	}
	
	public void updateTitle() {
		mTitleBar.setText(R.string.personel_setting_title_bar);
	}


///////////////////////////////////////presenter//////////////
	

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(0, R.animator.left_to_right_out);
	}

	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		presenter.onUIDestroyed();
	}


	

}