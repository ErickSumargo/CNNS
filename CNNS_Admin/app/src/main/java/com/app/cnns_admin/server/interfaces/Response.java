package com.app.cnns_admin.server.interfaces;

import com.app.cnns_admin.server.response.BaseResponse;

/**
 * Created by edinofri on 02/11/2016.
 */

public interface Response {
    void onSuccess(BaseResponse base);

    void onFailure(String message);
}