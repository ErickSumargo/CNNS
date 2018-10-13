package com.app.cnns_admin.server.response;

import com.app.cnns_admin.server.model.User;

import java.util.List;

/**
 * Created by Erick Sumargo on 2/24/2018.
 */

public class MainResponse extends BaseResponse {
    public class Data {
        private List<User> users;

        private int views;

        public void setUsers(List<User> users) {
            this.users = users;
        }

        public List<User> getUsers() {
            return users;
        }

        public void setViews(int views) {
            this.views = views;
        }

        public int getViews() {
            return views;
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