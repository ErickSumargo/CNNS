package com.app.cnns.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.app.cnns.helper.Internet;
import com.app.cnns.helper.Session;
import com.app.cnns.server.interfaces.Response;
import com.app.cnns.server.presenter.UserPresenter;
import com.app.cnns.server.response.BaseResponse;
import com.app.cnns.server.response.UserResponse;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Erick Sumargo on 2/24/2018.
 */

public class UpdateProfileService extends Service implements Response {
    private Handler mHandler = new Handler();
    private Timer mTimer = null;

    private final long INTERVAL = 30000;

    private UserPresenter userPresenter;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            mTimer = new Timer();
            userPresenter = new UserPresenter(getApplicationContext(), this);
        }
        mTimer.scheduleAtFixedRate(new CustomTask(), 0, INTERVAL);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public void attemptUpdate() {
        if (Internet.isConnected(this)) {
            userPresenter.updateProfile();
        }
    }

    private class CustomTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    attemptUpdate();
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        mTimer.cancel();

        super.onDestroy();
    }

    @Override
    public void onSuccess(BaseResponse base) {
        UserResponse r = (UserResponse) base;

        Session.with(this).saveUser(r.getData().getUser());
        Session.with(this).saveToken(r.getData().getToken());
    }

    @Override
    public void onFailure(String message) {
    }
}