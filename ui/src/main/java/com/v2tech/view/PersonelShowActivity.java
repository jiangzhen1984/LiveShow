/**
 *
 */
package com.v2tech.view;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.v2tech.presenter.BasePresenter;
import com.v2tech.presenter.PersonelShowPresenter;
import com.v2tech.presenter.PersonelShowPresenter.PersonelShowPresenterUI;
import com.v2tech.R;

/**
 * @author jiangzhen
 */
public class PersonelShowActivity extends BaseActivity implements OnClickListener,
        PersonelShowPresenterUI {

    private TextView titleBarName;

    private PersonelShowPresenter presenter;

    private View avatarUpdateBtn;
    private View nickNameUpdateBtn;
    private View genderUpdateBtn;
    private View signatureUpdateBtn;
    private View locationUpdateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.personal_show_activity);

        findViewById(R.id.title_bar_left_btn).setOnClickListener(this);
        titleBarName = (TextView) findViewById(R.id.title_bar_center_tv);

        avatarUpdateBtn = findViewById(R.id.avatar_layout);
        nickNameUpdateBtn = findViewById(R.id.personal_nick_name_btn);
        genderUpdateBtn = findViewById(R.id.personal_gender_update_btn);
        signatureUpdateBtn = findViewById(R.id.personal_signature_update_btn);
        locationUpdateBtn = findViewById(R.id.personal_location_update_btn);


        avatarUpdateBtn.setOnClickListener(this);
        nickNameUpdateBtn.setOnClickListener(this);
        genderUpdateBtn.setOnClickListener(this);
        signatureUpdateBtn.setOnClickListener(this);
        locationUpdateBtn.setOnClickListener(this);

        this.overridePendingTransition(R.anim.left_to_right_in,
                R.anim.left_to_right_out);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.title_bar_left_btn:
                presenter.returnBtnClicked();
                break;
            case R.id.personal_nick_name_btn:
                presenter.nickNameUpdateBtnClicked();
                break;
            case R.id.avatar_layout:
                presenter.avatarUpdateBtnClicked();
                break;
            case R.id.personal_gender_update_btn:
                presenter.genderUpdateBtnClicked();
                break;
            case R.id.personal_signature_update_btn:
                presenter.signatureUpdateBtnClicked();
                break;
            case R.id.personal_location_update_btn:
                presenter.locationUpdateBtnClicked();
                break;
            default:
        }

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.left_to_right_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void finishMainUI() {
        finish();

    }


    @Override
    public BasePresenter getPresenter() {
        if (presenter == null) {
            presenter = new PersonelShowPresenter(this, this);
        }
        return presenter;
    }

    @Override
    public void updateTitleBar() {
        titleBarName.setText(R.string.personal_show_title_text);
    }

}
