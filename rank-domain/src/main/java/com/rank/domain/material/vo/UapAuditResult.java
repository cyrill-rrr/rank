package com.rank.domain.material.vo;

/**
 * UAP审核结果值对象
 */
public class UapAuditResult {

    /**
     * UAP唯一标识
     */
    private final String uapUniqueId;

    /**
     * 是否成功
     */
    private final boolean success;

    /**
     * 错误信息
     */
    private final String errorMsg;

    public UapAuditResult(String uapUniqueId, boolean success, String errorMsg) {
        this.uapUniqueId = uapUniqueId;
        this.success = success;
        this.errorMsg = errorMsg;
    }

    public String getUapUniqueId() {
        return uapUniqueId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    /**
     * 创建成功结果
     */
    public static UapAuditResult success(String uapUniqueId) {
        return new UapAuditResult(uapUniqueId, true, null);
    }

    /**
     * 创建失败结果
     */
    public static UapAuditResult fail(String errorMsg) {
        return new UapAuditResult(null, false, errorMsg);
    }
}
