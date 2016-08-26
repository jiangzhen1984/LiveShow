package com.v2tech.frag;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.v2tech.R;
import com.v2tech.service.PersonalSetting;


public class PersonalLocationSettingFragment extends Fragment {

    PersonalSetting settingService;

    ListView countListView;
    String[] countries;

    public PersonalLocationSettingFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        countries = this.getActivity().getResources().getStringArray(R.array.countries_array);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.personal_location_setting_frag_layout, null);
        countListView = (ListView) root.findViewById(R.id.personal_location_setting_all_list);
        countListView.setAdapter(new LocalAdapter());
        return root;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    public PersonalSetting getSettingService() {
        return settingService;
    }

    public void setSettingService(PersonalSetting settingService) {

        this.settingService = settingService;
    }



    class LocalAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return countries.length;
        }

        @Override
        public Object getItem(int position) {
            return countries[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LocalViewBind lvb = null;
            if (convertView == null) {
                View root = LayoutInflater.from(getActivity()).inflate(R.layout.personal_location_list_item_layout, null);
                lvb = new LocalViewBind();
                lvb.locationName = (TextView) root.findViewById(R.id.personal_location_list_item_name);
                root.setTag(lvb);
                convertView = root;
            } else {
                lvb = (LocalViewBind)convertView.getTag();
            }

            lvb.locationName.setText(countries[position]);
            return convertView;
        }
    }



    class LocalViewBind {
        TextView locationName;

    }
}
