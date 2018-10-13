package com.app.cnns.helper;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class Utils {
    private Context context;

    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Utils(Context context) {
        this.context = context;
    }

    public static Utils with(Context context) {
        return new Utils(context);
    }

    public void playDefSound() {
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        manager.setStreamVolume(AudioManager.STREAM_MUSIC, 10, 0);

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        MediaPlayer player = MediaPlayer.create(context, notification);
        player.start();
    }

    public String formatDate(String createdAt, String format) {
        SimpleDateFormat dateDefault = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        try {
            return dateFormat.format(dateDefault.parse(createdAt));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getCurrentDate() {
        Date currentDate = Calendar.getInstance().getTime();
        return df.format(currentDate);
    }

    public RequestBody convertToRequestBody(String s) {
        return RequestBody.create(MediaType.parse("text/plain"), s);
    }

    public RequestBody convertToRequestBody(File file) {
        return RequestBody.create(MediaType.parse(getMimeType(Uri.fromFile(file))), file);
    }

    public String getURLMediaImage(String name, String pathType) {
        return Constant.URL_MEDIA_IMAGE + pathType + "/" + name;
    }

    public String getURLMediaVideo(String name) {
        return Constant.URL_MEDIA_VIDEO + name;
    }

    public String getMimeType(Uri uri) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
        return mimeType;
    }

    public String getFileExtension(Uri uri) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
        return extension;
    }

    public Bitmap getThumbnail(String path) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.fromFile(new File(path)));
            Bitmap thumbBitmap = ThumbnailUtils.extractThumbnail(bitmap, 120, 120);
            return thumbBitmap;
        } catch (IOException ex) {
        }
        return null;
    }
}