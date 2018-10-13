package com.app.cnns_admin.helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.app.cnns_admin.server.model.User;
import com.app.cnns_admin.view.activity.MainActivity;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Erick Sumargo on 2/27/2018.
 */

public class Session {
    private Context context;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private Session(Context context) {
        this.context = context;

        pref = context.getSharedPreferences(Constant.PREF_NAME, 0);
        editor = pref.edit();
    }

    public static Session with(Context context) {
        return new Session(context);
    }

    public void saveLogin() {
        editor.putBoolean(Constant.PREF_IS_LOGIN, true);
        editor.commit();
    }

    public boolean isLogin() {
        return pref.getBoolean(Constant.PREF_IS_LOGIN, false);
    }

    public void saveToken(String token) {
        editor.putString(Constant.PREF_TOKEN, token);
        editor.commit();
    }

    public void saveUser(User user) {
        editor.putString(Constant.PREF_USER, new Gson().toJson(User.parse(user)));
        editor.putString(Constant.PREF_USER_TYPE, user.getType());
        editor.commit();
    }

    public User getUser() {
        return new Gson().fromJson(pref.getString(Constant.PREF_USER, null), User.class);
    }

    public String getUserType() {
        return pref.getString(Constant.PREF_USER_TYPE, "");
    }

    public String getToken() {
        return pref.getString(Constant.PREF_TOKEN, "");
    }

    public void logout() {
        editor.clear().commit();

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}