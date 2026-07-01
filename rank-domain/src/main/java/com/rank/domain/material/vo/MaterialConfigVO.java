package com.rank.domain.material.vo;

import com.rank.domain.common.exception.BizException;

import java.util.Date;

/**
 * 值对象职责：封装材料配置信息（窗口期 + UAP模板）。
 */
public class MaterialConfigVO {

    /** 窗口期开始时间 */
    private final Date windowStartTime;
    /** 窗口期结束时间 */
    private final Date windowEndTime;
    /** UAP审核模板标识 */
    private final String uapTemplate;

    public MaterialConfigVO(Date windowStartTime, Date windowEndTime, String uapTemplate) {
        this.windowStartTime = windowStartTime;
        this.windowEndTime = windowEndTime;
        this.uapTemplate = uapTemplate;
    }

    public Date getWindowStartTime() {
        return windowStartTime;
    }

    public Date getWindowEndTime() {
        return windowEndTime;
    }

    public String getUapTemplate() {
        return uapTemplate;
    }

    /**
     * 校验当前时间是否在材料提报窗口期内
     */
    public void checkCanOperate() {
        if (windowStartTime == null || windowEndTime == null) {
            throw BizException.illegalState("材料窗口期配置不完整");
        }
        Date now = new Date();
        if (now.before(windowStartTime) || now.after(windowEndTime)) {
            throw BizException.invalidParam("当前不在材料提报窗口期内");
        }
    }
}
