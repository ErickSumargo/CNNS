package com.app.cnns.server.presenter;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.app.cnns.helper.API;
import com.app.cnns.helper.Constant;
import com.app.cnns.helper.Utils;
import com.app.cnns.server.interfaces.Response;
import com.app.cnns.server.response.RegistrationResponse;
import com.app.cnns.server.response.UserResponse;
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

    public void registerPhone(String phone) {
        Map<String, Object> data = new HashMap<>();
        data.put(Constant.REQ_PHONE, phone);

        API.with(context, true).getRest().registerPhone(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<retrofit2.Response<RegistrationResponse>>() {
                               @Override
                               public void onSubscribe(Disposable d) {
                                   disposables.add(d);
                               }

                               @Override
                               public void onSuccess(retrofit2.Response<RegistrationResponse> value) {
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

    public void resendCode(String phone) {
        Map<String, Object> data = new HashMap<>();
        data.put(Constant.REQ_PHONE, phone);

        API.with(context, true).getRest().resendCode(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<retrofit2.Response<RegistrationResponse>>() {
                               @Override
                               public void onSubscribe(Disposable d) {
                                   disposables.add(d);
                               }

                               @Override
                               public void onSuccess(retrofit2.Response<RegistrationResponse> value) {
                                   if (value.code() == 200) {
                                       value.body().setTag(Constant.REQ_RESEND_CODE);
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

    public void verifyCode(String phone, String code) {
        Map<String, Object> data = new HashMap<>();
        data.put(Constant.REQ_PHONE, phone);
        data.put(Constant.REQ_CODE, code);

        API.with(context, true).getRest().verifyCode(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<retrofit2.Response<RegistrationResponse>>() {
                               @Override
                               public void onSubscribe(Disposable d) {
                                   disposables.add(d);
                               }

                               @Override
                               public void onSuccess(retrofit2.Response<RegistrationResponse> value) {
                                   if (value.code() == 200) {
                                       value.body().setTag(Constant.REQ_CODE);
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

    public void registerUser(String phone, String name, String email, String password, String birthday, int gender, String city) {
        Map<String, Object> data = new HashMap<>();
        data.put(Constant.REQ_PHONE, phone);
        data.put(Constant.REQ_NAME, name);
        data.put(Constant.REQ_EMAIL, email);
        data.put(Constant.REQ_PASSWORD, password);
        data.put(Constant.REQ_BIRTHDAY, birthday);
        data.put(Constant.REQ_GENDER, gender);
        data.put(Constant.REQ_CITY, city);

        API.with(context, true).getRest().registerUser(data)
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

    public void updateProfile(Uri uri, int gender, String phone, String address) {
        Map<String, RequestBody> data = new HashMap<>();
        data.put(Constant.REQ_GENDER, Utils.with(context).convertToRequestBody(String.valueOf(gender)));
        data.put(Constant.REQ_PHONE, Utils.with(context).convertToRequestBody(phone));
        data.put(Constant.REQ_ADDRESS, Utils.with(context).convertToRequestBody(address));

        if (uri != null) {
            File file = new File(uri.getPath());
            if (file.exists()) {
                data.put(Constant.REQ_IMAGE + "." + Utils.with(context).getFileExtension(Uri.fromFile(file)) + "\"", Utils.with(context).convertToRequestBody(file));
                data.put(Constant.REQ_PHOTO_CHANGED, Utils.with(context).convertToRequestBody("true"));
            }
        }

        API.with(context, true).getRest().updateProfile(data)
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

    public void updateProfile() {
        Map<String, RequestBody> data = new HashMap<>();
        data.put(Constant.REQ_FREQUENTLY, Utils.with(context).convertToRequestBody("true"));

        API.with(context, true).getRest().updateProfile(data)
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