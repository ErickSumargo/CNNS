package com.app.cnns_admin.server.presenter;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.app.cnns_admin.helper.API;
import com.app.cnns_admin.helper.Constant;
import com.app.cnns_admin.server.interfaces.Response;
import com.app.cnns_admin.server.response.UserResponse;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

public class UserPresenter {
    private Context context;
    private Response response;

    public CompositeDisposable disposables;

    public UserPresenter(Context context, Response response) {
        this.context = context;
        this.response = response;
        disposables = new CompositeDisposable();
    }

    public void login(String email, String password) {
        Map<String, Object> data = new HashMap<>();
        data.put(Constant.REQ_EMAIL, email);
        data.put(Constant.REQ_PASSWORD, password);

        API.with(context, true).getRest().login(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<retrofit2.Response<UserResponse>>() {
                               @Override
                               public void onSubscribe(Disposable d) {
                                   disposables.add(d);
                               }

                               @Override
                               public void onSuccess(retrofit2.Response<UserResponse> value) {
                                   if (value.code() == 200) {
                                       response.onSuccess(value.body());
                                   } else {
                                       response.onFailure(value.message());
                                   }
                               }

                               @Override
                               public void onError(Throwable e) {
                                   response.onFailure(e.getMessage());
                               }
                           }
                );
    }
}