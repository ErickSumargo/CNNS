package com.app.cnns.helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.app.cnns.server.model.User;
import com.app.cnns.service.UpdateProfileService;
import com.app.cnns.view.activity.MainActivity;
import com.google.gson.Gson;

import com.app.cnns.server.model.User;

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

    public void saveBookmark(int id, boolean saved) {
        ArrayList<String> bookmarksS = new ArrayList<>(pref.getStringSet(Constant.PREF_BOOKMARK, new HashSet<String>()));
        ArrayList<Integer> bookmarks = new ArrayList<>();
        for (int i = 0; i < bookmarksS.size(); i++) {
            bookmarks.add(Integer.valueOf(bookmarksS.get(i)));
        }

        if (saved) {
            for (int i = 0; i < bookmarks.size(); i++) {
                if (bookmarks.get(i) == id) {
                    bookmarks.remove(i);
                    break;
                }
            }
        } else {
            bookmarks.add(id);
        }

        Set<String> bs = new HashSet<>();
        for (int i = 0; i < bookmarks.size(); i++) {
            bs.add(String.valueOf(bookmarks.get(i)));
        }
        editor.putStringSet(Constant.PREF_BOOKMARK, bs);
        editor.commit();
    }

    public ArrayList<Integer> getBookmarks() {
        ArrayList<String> bookmarksS = new ArrayList<>(pref.getStringSet(Constant.PREF_BOOKMARK, new HashSet<String>()));
        ArrayList<Integer> bookmarks = new ArrayList<>();
        for (int i = 0; i < bookmarksS.size(); i++) {
            bookmarks.add(Integer.valueOf(bookmarksS.get(i)));
        }
        return bookmarks;
    }

    public void startServices() {
        context.startService(new Intent(context, UpdateProfileService.class));
    }

    private void stopServices() {
        context.stopService(new Intent(context, UpdateProfileService.class));
    }

    public void logout() {
        stopServices();
        editor.clear().commit();

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}