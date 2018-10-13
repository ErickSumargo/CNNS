package com.app.cnns;

import android.content.Context;
import android.content.res.Configuration;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.app.cnns.helper.Utils;
import com.app.cnns.server.interfaces.Response;
import com.app.cnns.server.presenter.BasePresenter;
import com.app.cnns.server.response.BaseResponse;

import java.util.Locale;

import com.app.cnns.helper.Utils;
import com.app.cnns.server.interfaces.Response;
import com.app.cnns.server.presenter.BasePresenter;
import com.app.cnns.server.response.BaseResponse;

/**
 * Created by Erick Sumargo on 2/18/2018.
 */

public class CNNS extends MultiDexApplication implements Response {
    private BasePresenter basePresenter;

    @Override
    public void onCreate() {
        super.onCreate();

        setDefLanguage();

        basePresenter = new BasePresenter(getApplicationContext(), this);
        basePresenter.estConn();
    }

    private void setDefLanguage() {
        Locale locale = new Locale("id");
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.locale = locale;

        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
    }

    @Override
    public void onSuccess(BaseResponse base) {
        Utils.with(this).playDefSound();
    }

    @Override
    public void onFailure(String message) {
        basePresenter.estConn();
    }
}