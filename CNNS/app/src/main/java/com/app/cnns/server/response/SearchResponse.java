package com.app.cnns.server.response;

import com.app.cnns.server.model.News;
import com.app.cnns.server.model.User;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Erick Sumargo on 2/24/2018.
 */

public class SearchResponse extends BaseResponse {
    public class Data {
        @SerializedName("search_results")
        private List<News> searchResults;

        public void setSearchResults(List<News> searchResults) {
            this.searchResults = searchResults;
        }

        public List<News> getSearchResults() {
            return searchResults;
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