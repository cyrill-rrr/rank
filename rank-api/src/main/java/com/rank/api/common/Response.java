package com.rank.api.common;

import lombok.Data;

/**
 * 统一响应体
 *
 * @param <T> data 泛型类型
 */
@Data
public class Response<T> {

    private int code;
    private String msg;
    private T data;
    private String traceId;

    public Response() {
    }

    public Response(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 成功响应
     */
    public static <T> Response<T> success(T data) {
        return new Response<>(200, "success", data);
    }

    /**
     * 失败响应
     */
    public static <T> Response<T> fail(int code, String msg) {
        return new Response<>(code, msg, null);
    }
}
