package com.app.cnns_admin.server.response;

import com.app.cnns_admin.server.model.User;

import java.util.List;

/**
 * Created by Erick Sumargo on 2/24/2018.
 */

public class UserResponse extends BaseResponse {
    public class Data {
        private User user;

        private String token;

        private List<User> users;

        public void setUsers(List<User> users) {
            this.users = users;
        }

        public List<User> getUsers() {
            return users;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public User getUser() {
            return user;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
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