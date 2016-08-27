package com.v2tech.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.V2.jni.util.V2Log;
import com.baidu.mapapi.map.Text;
import com.v2tech.R;
import com.v2tech.frag.PersonalAvatarSettingFragment;
import com.v2tech.frag.PersonalGenderSettingFragment;
import com.v2tech.frag.PersonalLocationSettingFragment;
import com.v2tech.frag.PersonalNicknameSettingFragment;
import com.v2tech.frag.PersonalQRCodeSettingFragment;
import com.v2tech.frag.PersonalSignatureSettingFragment;
import com.v2tech.presenter.BasePresenter;
import com.v2tech.presenter.PersonalSettingPresenter;
import com.v2tech.presenter.PersonalSettingPresenter.PersonalSettingPresenterUI;

public class PersonalSettingActivity extends BaseFragmentActivity implements PersonalSettingPresenterUI, OnClickListener {


    private PersonalSettingPresenter presenter;
    private FragmentManager fm;

    private ImageView backBtn;
    private TextView saveBtn;
    private TextView titleTxv;
    private PersonalNicknameSettingFragment nicknameSettingFragment;
    private PersonalGenderSettingFragment genderSettingFragment;
    private PersonalSignatureSettingFragment signatureSettingFragment;
    private PersonalAvatarSettingFragment avatarSettingFragment;
    private PersonalLocationSettingFragment locationSettingFragment;
    private PersonalQRCodeSettingFragment qrCodeSettingFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_setting_activity);

        saveBtn = (TextView) findViewById(R.id.title_bar_right_tv);
        saveBtn.setText(R.string.personal_setting_save);
        saveBtn.setVisibility(View.VISIBLE);
        saveBtn.setOnClickListener(this);


        backBtn = (ImageView) findViewById(R.id.title_bar_left_btn);
        backBtn.setOnClickListener(this);

        titleTxv  = (TextView) findViewById(R.id.title_bar_center_tv);
        fm  = this.getSupportFragmentManager();
        presenter.initUIFragment(4);//getIntent().getIntExtra("type", 0));
    }

    @Override
    public void onBackPressed() {
         presenter.backKeyPressed();
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
                titleTxv.setText(R.string.personal_setting_nick_name_update_title);
                break;

            case PersonalSettingPresenter.UI_TYPE_GENDER_SETTING:
                if (genderSettingFragment == null) {
                    genderSettingFragment = new PersonalGenderSettingFragment();
                    genderSettingFragment.setSettingService(presenter);
                }
                titleTxv.setText(R.string.personal_setting_gender_update_title);
                frag = genderSettingFragment;
                break;
            case PersonalSettingPresenter.UI_TYPE_SIGNATURE_SETTING:
                if (signatureSettingFragment == null) {
                    signatureSettingFragment = new PersonalSignatureSettingFragment();
                    signatureSettingFragment.setSettingService(presenter);
                }
                titleTxv.setText(R.string.personal_setting_signature_update_title);
                frag = signatureSettingFragment;
                break;
            case PersonalSettingPresenter.UI_TYPE_LOCATION_SETTING:
                if (locationSettingFragment == null) {
                    locationSettingFragment = new PersonalLocationSettingFragment();
                    locationSettingFragment.setSettingService(presenter);
                }
                frag = locationSettingFragment;
                titleTxv.setText(R.string.personal_setting_location_update_title);
                break;
            case PersonalSettingPresenter.UI_TYPE_AVATAR_SETTING:
                if (avatarSettingFragment == null) {
                    avatarSettingFragment = new PersonalAvatarSettingFragment();
                    avatarSettingFragment.setSettingService(presenter);
                    avatarSettingFragment.setFragListener(presenter);
                }
                frag = avatarSettingFragment;
                titleTxv.setText(R.string.personal_setting_avatar_update_title);
                break;
            case PersonalSettingPresenter.UI_TYPE_QR_CODE_SETTING:
                if (qrCodeSettingFragment == null) {
                    qrCodeSettingFragment = new PersonalQRCodeSettingFragment();
                    qrCodeSettingFragment.setSettingService(presenter);
                    qrCodeSettingFragment.setFragListener(presenter);
                }
                frag = qrCodeSettingFragment;
                titleTxv.setText(R.string.personal_setting_qr_code_update_title);
                break;
        }

        if (frag == null) {
            V2Log.e(" Unknown frag type: " + type);
            finish();
            return;
        }
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.content_fragment, frag);
        ft.commit();
    }

    @Override
    public void showQRCodeMenu(boolean flag) {
        qrCodeSettingFragment.showBtn(flag);
    }

    @Override
    public void showAvatarMenu(boolean flag) {
        avatarSettingFragment.showBottomMenu(flag);
    }

    @Override
    public void quit() {
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v == saveBtn) {
            presenter.actionBarRightBtnClicked(v);
        } else if (v == backBtn) {
            presenter.backKeyPressed();
        }
    }
}
