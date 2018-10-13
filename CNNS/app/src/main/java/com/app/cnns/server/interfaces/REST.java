package com.app.cnns.server.interfaces;

import java.util.List;
import java.util.Map;

import com.app.cnns.server.response.BaseResponse;
import com.app.cnns.server.response.CommentResponse;
import com.app.cnns.server.response.NewsResponse;
import com.app.cnns.server.response.RegistrationResponse;
import com.app.cnns.server.response.SearchResponse;
import com.app.cnns.server.response.UserResponse;
import com.app.cnns.server.response.wrapper.BaseWrapper;

import io.reactivex.Single;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface REST {
    @GET("est_conn")
    Single<Response<BaseResponse>> estConn();

    @POST("register/phone")
    Single<Response<RegistrationResponse>> registerPhone(@Body Map<String, Object> data);

    @POST("register/resend_code")
    Single<Response<RegistrationResponse>> resendCode(@Body Map<String, Object> data);

    @POST("register/verify_code")
    Single<Response<RegistrationResponse>> verifyCode(@Body Map<String, Object> data);

    @POST("register/user")
    Single<Response<UserResponse>> registerUser(@Body Map<String, Object> data);

    @POST("login")
    Single<Response<UserResponse>> login(@Body Map<String, Object> data);

    @Multipart
    @POST("update_profile")
    Single<Response<UserResponse>> updateProfile(@PartMap Map<String, RequestBody> data);

    @GET("news/search")
    Single<Response<SearchResponse>> search(@Query("query") String query);

    @GET("news/load_base")
    Single<Response<BaseWrapper>> loadBase();

    @GET("news/load_detail")
    Single<Response<NewsResponse>> loadNewsDetail(@Query("user_id") int userId, @Query("news_id") int newsId);

    @GET("news/load_viewers")
    Single<Response<UserResponse>> loadViewers(@Query("news_id") int newsId);

    @GET("news/load_drafts")
    Single<Response<NewsResponse>> loadDrafts();

    @GET("news/load_draft_detail")
    Single<Response<NewsResponse>> loadDraftDetail(@Query("news_id") int newsId);

    @POST("news/set_viewed")
    Single<Response<BaseResponse>> setViewed(@Body Map<String, Object> data);

    @POST("news/set_like")
    Single<Response<BaseResponse>> setNewsLike(@Body Map<String, Object> data);

    @POST("news/report")
    Single<Response<BaseResponse>> report(@Body Map<String, Object> data);

    @GET("news/load_comments")
    Single<Response<CommentResponse>> loadComments(@Query("user_id") int userId, @Query("news_id") int newsId);

    @POST("news/set_comment_like")
    Single<Response<BaseResponse>> setCommentLike(@Body Map<String, Object> data);

    @POST("news/post_comment")
    Single<Response<BaseResponse>> postComment(@Body Map<String, Object> data);

    @Multipart
    @POST("news/post")
    Single<Response<BaseResponse>> postNews(@PartMap Map<String, RequestBody> data);

    @Multipart
    @POST("news/edit")
    Single<Response<BaseResponse>> editNews(@PartMap Map<String, RequestBody> data);

    @GET("news/load_videos")
    Single<Response<NewsResponse>> loadVideos();

    @GET("news/load_most_popular")
    Single<Response<NewsResponse>> loadMostPopular();

    @GET("news/load_reported")
    Single<Response<NewsResponse>> loadReported();

    @GET("news/load_bookmarks")
    Single<Response<NewsResponse>> loadBookmarks(@Query("ids") String ids);

    @GET("news/load_histories")
    Single<Response<NewsResponse>> loadHistories(@Query("id") int id);

    @GET("news/load_locations")
    Single<Response<NewsResponse>> loadLocations();
}