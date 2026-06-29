package com.rank.application.sign.qry;

/**
 * 报名分页查询Qry
 */
public class SignPageQry {

    private String signScene;    // 报名场景
    private Long indexShopId;    // 冗余机构ID
    private Integer status;      // 筛选状态，为空查全部
    private int pageNo;          // 页码
    private int pageSize;        // 每页大小

    public String getSignScene() {
        return signScene;
    }

    public void setSignScene(String signScene) {
        this.signScene = signScene;
    }

    public Long getIndexShopId() {
        return indexShopId;
    }

    public void setIndexShopId(Long indexShopId) {
        this.indexShopId = indexShopId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
