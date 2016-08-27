package com.v2tech.frag;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.V2.jni.util.V2Log;
import com.v2tech.R;
import com.v2tech.service.PersonalSetting;


public class PersonalAvatarSettingFragment extends Fragment implements View.OnClickListener {

    PersonalSetting settingService;

    private AvatarSettingFragListener fragListener;
    private View btnLayout;
    private View cancelBtn;
    private View photoTokenBtn;
    private View picChooseBtn;
    private View picSaveBtn;
    private ImageView avatar;


    public PersonalAvatarSettingFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.personal_avatar_setting_frag_layout, null);


        btnLayout = root.findViewById(R.id.personal_avatar_bottom_layout);
        btnLayout.setVisibility(View.GONE);

        photoTokenBtn = root.findViewById(R.id.personal_avatar_btn_photo_token);
        picSaveBtn = root.findViewById(R.id.personal_avatar_btn_pic_save);
        picChooseBtn = root.findViewById(R.id.personal_avatar_btn_avatar_choose_pic);
        cancelBtn = root.findViewById(R.id.personal_avatar_btn_cancel);
        photoTokenBtn.setOnClickListener(this);
        picSaveBtn.setOnClickListener(this);
        picChooseBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        avatar = (ImageView) root.findViewById(R.id.personal_avatar_setting_avatar_img);

        return root;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }




    @Override
    public void onClick(View v) {
        if (fragListener == null) {
            V2Log.w("=== AvatarSettingFragListener is null");
            return;
        }
        int id = v.getId();
        switch (id) {
            case R.id.personal_avatar_btn_photo_token:
                fragListener.onAvatarPhotoTokenBtnClicked(v);
                break;
            case R.id.personal_avatar_btn_pic_save:
                fragListener.onAvatarPICSaveBtnClicked(v);
                break;
            case R.id.personal_avatar_btn_avatar_choose_pic:
                fragListener.onAvatarPICChooseFromAlbumBtnClicked(v);
                break;
            case R.id.personal_avatar_btn_cancel:
                fragListener.onAvatarCancelBtnClicked(v);
                break;

        }
    }



    public void showBottomMenu(boolean flag) {
        if (flag) {
            Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_to_up_in);
            btnLayout.startAnimation(hyperspaceJumpAnimation);
        } else {
            Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.up_to_bottom_out);
            btnLayout.startAnimation(hyperspaceJumpAnimation);
        }
        btnLayout.setVisibility(flag ? View.VISIBLE : View.GONE);
    }



    public void updateAvatar(Uri avatarUri) {
        avatar.setImageURI(avatarUri);
    }


    public PersonalSetting getSettingService() {
        return settingService;
    }

    public void setSettingService(PersonalSetting settingService) {

        this.settingService = settingService;
    }

    public void setFragListener(AvatarSettingFragListener fragListener) {
        this.fragListener = fragListener;
    }

    public interface AvatarSettingFragListener {
        public void onAvatarPhotoTokenBtnClicked(View v);
        public void onAvatarPICSaveBtnClicked(View v);
        public void onAvatarPICChooseFromAlbumBtnClicked(View v);
        public void onAvatarCancelBtnClicked(View v);
    }
}
