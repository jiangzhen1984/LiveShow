package com.v2tech.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.V2.jni.util.V2Log;
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
import com.v2tech.util.GlobalConfig;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PersonalSettingActivity extends BaseFragmentActivity implements PersonalSettingPresenterUI, OnClickListener {


    public static final int REQUEST_CAMERA_CODE_FOR_PHOTO_TOKEN = 1;

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

    private Uri tmpPhotoURI;


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
        presenter.initUIFragment(getIntent().getIntExtra("type", 0));
    }

    @Override
    public void onBackPressed() {
         presenter.backKeyPressed();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            presenter.photoToken(tmpPhotoURI);
        } else if (resultCode == RESULT_CANCELED) {

        }
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
    public void openCamera(int type) {
        if (type == PersonalSettingPresenter.OPEN_CAMERA_TYPE_AVATAR_PHOTO_TOKEN) {
            //create new Intent
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            tmpPhotoURI = Uri.fromFile(new File(GlobalConfig.getGlobalPicsPath()+"/" + timeStamp+".jpg"));
            intent.putExtra(MediaStore.EXTRA_OUTPUT, tmpPhotoURI);  // set the image file name
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // set the video image quality to high
            // start the Video Capture Intent
            startActivityForResult(intent, REQUEST_CAMERA_CODE_FOR_PHOTO_TOKEN);
        }
    }

    @Override
    public void showAvatarPIC(Uri photoUri) {
        avatarSettingFragment.updateAvatar(photoUri);
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
