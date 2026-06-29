package com.rank.domain.common.exception;

/**
 * 业务异常基类，继承 RuntimeException。
 * 提供静态工厂方法快速构造常见业务异常。
 */
public class BizException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BizException(String message) {
        super(message);
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 参数校验失败
     */
    public static BizException invalidParam(String message) {
        return new BizException(message);
    }

    /**
     * 数据未找到
     */
    public static BizException notFound(String message) {
        return new BizException(message);
    }

    /**
     * 非法状态
     */
    public static BizException illegalState(String message) {
        return new BizException(message);
    }

    /**
     * 操作被禁止
     */
    public static BizException forbidden(String message) {
        return new BizException(message);
    }
}
