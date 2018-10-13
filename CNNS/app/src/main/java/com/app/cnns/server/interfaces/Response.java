package com.app.cnns.server.interfaces;

import com.app.cnns.server.response.BaseResponse;

/**
 * Created by edinofri on 02/11/2016.
 */

public interface Response {
    void onSuccess(BaseResponse base);

    void onFailure(String message);
}