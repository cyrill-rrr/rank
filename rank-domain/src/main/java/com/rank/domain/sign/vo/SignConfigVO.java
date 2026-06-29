package com.rank.domain.sign.vo;

import com.rank.domain.common.exception.BizException;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 值对象职责：承接Lion报名配置，收口窗口期和可选项目校验。
 */
public class SignConfigVO {

    private final SignWindowVO window;
    private final List<SignProjectVO> projectList;

    public SignConfigVO(SignWindowVO window, List<SignProjectVO> projectList) {
        this.window = window;
        this.projectList = projectList != null ? projectList : Collections.emptyList();
    }

    public SignWindowVO getWindow() {
        return window;
    }

    public List<SignProjectVO> getProjectList() {
        return projectList;
    }

    /**
     * 校验窗口期是否允许操作
     *
     * @param operationType 操作类型
     */
    public void checkCanOperate(String operationType) {
        if (window == null) {
            throw BizException.illegalState("报名窗口期未配置，不能提报或退报");
        }
        window.checkCanOperate();
    }

    /**
     * 校验传入的项目code列表是否全部可选
     *
     * @param projectCodeList 本次请求操作的项目code列表
     */
    public void checkProjectsSelectable(List<Integer> projectCodeList) {
        if (projectList.isEmpty()) {
            throw BizException.invalidParam("暂无可选提报项目");
        }
        Set<Integer> validCodes = projectList.stream()
                .map(SignProjectVO::getProjectCode)
                .collect(Collectors.toSet());
        for (Integer code : projectCodeList) {
            if (!validCodes.contains(code)) {
                throw BizException.invalidParam("提报项目不可选，projectCode=" + code);
            }
        }
    }
}
