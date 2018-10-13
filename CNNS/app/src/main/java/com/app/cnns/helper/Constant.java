package com.app.cnns.helper;

import android.os.Environment;

import java.io.File;

/**
 * Created by Erick Sumargo on 2/27/2018.
 */

public class Constant {
    public static final String URL = "http://192.168.100.7:82/";
    public static final String URL_MEDIA_IMAGE = "http://192.168.100.7:82/media/image/";
    public static final String URL_MEDIA_VIDEO = "http://192.168.100.7:82/media/video/";

    public static final String USER = "user";
    public static final String GUEST = "guest";
    public static final String AUTHORIZATION = "Authorization";
    public static final String JWT_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6LTEsIm5hbWUiOiJHdWVzdCJ9.YPBvng-UfdW9xipEBsVJB4ZEvzLAnc2hbh_6fDgkzVg";

    public static final String PREF_NAME = "NEWT";
    public static final String PREF_IS_LOGIN = "NEWT.IS_LOGIN";

    public static final String PREF_USER = "NEWT.USER";
    public static final String PREF_USER_TYPE = "NEWT.USER_TYPE";
    public static final String PREF_TOKEN = "NEWT.TOKEN";
    public static final String PREF_BOOKMARK = "NEWT.BOOKMARK";

    public static final String REQ_PHONE = "phone";
    public static final String REQ_CODE = "code";
    public static final String REQ_RESEND_CODE = "resend_code";

    public static final String REQ_USER_ID = "user_id";
    public static final String REQ_NAME = "name";
    public static final String REQ_EMAIL = "email";
    public static final String REQ_PASSWORD = "password";
    public static final String REQ_BIRTHDAY = "birthday";
    public static final String REQ_GENDER = "gender";
    public static final String REQ_CITY = "city";
    public static final String REQ_ADDRESS = "address";
    public static final String REQ_FREQUENTLY = "frequently";
    public static final String REQ_PHOTO_CHANGED = "photo_changed";

    public static final String REQ_IMAGE = "image\"; filename=\"image";
    public static final String REQ_VIDEO = "video\"; filename=\"video";

    public static final String REQ_NEWS_ID = "news_id";
    public static final String REQ_COMMENT_ID = "comment_id";
    public static final String REQ_TITLE = "title";
    public static final String REQ_DATE_TIME = "date_time";
    public static final String REQ_CONTENT = "content";
    public static final String REQ_LATITUDE = "latitude";
    public static final String REQ_LONGITUDE = "longitude";
    public static final String REQ_LIKE = "like";
    public static final String REQ_IDS = "ids[]";

    public static final String TAG_LOAD_BASE = "tag_load_base";
    public static final String TAG_SEARCH = "tag_search";
    public static final String TAG_SET_LIKE = "tag_set_like";

    public static final int PLACE_PICKER_REQUEST = 1234;
}