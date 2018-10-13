package com.app.cnns.server.response;

import com.app.cnns.server.model.Comment;
import com.app.cnns.server.model.News;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Erick Sumargo on 2/24/2018.
 */

public class NewsResponse extends BaseResponse {
    public class Data {
        private News news;
        private List<News> newsList;
        private int like, reported;
        private boolean own;

        private List<Comment> comments;

        public void setNews(News news) {
            this.news = news;
        }

        public News getNews() {
            return news;
        }

        public void setNewsList(List<News> newsList) {
            this.newsList = newsList;
        }

        public List<News> getNewsList() {
            return newsList;
        }

        public void setLike(int like) {
            this.like = like;
        }

        public int getLike() {
            return like;
        }

        public void setReported(int reported) {
            this.reported = reported;
        }

        public int getReported() {
            return reported;
        }

        public void setOwn(boolean own) {
            this.own = own;
        }

        public boolean isOwn() {
            return own;
        }

        public void setComments(List<Comment> comments) {
            this.comments = comments;
        }

        public List<Comment> getComments() {
            return comments;
        }
    }

    private Data data;

    public void setData(Data data) {
        this.data = data;
    }

    public Data getData() {
        return data;
    }
}