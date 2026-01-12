package com.bubble.status.model;

import com.bubble.status.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommonWebResponse<T> {
    private T data;
    private int code;
    private String msg;

    public CommonWebResponse(T data, int code) {
        this.data = data;
        this.code = code;
    }

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }
}
