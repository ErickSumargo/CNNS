package com.app.cnns_admin.server.interfaces;

import com.app.cnns_admin.server.response.BaseResponse;
import com.app.cnns_admin.server.response.MainResponse;
import com.app.cnns_admin.server.response.NewsResponse;
import com.app.cnns_admin.server.response.UserResponse;

import java.util.Map;

import io.reactivex.Single;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface REST {
    @GET("est_conn")
    Single<Response<BaseResponse>> estConn();

    @POST("admin/login")
    Single<Response<UserResponse>> login(@Body Map<String, Object> data);

    @GET("admin/views")
    Single<Response<MainResponse>> loadViews();

    @GET("admin/load/members")
    Single<Response<MainResponse>> loadMembers();

    @GET("admin/load/news")
    Single<Response<NewsResponse>> loadNews();

    @GET("admin/load/reports")
    Single<Response<NewsResponse>> loadReportedNews();

    @GET("admin/load/users")
    Single<Response<MainResponse>> loadUsers();

    @GET("admin/load/viewers")
    Single<Response<UserResponse>> loadViewers(@Query("news_id") int newsId);

    @GET("admin/load/reporters")
    Single<Response<UserResponse>> loadReporters(@Query("news_id") int newsId);

    @POST("admin/user/block")
    Single<Response<BaseResponse>> blockUser(@Query("id") int id);

    @GET("admin/load/news_detail")
    Single<Response<NewsResponse>> loadNewsDetail(@Query("news_id") int newsId);

    @POST("admin/news/post")
    Single<Response<BaseResponse>> postNews(@Query("news_id") int newsId);

    @Multipart
    @POST("admin/news/edit")
    Single<Response<BaseResponse>> editNews(@PartMap Map<String, RequestBody> data);

    @POST("admin/news/block")
    Single<Response<BaseResponse>> blockNews(@Query("news_id") int newsId);
}