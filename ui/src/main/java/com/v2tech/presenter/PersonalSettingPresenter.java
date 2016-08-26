package com.v2tech.presenter;

import android.content.Context;
import android.view.View;

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
    public static final int UI_TYPE_QR_CODE_SETTING = 6;


    private Context context;
    private PersonalSettingPresenterUI ui;
    private int type ;

    private boolean menuFlag;

    public interface PersonalSettingPresenterUI {
        public void updateUIType(int type);

        public void showQRCodeMenu(boolean flag);
    }





    public PersonalSettingPresenter(Context context, PersonalSettingPresenterUI ui) {
        this.context = context;
        this.ui = ui;
    }



    public void initUIFragment(int type) {
        this.type = type;
        ui.updateUIType(type);
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


    public void actionBarRightBtnClicked(View v) {
        switch (type) {
            case UI_TYPE_QR_CODE_SETTING:
                ui.showQRCodeMenu(!menuFlag);
                menuFlag = !menuFlag;
                break;
            case UI_TYPE_AVATAR_SETTING:
                menuFlag = !menuFlag;
                break;
        }
    }
}
