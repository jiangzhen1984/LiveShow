package com.v2tech.frag;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.V2.jni.util.V2Log;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.v2tech.R;
import com.v2tech.service.PersonalSetting;


public class PersonalQRCodeSettingFragment extends Fragment implements View.OnClickListener {

    PersonalSetting settingService;

    private ImageView qrImag;
    private View btnLayout;
    private View cancelBtn;
    private View styleChangeBtn;
    private View picSaveBtn;
    private View qrPICScanBtn;

    private QRCodeSettingFragListener fragListener;

    public PersonalQRCodeSettingFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.personal_qr_code_setting_frag_layout, null);
        qrImag = (ImageView)root.findViewById(R.id.personal_qr_code_img);
        btnLayout = root.findViewById(R.id.personal_qr_code_bottom_layout);
        btnLayout.setVisibility(View.GONE);

        styleChangeBtn = root.findViewById(R.id.personal_qr_code_btn_style_change);
        picSaveBtn = root.findViewById(R.id.personal_qr_code_btn_pic_save);
        qrPICScanBtn = root.findViewById(R.id.personal_qr_code_btn_qr_code_scan);
        cancelBtn = root.findViewById(R.id.personal_qr_code_btn_cancel);
        styleChangeBtn.setOnClickListener(this);
        picSaveBtn.setOnClickListener(this);
        qrPICScanBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                generateQR();
            }
        }, 1000);
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


    @Override
    public void onClick(View v) {
        if (fragListener == null) {
            V2Log.w("=== QRCodeSettingFragListener is null");
            return;
        }
        int id = v.getId();
        switch (id) {
            case R.id.personal_qr_code_btn_style_change:
                fragListener.onQRStyleChangeBtnClicked(v);
                break;
            case R.id.personal_qr_code_btn_pic_save:
                fragListener.onQRPICSaveBtnClicked(v);
                break;
            case R.id.personal_qr_code_btn_qr_code_scan:
                fragListener.onQRCodeScanBtnClicked(v);
                break;
            case R.id.personal_qr_code_btn_cancel:
                fragListener.onQRCodeCancelBtnClicked(v);
                break;

        }
    }

    private void generateQR(){
        String qrInputText = "test";
        int width = ((ViewGroup)qrImag.getParent()).getWidth();
        int height = ((ViewGroup)qrImag.getParent()).getHeight();
        int smallerDimension = width < height ? width : height;


        try {
            qrImag.setImageBitmap(encodeAsBitmap("sdfsdfs", width));
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }




    Bitmap encodeAsBitmap(String str, int width) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, width, width, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? Color.BLACK : Color.WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, w, h);
        return bitmap;
    }



    public void showBtn(boolean flag) {
        if (flag) {
            Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_to_up_in);
            btnLayout.startAnimation(hyperspaceJumpAnimation);
        } else {
            Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.up_to_bottom_out);
            btnLayout.startAnimation(hyperspaceJumpAnimation);
        }
        btnLayout.setVisibility(flag ? View.VISIBLE : View.GONE);
    }


    public void setFragListener(QRCodeSettingFragListener fragListener) {
        this.fragListener = fragListener;
    }

    public interface QRCodeSettingFragListener {
        public void onQRStyleChangeBtnClicked(View v);
        public void onQRPICSaveBtnClicked(View v);
        public void onQRCodeScanBtnClicked(View v);
        public void onQRCodeCancelBtnClicked(View v);

    }
}
