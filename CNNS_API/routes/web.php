<?php

/*
|--------------------------------------------------------------------------
| Web Routes
|--------------------------------------------------------------------------
|
| Here is where you can register web routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| contains the "web" middleware group. Now create something great!
|
*/

Route::get('/', function () {
    return view('welcome');
});

Route::get('est_conn', 'BaseController@estConn');

Route::group(['prefix' => 'register'], function () {
    Route::post('phone', 'UserController@registerPhone');
    Route::post('resend_code', 'UserController@resendCode');
    Route::post('verify_code', 'UserController@verifyCode');
    Route::post('user', 'UserController@registerUser');
});

Route::post('login', 'UserController@login');

Route::group(['prefix' => 'news'], function () {
    Route::get('search', 'BaseController@search');
    Route::get('load_base', 'BaseController@loadBase');
    Route::get('load_most_popular', 'BaseController@loadMostPopular');
    Route::get('load_reported', 'BaseController@loadReported');

    Route::get('load_detail', 'NewsController@loadNewsDetail');
    Route::get('load_comments', 'NewsController@loadComments');
    Route::get('load_videos', 'NewsController@loadVideos');
    Route::get('load_locations', 'BaseController@loadLocations');

    Route::group(['middleware' => 'validate.token'], function () {
        Route::get('load_viewers', 'NewsController@loadViewers');
        Route::get('load_drafts', 'NewsController@loadDrafts');
        Route::get('load_draft_detail', 'NewsController@loadDraftDetail');

        Route::post('set_viewed', 'NewsController@setViewed');
        Route::post('set_like', 'NewsController@setNewsLike');
        Route::post('report', 'NewsController@report');

        Route::post('set_comment_like', 'NewsController@setCommentLike');
        Route::post('post_comment', 'NewsController@postComment');
        Route::post('post', 'NewsController@postNews');
        Route::post('edit', 'NewsController@editNews');

        Route::get('load_bookmarks', 'NewsController@loadBookmarks');
        Route::get('load_histories', 'NewsController@loadHistories');
    });
});

Route::get('load_bookmarks', 'NewsController@loadBookmarks');
Route::get('load_histories', 'NewsController@loadHistories');

Route::group(['middleware' => 'validate.token'], function () {
    Route::post('update_profile', 'UserController@updateProfile');
});

Route::group(['prefix' => 'admin'], function () {
    Route::post('login', 'AdminController@login');
    Route::get('views', 'AdminController@loadViews');

    Route::group(['middleware' => 'validate.token'], function () {
        Route::group(['prefix' => 'load'], function () {
            // lol...
            Route::get('members', 'AdminController@loadMembers');
            Route::get('news', 'AdminController@loadNews');
            Route::get('reports', 'AdminController@loadReportedNews');
            Route::get('users', 'AdminController@loadUsers');
            Route::get('news_detail', 'AdminController@loadNewsDetail');
            Route::get('viewers', 'AdminController@loadViewers');
            Route::get('reporters', 'AdminController@loadReporters');
        });

        Route::post('news/post', 'AdminController@postNews');
        Route::post('news/edit', 'AdminController@editNews');
        Route::post('news/block', 'AdminController@blockNews');
        Route::post('user/block', 'AdminController@blockUser');
    });
});