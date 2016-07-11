/**
 * 
 */
package com.v2tech.view;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.v2tech.presenter.BasePresenter;
import com.v2tech.presenter.FansFollowPresenter;
import com.v2tech.presenter.FansFollowPresenter.FansFollowPresenterUI;
import com.v2tech.v2liveshow.R;
import com.v2tech.widget.LiverInteractionLayout;
import com.v2tech.widget.VoiceRecordDialogWidget;

/**
 * @author jiangzhen
 * 
 */
public class FansFollowActivity extends BaseActivity implements OnClickListener,
		FansFollowPresenterUI {

	private TextView titleBarName;

	private FansFollowPresenter presenter;
	private LiverInteractionLayout personelLayout;
	
	private View audioRecordBtn;
	private PopupWindow   dialog;
	private VoiceRecordDialogWidget voiceDialogWidget;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.fans_follow_activity);
		
		
		findViewById(R.id.title_bar_left_btn).setOnClickListener(this);
		titleBarName = (TextView) findViewById(R.id.title_bar_center_tv);
		personelLayout = (LiverInteractionLayout)  findViewById(R.id.personel_liver_interaction_layout);

		audioRecordBtn = personelLayout.getAudioRecordBtn();
		//cancel click event
		audioRecordBtn.setOnClickListener(null);
		audioRecordBtn.setOnTouchListener(touchListener);
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
	public BasePresenter getPresenter() {
		if (presenter == null) {
			presenter = new FansFollowPresenter(this, this);
			personelLayout.setOutListener(presenter);
		}
		return presenter;
	}
	
	public void updatePersonelViewData(Bitmap avatar, String name,
			String sign, String location, int vipLevel, int video,
			int fans, int followers, int type) {
		if (avatar != null) {
			personelLayout.updateAvatarImg(avatar);
		}
		
		if (name != null) {
			personelLayout.updateNameText(name);
		}
		
		personelLayout.updateSignature(sign);
		personelLayout.updateLocationText(location);
		personelLayout.updateVidoesText(video+"");
		personelLayout.updateFansText(fans+"");
		personelLayout.updateFollowsText(followers+"");
		if (type == FansFollowPresenter.TYPE_FANS) {
			personelLayout.updateFollowBtnTextResource(R.drawable.liver_interaction_follow);
			personelLayout.updateFollowBtnTextResource(R.string.personel_item_user_f_text);
		} else if (type == FansFollowPresenter.TYPE_FOLLOWERS) {
			personelLayout.updateFollowBtnTextResource(R.drawable.liver_interaction_cancel_follow);
			personelLayout.updateFollowBtnTextResource(R.string.personel_item_user_cf_text);
		} else if (type == FansFollowPresenter.TYPE_FRIENDS) {
			personelLayout.updateFollowBtnTextResource(R.drawable.liver_interaction_cancel_follow_friend);
			personelLayout.updateFollowBtnTextResource(R.string.personel_item_user_cf_text);
		}
	}
	
	
	public void updateBtnText(boolean addFlag) {
		personelLayout.updateFollowBtnTextResource(addFlag?R.string.personel_item_user_f_text : R.string.personel_item_user_cf_text);
	}
	
	public Object getIntentData(String key) {
		if (this.getIntent().getExtras() == null) {
			return null;
		}
		return this.getIntent().getExtras().get(key);
	}

	@Override
	public void updateTitleBar() {
		titleBarName.setText(R.string.personal_show_title_text);
	}

	
	public void showBox() {
		personelLayout.showInnerBox(true);
	}
	
	
	public void showDialog(boolean flag, int type) {
		if (dialog == null) {
			dialog = new PopupWindow(this);
			voiceDialogWidget = (VoiceRecordDialogWidget) LayoutInflater.from(
					this).inflate(R.layout.voice_record_dialog_widget_layout,
					(ViewGroup)null);
			dialog.setContentView(voiceDialogWidget);
			dialog.setOutsideTouchable(false);
			voiceDialogWidget.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
			dialog.setWidth(voiceDialogWidget.getMeasuredWidth());
			dialog.setHeight(voiceDialogWidget.getMeasuredHeight());
		}
		
		if (flag) {
			switch (type) {
			case FansFollowPresenter.DIALOG_TYPE_VOLUMN:
				voiceDialogWidget.showVolumnView();
				break;
			case FansFollowPresenter.DIALOG_TYPE_LONG_DURATION:
				voiceDialogWidget.showLongDurationView();
				break;
			case FansFollowPresenter.DIALOG_TYPE_SHORT_DURATION:
				voiceDialogWidget.showRequireMoreDuration();
				break;
			case FansFollowPresenter.DIALOG_TYPE_TOUCH_UP_CANCEL:
				voiceDialogWidget.showTouchUpCancelView();
				break;
			}
			if (!dialog.isShowing()) {
				dialog.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, -220);
			}
		} else {
			dialog.dismiss();
		}
	}
	
	
	public void updateVoiceDBLevel(int level) {
		voiceDialogWidget.updateVolumnLevel(level);
	}
	
	
	
	private boolean inBoundsFlag = true;
	private OnTouchListener touchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int action = event.getAction();
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				audioRecordBtn.setPressed(true);
				presenter.onRecordBtnTouchDown(event);
				inBoundsFlag = true;
				break;
			case MotionEvent.ACTION_MOVE:
				Rect r = new Rect();
				v.getDrawingRect(r);
				if (r.contains((int)event.getX(), (int)event.getY())) {
					if (!inBoundsFlag) {
						inBoundsFlag = true;
						presenter.onRecordBtnTouchMoveInBtn(event);
					}
				} else {
					if (inBoundsFlag) {
						presenter.onRecordBtnTouchMoveOutOfBtn(event);
						inBoundsFlag = false;
					}
				}
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				audioRecordBtn.setPressed(false);
				presenter.onRecordBtnTouchUp(event);
				break;
			}
			return true;
		}
		
	};
}
