package com.v2tech.presenter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.v2tech.service.UserService;
import com.v2tech.vo.User;
import com.v2tech.vo.WalletDetail;


public class PersonelWalletPresenter extends BasePresenter {
	
	List<WalletDetail> detailList;
	UserService us;
	private static DateFormat df = new SimpleDateFormat("yyyy-mm-DD HH:MM:ss", Locale.getDefault());

	public interface PersonelWalletPresenterUI {

		public void doFinish();

		public void updateTitle();
		
		public void updateItemName(Object obj,String name);
		public void updateItemTime(Object obj,String time);
		public void updateItemSum(Object obj, String sum);
		public void updateItemId(Object obj, long id);
	}

	private PersonelWalletPresenterUI ui;

	public PersonelWalletPresenter(PersonelWalletPresenterUI ui) {
		super();
		this.ui = ui;
		us = new UserService();
		//TODO query from server
		detailList = new ArrayList<WalletDetail>();
		for (int i =0; i < 30; i++) {
			WalletDetail wd = new WalletDetail();
			wd.id = 1;
			wd.money = 12.9F + i;
			wd.d = new Date();
			wd.user = new User(i, "小明"+ i, "第三", "");
			wd.user.setNickName("小明"+ i);
			detailList.add(wd);
		}
		
	}

	@Override
	public void onUICreated() {
		ui.updateTitle();
	}

	@Override
	public void onUIDestroyed() {
		super.onUIDestroyed();
		us.clearCalledBack();
	}

	public void returnButtonClicked() {
		ui.doFinish();
	}
	

	
	public int getCount() {
		return detailList.size();
	}

	public Object getItem(int position) {
		return detailList.get(position);
	}

	public long getItemId(int position) {
		return detailList.get(position).id;
	}

	
	public void doUpdateView(Object obj, int position) {
		WalletDetail wd = detailList.get(position);
		ui.updateItemName(obj, wd.user.getNickName());
		ui.updateItemTime(obj, df.format(wd.d));
		ui.updateItemSum(obj, wd.money > 0 ? "+" +  wd.money : "-" +  wd.money  );
		ui.updateItemId(obj, wd.id);
	}
}
