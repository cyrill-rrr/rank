package com.rank.domain.material.vo;

/**
 * UAP审核请求值对象
 */
public class UapAuditRequest {

    /**
     * 审核模板
     */
    private final String template;

    /**
     * 审核数据JSON
     */
    private final String dataJson;

    public UapAuditRequest(String template, String dataJson) {
        this.template = template;
        this.dataJson = dataJson;
    }

    public String getTemplate() {
        return template;
    }

    public String getDataJson() {
        return dataJson;
    }
}
