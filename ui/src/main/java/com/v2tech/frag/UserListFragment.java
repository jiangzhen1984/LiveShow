package com.v2tech.frag;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.v2tech.R;

public class UserListFragment extends Fragment implements UserListFragmentNotification, OnItemClickListener {
	
	private ListView listview;
	private LocalAdapter adapter;
	private UserListFragmentConnector connector;
	private UserListFragmentClickListener listener;
	
	
	public UserListFragment() {
		super();
		adapter = new LocalAdapter();
	}

	public UserListFragment(UserListFragmentConnector connector) {
		super();
		this.connector = connector;
	}
	

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		adapter = new LocalAdapter();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = LayoutInflater.from(getActivity()).inflate(R.layout.user_list_layout, (ViewGroup)null);
		listview = (ListView)root.findViewById(R.id.listView_list);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(this);
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
	
	
	
	
	
	
	
	public UserListFragmentClickListener getListener() {
		return listener;
	}

	public void setListener(UserListFragmentClickListener listener) {
		this.listener = listener;
	}

	public UserListFragmentConnector getConnector() {
		return connector;
	}

	public void setConnector(UserListFragmentConnector connector) {
		this.connector = connector;
	}

	@Override
	public void notifyDatasetChanged() {
		adapter.notifyDataSetChanged();
	}

	
	


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (listener != null) {
			listener.onItemClick(parent, view, position, id);
		}
	}





	class LocalAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return connector == null ? 0 :connector.getCount();
		}

		@Override
		public Object getItem(int position) {
			return connector.getItem(position);
		}

		@Override
		public long getItemId(int position) {
			return connector.getItemId(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = connector.inflate();
			}
			connector.update(position, convertView);
			return convertView;
		}
		
	}
	
	
	public interface UserListFragmentConnector {
		
		public int getCount();

		public Object getItem(int position);

		public long getItemId(int position);

		public void update(int position, View convertView);
		
		public View inflate();
		
	}
	
	public interface UserListFragmentClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id);
	}

}
