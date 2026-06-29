package com.rank.domain.sign.repository;

import com.rank.domain.sign.vo.SignConfigVO;

/**
 * 报名配置仓储接口：读取Lion配置
 */
public interface SignConfigRepository {

    /**
     * 按报名场景查询配置
     *
     * @param signScene 报名场景
     * @return SignConfigVO 包含窗口期和可选项目列表
     */
    SignConfigVO findByScene(String signScene);
}
