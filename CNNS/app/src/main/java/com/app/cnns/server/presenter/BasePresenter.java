package com.app.cnns.server.presenter;

import android.content.Context;
import android.net.Uri;

import com.app.cnns.helper.API;
import com.app.cnns.helper.Constant;
import com.app.cnns.helper.Utils;
import com.app.cnns.server.interfaces.Response;
import com.app.cnns.server.response.BaseResponse;
import com.app.cnns.server.response.CommentResponse;
import com.app.cnns.server.response.NewsResponse;
import com.app.cnns.server.response.SearchResponse;
import com.app.cnns.server.response.UserResponse;
import com.app.cnns.server.response.wrapper.BaseWrapper;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

public class BasePresenter {
    private Context context;
    private Response response;

    public CompositeDisposable disposables;

    public BasePresenter(Context context, Response response) {
        this.context = context;
        this.response = response;
        disposables = new CompositeDisposable();
    }

    public void estConn() {
        API.with(context, true).getRest().estConn()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<retrofit2.Response<BaseResponse>>() {
                               @Override
                               public void onSubscribe(Disposable d) {
                                   disposables.add(d);
                               }

                               @Override
                               public void onSuccess(retrofit2.Response<BaseResponse> value) {
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

    public void loadBase() {
        API.with(context, true).getRest().loadBase()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<retrofit2.Response<BaseWrapper>>() {
                               @Override
                               public void onSubscribe(Disposable d) {
                                   disposables.add(d);
                               }

                               @Override
                               public void onSuccess(retrofit2.Response<BaseWrapper> value) {
                                   if (value.code() == 200) {
                                       value.body().setTag(Constant.TAG_LOAD_BASE);
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

    public void search(String query) {
        API.with(context, true).getRest().search(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<retrofit2.Response<SearchResponse>>() {
                               @Override
                               public void onSubscribe(Disposable d) {
                                   disposables.add(d);
                               }

                               @Override
                               public void onSuccess(retrofit2.Response<SearchResponse> value) {
                                   if (value.code() == 200) {
                                       value.body().setTag(Constant.TAG_SEARCH);
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

    public void loadNewsDetail(int userId, int newsId) {
        API.with(context, true).getRest().loadNewsDetail(userId, newsId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<retrofit2.Response<NewsResponse>>() {
                               @Override
                               public void onSubscribe(Disposable d) {
                                   disposables.add(d);
                               }

                               @Override
                               public void onSuccess(retrofit2.Response<NewsResponse> value) {
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

    public void loadViewers(int newsId) {
        API.with(context, true).getRest().loadViewers(newsId)
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

    public void loadDrafts() {
        API.with(context, true).getRest().loadDrafts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<retrofit2.Response<NewsResponse>>() {
                               @Override
                               public void onSubscribe(Disposable d) {
                                   disposables.add(d);
                               }

                               @Override
                               public void onSuccess(retrofit2.Response<NewsResponse> value) {
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

    public void loadDraftDetail(int newsId) {
        API.with(context, true).getRest().loadDraftDetail(newsId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<retrofit2.Response<NewsResponse>>() {
                               @Override
                               public void onSubscribe(Disposable d) {
                                   disposables.add(d);
                               }

                               @Override
                               public void onSuccess(retrofit2.Response<NewsResponse> value) {
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

    public void setViewed(int id) {
        Map<String, Object> data = new HashMap<>();
        data.put(Constant.REQ_NEWS_ID, id);

        API.with(context, true).getRest().setViewed(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<retrofit2.Response<BaseResponse>>() {
                               @Override
                               public void onSubscribe(Disposable d) {
                                   disposables.add(d);
                               }

                               @Override
                               public void onSuccess(retrofit2.Response<BaseResponse> value) {
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

    public void setNewsLike(int id, int like) {
        Map<String, Object> data = new HashMap<>();
        data.put(Constant.REQ_NEWS_ID, id);
        data.put(Constant.REQ_LIKE, like);

        API.with(context, true).getRest().setNewsLike(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<retrofit2.Response<BaseResponse>>() {
                               @Override
                               public void onSubscribe(Disposable d) {
                                   disposables.add(d);
                               }

                               @Override
                               public void onSuccess(retrofit2.Response<BaseResponse> value) {
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

    public void report(int id) {
        Map<String, Object> data = new HashMap<>();
        data.put(Constant.REQ_NEWS_ID, id);

        API.with(context, true).getRest().report(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<retrofit2.Response<BaseResponse>>() {
                               @Override
                               public void onSubscribe(Disposable d) {
                                   disposables.add(d);
                               }

                               @Override
                               public void onSuccess(retrofit2.Response<BaseResponse> value) {
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

    public void loadComments(int userId, int newsId) {
        API.with(context, true).getRest().loadComments(userId, newsId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<retrofit2.Response<CommentResponse>>() {
                               @Override
                               public void onSubscribe(Disposable d) {
                                   disposables.add(d);
                               }

                               @Override
                               public void onSuccess(retrofit2.Response<CommentResponse> value) {
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

    public void setCommentLike(int commentId, int like) {
        Map<String, Object> data = new HashMap<>();
        data.put(Constant.REQ_COMMENT_ID, commentId);
        data.put(Constant.REQ_LIKE, like);

        API.with(context, true).getRest().setCommentLike(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<retrofit2.Response<BaseResponse>>() {
                               @Override
                               public void onSubscribe(Disposable d) {
                                   disposables.add(d);
                               }

                               @Override
                               public void onSuccess(retrofit2.Response<BaseResponse> value) {
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

    public void postComment(int newsId, String content) {
        Map<String, Object> data = new HashMap<>();
        data.put(Constant.REQ_NEWS_ID, newsId);
        data.put(Constant.REQ_CONTENT, content);

        API.with(context, true).getRest().postComment(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<retrofit2.Response<BaseResponse>>() {
                               @Override
                               public void onSubscribe(Disposable d) {
                                   disposables.add(d);
                               }

                               @Override
                               public void onSuccess(retrofit2.Response<BaseResponse> value) {
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

    public void postNews(String title, double latitude, double longitude, String dateTime, String content, String imagePath, String videoPath) {
        Map<String, RequestBody> data = new HashMap<>();
        data.put(Constant.REQ_TITLE, Utils.with(context).convertToRequestBody(title));
        data.put(Constant.REQ_LATITUDE, Utils.with(context).convertToRequestBody(String.valueOf(latitude)));
        data.put(Constant.REQ_LONGITUDE, Utils.with(context).convertToRequestBody(String.valueOf(longitude)));
        data.put(Constant.REQ_DATE_TIME, Utils.with(context).convertToRequestBody(dateTime));
        data.put(Constant.REQ_CONTENT, Utils.with(context).convertToRequestBody(content));

        if (!imagePath.isEmpty()) {
            File file = new File(imagePath);
            if (file.exists()) {
                data.put(Constant.REQ_IMAGE + "." + Utils.with(context).getFileExtension(Uri.fromFile(file)) + "\"", Utils.with(context).convertToRequestBody(file));
            }
        }
        if (!videoPath.isEmpty()) {
            File file = new File(videoPath);
            if (file.exists()) {
                data.put(Constant.REQ_VIDEO + "." + Utils.with(context).getFileExtension(Uri.fromFile(file)) + "\"", Utils.with(context).convertToRequestBody(file));
            }
        }

        API.with(context, false).getRest().postNews(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<retrofit2.Response<BaseResponse>>() {
                               @Override
                               public void onSubscribe(Disposable d) {
                                   disposables.add(d);
                               }

                               @Override
                               public void onSuccess(retrofit2.Response<BaseResponse> value) {
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

    public void editNews(int newsId, String title, double latitude, double longitude, String dateTime, String content, String imagePath, String videoPath, boolean imageChanged, boolean videoChanged) {
        Map<String, RequestBody> data = new HashMap<>();
        data.put(Constant.REQ_NEWS_ID, Utils.with(context).convertToRequestBody(String.valueOf(newsId)));
        data.put(Constant.REQ_TITLE, Utils.with(context).convertToRequestBody(title));
        data.put(Constant.REQ_LATITUDE, Utils.with(context).convertToRequestBody(String.valueOf(latitude)));
        data.put(Constant.REQ_LONGITUDE, Utils.with(context).convertToRequestBody(String.valueOf(longitude)));
        data.put(Constant.REQ_DATE_TIME, Utils.with(context).convertToRequestBody(dateTime));
        data.put(Constant.REQ_CONTENT, Utils.with(context).convertToRequestBody(content));

        if (imageChanged) {
            data.put("image_changed", Utils.with(context).convertToRequestBody("1"));
            if (!imagePath.isEmpty()) {
                File file = new File(imagePath);
                if (file.exists()) {
                    data.put(Constant.REQ_IMAGE + "." + Utils.with(context).getFileExtension(Uri.fromFile(file)) + "\"", Utils.with(context).convertToRequestBody(file));
                }
            }
        } else {
            data.put("image_changed", Utils.with(context).convertToRequestBody("0"));
        }

        if (videoChanged) {
            data.put("video_changed", Utils.with(context).convertToRequestBody("1"));
            if (!videoPath.isEmpty()) {
                File file = new File(videoPath);
                if (file.exists()) {
                    data.put(Constant.REQ_VIDEO + "." + Utils.with(context).getFileExtension(Uri.fromFile(file)) + "\"", Utils.with(context).convertToRequestBody(file));
                }
            }
        } else {
            data.put("video_changed", Utils.with(context).convertToRequestBody("0"));
        }

        API.with(context, false).getRest().editNews(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<retrofit2.Response<BaseResponse>>() {
                               @Override
                               public void onSubscribe(Disposable d) {
                                   disposables.add(d);
                               }

                               @Override
                               public void onSuccess(retrofit2.Response<BaseResponse> value) {
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

    public void loadVideos() {
        API.with(context, true).getRest().loadVideos()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<retrofit2.Response<NewsResponse>>() {
                               @Override
                               public void onSubscribe(Disposable d) {
                                   disposables.add(d);
                               }

                               @Override
                               public void onSuccess(retrofit2.Response<NewsResponse> value) {
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

    public void loadMostPopular() {
        API.with(context, true).getRest().loadMostPopular()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<retrofit2.Response<NewsResponse>>() {
                               @Override
                               public void onSubscribe(Disposable d) {
                                   disposables.add(d);
                               }

                               @Override
                               public void onSuccess(retrofit2.Response<NewsResponse> value) {
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

    public void loadReported() {
        API.with(context, true).getRest().loadReported()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<retrofit2.Response<NewsResponse>>() {
                               @Override
                               public void onSubscribe(Disposable d) {
                                   disposables.add(d);
                               }

                               @Override
                               public void onSuccess(retrofit2.Response<NewsResponse> value) {
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

    public void loadBookmarks(List<Integer> ids) {
        String idsS ="";
        for(int i =0;i<ids.size();i++) {
            idsS += ids.get(i);
            if (i < ids.size() - 1) {
                idsS += " ";
            }
        }
        API.with(context, true).getRest().loadBookmarks(idsS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<retrofit2.Response<NewsResponse>>() {
                               @Override
                               public void onSubscribe(Disposable d) {
                                   disposables.add(d);
                               }

                               @Override
                               public void onSuccess(retrofit2.Response<NewsResponse> value) {
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

    public void loadHistories(int id) {
        API.with(context, true).getRest().loadHistories(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<retrofit2.Response<NewsResponse>>() {
                               @Override
                               public void onSubscribe(Disposable d) {
                                   disposables.add(d);
                               }

                               @Override
                               public void onSuccess(retrofit2.Response<NewsResponse> value) {
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

    public void loadLocations() {
        API.with(context, true).getRest().loadLocations()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<retrofit2.Response<NewsResponse>>() {
                               @Override
                               public void onSubscribe(Disposable d) {
                                   disposables.add(d);
                               }

                               @Override
                               public void onSuccess(retrofit2.Response<NewsResponse> value) {
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