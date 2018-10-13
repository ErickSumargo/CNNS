package com.app.cnns.server.response.wrapper;


import com.app.cnns.server.model.News;
import com.app.cnns.server.response.BaseResponse;

import java.util.List;

public class BaseWrapper extends BaseResponse {
    public class Data {
        private List<News> tops, daily;

        public void setTops(List<News> tops) {
            this.tops = tops;
        }

        public List<News> getTops() {
            return tops;
        }

        public void setDaily(List<News> daily) {
            this.daily = daily;
        }

        public List<News> getDaily() {
            return daily;
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