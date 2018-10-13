package com.app.cnns.server.response;

import com.app.cnns.server.model.Comment;
import com.app.cnns.server.model.News;

import java.util.List;

/**
 * Created by Erick Sumargo on 2/24/2018.
 */

public class CommentResponse extends BaseResponse {
    public class Data {
        private News news;
        private List<Comment> comments;

        public void setNews(News news) {
            this.news = news;
        }

        public News getNews() {
            return news;
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