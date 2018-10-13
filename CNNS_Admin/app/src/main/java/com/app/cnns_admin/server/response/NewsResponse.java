package com.app.cnns_admin.server.response;

import com.app.cnns_admin.server.model.News;

import java.util.List;

/**
 * Created by Erick Sumargo on 2/24/2018.
 */

public class NewsResponse extends BaseResponse {
    public class Data {
        private List<News> newsList;
        private News news;

        public void setNewsList(List<News> newsList) {
            this.newsList = newsList;
        }

        public List<News> getNewsList() {
            return newsList;
        }

        public void setNews(News news) {
            this.news = news;
        }

        public News getNews() {
            return news;
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