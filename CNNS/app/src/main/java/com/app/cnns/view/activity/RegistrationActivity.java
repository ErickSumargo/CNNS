package com.app.cnns.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.cnns.R;
import com.app.cnns.view.custom.ViewPagerCustom;
import com.app.cnns.view.fragment.CodeVerFragment;
import com.app.cnns.view.fragment.PhoneRegFragment;
import com.app.cnns.view.fragment.UserRegFragment;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;

public class RegistrationActivity extends AppCompatActivity {
    private CoordinatorLayout parentLayout;
    private RelativeLayout childLayout;
    public StepView stepView;

    private SectionsPagerAdapter sectionsPagerAdapter;
    public ViewPagerCustom viewPager;

    public TextView previous, next;

    private PhoneRegFragment phoneReg;
    private CodeVerFragment codeVer;
    private UserRegFragment userReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initView();
        setEvent();
    }

    private void initView() {
        parentLayout = findViewById(R.id.parent_layout);
        childLayout = findViewById(R.id.child_layout);
        stepView = findViewById(R.id.step_view);

        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        viewPager = findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);

        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);

        next.setText("LANJUT");

        phoneReg = new PhoneRegFragment();
        codeVer = new CodeVerFragment();
        userReg = new UserRegFragment();
    }

    private void setEvent() {
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewPager.getCurrentItem() == 1) {
                    viewPager.setCurrentItem(0);
                    stepView.go(0, true);

                    previous.setVisibility(View.GONE);

                    if (codeVer.isTimerRunning) {
                        codeVer.timer.cancel();
                        codeVer.resetTimer();
                    }
                    codeVer.clearCodeField();
                } else {
                    viewPager.setCurrentItem(0);
                    stepView.go(0, true);

                    previous.setVisibility(View.GONE);
                    next.setText("LANJUT");

                    userReg.clearDataField();
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewPager.getCurrentItem() == 0) {
                    phoneReg.validate();
                } else if (viewPager.getCurrentItem() == 1) {
                    codeVer.validate();
                } else {
                    userReg.validate();
                }
            }
        });
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return phoneReg;
                case 1:
                    return codeVer;
                case 2:
                    return userReg;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (viewPager.getCurrentItem() == 0) {
                onBackPressed();
            } else if (viewPager.getCurrentItem() == 1) {
                viewPager.setCurrentItem(0);
                previous.setVisibility(View.GONE);

                if (codeVer.isTimerRunning) {
                    codeVer.timer.cancel();
                    codeVer.resetTimer();
                }
                codeVer.clearCodeField();
            } else {
                viewPager.setCurrentItem(0);
                previous.setVisibility(View.GONE);
                next.setText("LANJUT");

                userReg.clearDataField();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (codeVer.isTimerRunning) {
            codeVer.timer.cancel();
            codeVer.resetTimer();
        }
        super.onDestroy();
    }
}