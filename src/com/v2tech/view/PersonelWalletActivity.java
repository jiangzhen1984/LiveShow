/**
 * 
 */
package com.v2tech.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.v2tech.presenter.PersonelWalletPresenter;
import com.v2tech.presenter.PersonelWalletPresenter.PersonelWalletPresenterUI;
import com.v2tech.v2liveshow.R;

/**
 * @author jiangzhen
 * 
 */
public class PersonelWalletActivity extends BaseActivity implements OnClickListener, PersonelWalletPresenterUI {



	private TextView mTitleBar;
	private View returnButton;
	private ListView listView;
	private LocalAdapter adapter;
	private LayoutInflater infalter;
	
	private PersonelWalletPresenter presenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.personel_wallet_activity);
		presenter = new PersonelWalletPresenter(this);
		
		
		returnButton = findViewById(R.id.title_bar_left_btn);
		mTitleBar = (TextView)findViewById(R.id.title_bar_center_tv);
		listView = (ListView)findViewById(R.id.personel_wallet_balance_detail_list);
		adapter = new LocalAdapter();
		listView.setAdapter(adapter);
		returnButton.setOnClickListener(this);
		
		infalter = LayoutInflater.from(this);
		
		presenter.onUICreated();

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.title_bar_left_btn:
			presenter.returnButtonClicked();
			break;
		}

	}


	
	
	
	
	
  ///////////////////////////////////////presenter//////////////
	



	public void doFinish() {
		finish();
	}
	
	public void updateTitle() {
		mTitleBar.setText(R.string.personel_wallet_title_bar);
	}
	
	
	public void updateItemName(Object obj, String name) {
		((LocalBind)obj).name.setText(name);
	}
	
	public void updateItemTime(Object obj, String time) {
		((LocalBind)obj).time.setText(time);
	}
	
	public void updateItemSum(Object obj, String sum) {
		((LocalBind)obj).sum.setText(sum);
	}

	public void updateItemId(Object obj, long id) {
		((LocalBind)obj).id = id;
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
			LocalBind lb  = null;
			if (convertView == null) {
				View view = infalter.inflate(R.layout.personel_wallet_detail_item, (ViewGroup)null);
				lb = new LocalBind();
				view.setTag(lb);
				lb.name = (TextView)view.findViewById(R.id.personel_wallet_name);
				lb.time = (TextView)view.findViewById(R.id.personel_wallet_time);
				lb.sum = (TextView)view.findViewById(R.id.personel_wallet_sum_text);
				convertView = view;
			} else {
				lb = (LocalBind)convertView.getTag();
			}
			presenter.doUpdateView(lb, position);
			
			return convertView;
		}
		
	}
	
	
	
	
	
	class LocalBind  {
		long id;
		TextView name;
		TextView time;
		TextView sum;
	}
	

}
