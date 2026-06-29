package com.rank.domain.sign.vo;

import com.rank.domain.common.exception.BizException;

import java.util.Date;

/**
 * 值对象职责：封装报名窗口期判断。
 * 核心不变量：开始时间和结束时间必须同时存在，且开始时间早于结束时间。
 */
public class SignWindowVO {

    private final Date startTime;
    private final Date endTime;

    public SignWindowVO(Date startTime, Date endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    /**
     * 校验当前时间是否在窗口期内
     */
    public void checkCanOperate() {
        Date now = new Date();
        if (now.before(startTime) || now.after(endTime)) {
            throw BizException.invalidParam("当前不在报名窗口期内，不能提报或退报");
        }
    }
}
