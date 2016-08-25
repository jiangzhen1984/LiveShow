package com.v2tech.view;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.v2tech.R;
import com.v2tech.frag.PersonalGenderSettingFragment;
import com.v2tech.frag.PersonalNicknameSettingFragment;
import com.v2tech.frag.PersonalSignatureSettingFragment;
import com.v2tech.presenter.BasePresenter;
import com.v2tech.presenter.PersonalSettingPresenter;
import com.v2tech.presenter.PersonalSettingPresenter.PersonalSettingPresenterUI;

public class PersonalSettingActivity extends BaseFragmentActivity implements PersonalSettingPresenterUI {


    private PersonalSettingPresenter presenter;
    private FragmentManager fm;
    private PersonalNicknameSettingFragment nicknameSettingFragment;
    private PersonalGenderSettingFragment genderSettingFragment;
    private PersonalSignatureSettingFragment signatureSettingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_setting_activity);
        fm  = this.getFragmentManager();
    }


    @Override
    public BasePresenter getPresenter() {
        if (presenter == null) {
            presenter = new PersonalSettingPresenter(this, this);
        }
        return presenter;
    }


    @Override
    public void updateUIType(int type) {
        Fragment frag = null;
        switch (type) {
            case PersonalSettingPresenter.UI_TYPE_NICK_NAME_SETTING:
                if (nicknameSettingFragment == null) {
                    nicknameSettingFragment = new PersonalNicknameSettingFragment();
                    nicknameSettingFragment.setSettingService(presenter);
                }
                frag = nicknameSettingFragment;
                break;

            case PersonalSettingPresenter.UI_TYPE_GENDER_SETTING:
                if (genderSettingFragment == null) {
                    genderSettingFragment = new PersonalGenderSettingFragment();
                    genderSettingFragment.setSettingService(presenter);
                }
                frag = genderSettingFragment;
                break;
            case PersonalSettingPresenter.UI_TYPE_SIGNATURE_SETTING:
                if (signatureSettingFragment == null) {
                    signatureSettingFragment = new PersonalSignatureSettingFragment();
                    signatureSettingFragment.setSettingService(presenter);
                }
                frag = signatureSettingFragment;
                break;
        }

        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.content_fragment, frag);
        ft.commit();
    }



}
