package com.bubble.status.model;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WebResponse {
    private String data;
    private int code;

    @Override
    public String toString() {
        return JSON.toJSONString(new WebResponse(data, code));
    }
}
