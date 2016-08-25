package com.v2tech.presenter;

import android.content.Context;

import com.v2tech.service.PersonalSetting;

/**
 * Created by 28851274 on 8/25/16.
 */
public class PersonalSettingPresenter extends BasePresenter implements PersonalSetting {

    public static final int UI_TYPE_NICK_NAME_SETTING = 1;
    public static final int UI_TYPE_LOCATION_SETTING = 2;
    public static final int UI_TYPE_GENDER_SETTING = 3;
    public static final int UI_TYPE_AVATAR_SETTING = 4;
    public static final int UI_TYPE_SIGNATURE_SETTING = 5;


    private Context context;
    private PersonalSettingPresenterUI ui;


    public interface PersonalSettingPresenterUI {
        public void updateUIType(int type);
    }





    public PersonalSettingPresenter(Context context, PersonalSettingPresenterUI ui) {
        this.context = context;
        this.ui = ui;
    }



    public void initUIFragment(int type) {

    }


    @Override
    public void updateNickname(String name) {

    }

    @Override
    public void updateGender(PersonalGender gender) {

    }

    @Override
    public void updateLocation(String country, String prov, String city, String district) {

    }

    @Override
    public void updateNotes(String word) {

    }
}
