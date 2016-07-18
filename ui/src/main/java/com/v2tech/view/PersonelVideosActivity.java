/**
 * 
 */
package com.v2tech.view;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.v2tech.presenter.BasePresenter;
import com.v2tech.presenter.PersonelVideosPresenter;
import com.v2tech.presenter.PersonelVideosPresenter.PersonelVideosPresenterUI;
import com.v2tech.R;

/**
 * @author jiangzhen
 * 
 */
public class PersonelVideosActivity extends BaseActivity implements
		OnClickListener, PersonelVideosPresenterUI, View.OnLongClickListener {

	private TextView mTitleBar;
	private View returnButton;
	private ListView listView;
	private LayoutInflater infalter;
	private LocalAdapter adapter;
	private View selectModeLy;
	
	private PersonelVideosPresenter presenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.personel_video_activity);
		

		returnButton = findViewById(R.id.title_bar_left_btn);
		mTitleBar = (TextView) findViewById(R.id.title_bar_center_tv);
		listView = (ListView) findViewById(R.id.personel_vidoes_list_view);
		selectModeLy  = findViewById(R.id.video_select_mode_ly);
		
		adapter = new LocalAdapter();
		listView.setAdapter(adapter);
		returnButton.setOnClickListener(this);
		infalter = LayoutInflater.from(this);


	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.title_bar_left_btn:
			presenter.onReturnBtnClicked();
			break;
		default:
			ImageBind ib = (ImageBind) v.getTag();
			presenter.onVideoItemClicked(ib.tag, v);
			break;
		}

	}

	@Override
	public boolean onLongClick(View view) {
		ImageBind ib = (ImageBind) view.getTag();
		presenter.onVideoItemLongClicked(ib.tag, view);
		return true;
	}

	// /////////////////////////////////////presenter//////////////

	public void doFinish() {
		finish();
	}
	
	@Override
	public BasePresenter getPresenter() {
		if (presenter == null) {
			presenter = new PersonelVideosPresenter(this, this);
		}
		return presenter;
	}

	public void updateTitle() {
		mTitleBar.setText(R.string.personel_video_title_bar);
	}

	public void updateItemLeftShot(Object obj, Bitmap screenShot, String time,
			Object tag) {
		LocalBind lb = (LocalBind) obj;
		lb.left.setImageBitmap(screenShot);
		lb.leftTime.setText(time);
		if (lb.leftly.getTag() != null) {
			((ImageBind)(lb.leftly.getTag())).tag = tag;
		} else {
			
			lb.leftly.setTag(new ImageBind(tag));
		}
	}

	public void updateItemCenterShot(Object obj, Bitmap screenShot,
			String time, Object tag) {
		LocalBind lb = (LocalBind) obj;
		lb.center.setImageBitmap(screenShot);
		lb.centerTime.setText(time);
		if (lb.centerly.getTag() != null) {
			((ImageBind)(lb.centerly.getTag())).tag = tag;
		} else {
			
			lb.centerly.setTag(new ImageBind(tag));
		}
	}

	public void updateItemRightShot(Object obj, Bitmap screenShot, String time,
			Object tag) {
		LocalBind lb = (LocalBind) obj;
		lb.right.setImageBitmap(screenShot);
		lb.rightTime.setText(time);
		if (lb.rightly.getTag() != null) {
			((ImageBind)(lb.rightly.getTag())).tag = tag;
		} else {
			
			lb.rightly.setTag(new ImageBind(tag));
		}
	}

	public void refreshListView() {
		this.adapter.notifyDataSetChanged();
	}
	
	public void updateItemSelected(boolean select, Object lb, int type) {
		if (lb instanceof LocalBind) {
			if (type == PersonelVideosPresenter.ITEM_POS_LEFT) {
				updateItemSelected(select, ((LocalBind) lb).leftly);
			} else if (type == PersonelVideosPresenter.ITEM_POS_CENTER) {
				updateItemSelected(select, ((LocalBind) lb).centerly);
			} else if (type == PersonelVideosPresenter.ITEM_POS_RIGHT) {
				updateItemSelected(select, ((LocalBind) lb).rightly);
			}
		}
	}

	public void updateItemSelected(boolean select, View view) {
		ImageBind ib = (ImageBind) view.getTag();
		RelativeLayout pr = (RelativeLayout) view;
		if (select) {
			if (ib.conver != null) {
				if (ib.conver.isAttachedToWindow()) {
					return;
				} else {
					pr.addView(ib.conver);
				}
			} else {
				ImageView iv = new ImageView(this);
				iv.setImageResource(R.drawable.video_screen_conver);
				iv.setAlpha(0.4F);
				ib.conver = iv;
				RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.WRAP_CONTENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT);
				rl.addRule(RelativeLayout.CENTER_IN_PARENT);
				pr.addView(iv, rl);
			}
			
			
			ImageView iv1 = new ImageView(this);
			iv1.setImageResource(R.drawable.video_selected_icon);
			ib.icon = iv1;
			RelativeLayout.LayoutParams rl1 = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			rl1.addRule(RelativeLayout.CENTER_IN_PARENT);
			pr.addView(iv1, rl1);

		} else {
			if (ib.conver != null) {
				pr.removeView(ib.conver);
			}
			if (ib.icon != null) {
				pr.removeView(ib.icon);
			}
		}
	}

	
	public void updateSelectMode(boolean flag) {
		if (flag) {
			selectModeLy.setVisibility(View.VISIBLE);
		} else {
			selectModeLy.setVisibility(View.GONE);
		}
	}
	
	
	// /////////////////////////////////////presenter//////////////

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
	
	

	@Override
	public void onBackPressed() {
		presenter.onReturnBtnClicked();
	}



	class LocalAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return presenter.getCount();
		}

		@Override
		public Object getItem(int position) {
			return presenter.getItem(position);
		}

		@Override
		public long getItemId(int position) {
			return presenter.getItemId(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LocalBind lb = null;
			if (convertView == null) {
				View view = infalter.inflate(R.layout.video_list_view_item,
						(ViewGroup)null);
				lb = new LocalBind();
				view.setTag(lb);
				lb.left = (ImageView) view
						.findViewById(R.id.video_list_item_left_video_screen);
				lb.center = (ImageView) view
						.findViewById(R.id.video_list_item_middle_video_screen);
				lb.right = (ImageView) view
						.findViewById(R.id.video_list_item_right_video_screen);
				lb.leftTime = (TextView) view
						.findViewById(R.id.video_list_item_left_video_time_line);
				lb.centerTime = (TextView) view
						.findViewById(R.id.video_list_item_middle_video_time_line);
				lb.rightTime = (TextView) view
						.findViewById(R.id.video_list_item_right_video_time_line);

				lb.leftly = view.findViewById(R.id.video_list_item_left_ly);

				lb.leftly.setOnLongClickListener(PersonelVideosActivity.this);
				lb.leftly.setOnClickListener(PersonelVideosActivity.this);

				lb.centerly = view.findViewById(R.id.video_list_item_center_ly);
				lb.centerly.setOnLongClickListener(PersonelVideosActivity.this);
				lb.centerly.setOnClickListener(PersonelVideosActivity.this);

				lb.rightly = view.findViewById(R.id.video_list_item_right_ly);
				lb.rightly.setOnLongClickListener(PersonelVideosActivity.this);
				lb.rightly.setOnClickListener(PersonelVideosActivity.this);

				convertView = view;
			} else {
				lb = (LocalBind) convertView.getTag();
			}
			presenter.doUpdateView(lb, position);
			return convertView;
		}

	}

	class ImageBind {
		Object tag;
		ImageView conver;
		ImageView icon;
		
		public ImageBind() {
			super();
		}

		public ImageBind(Object tag) {
			super();
			this.tag = tag;
		}

	}

	class LocalBind {
		long id;
		
		View leftly;
		View centerly;
		View rightly;
		
		ImageView left;
		ImageView center;
		ImageView right;

		TextView leftTime;
		TextView centerTime;
		TextView rightTime;
	}

}
