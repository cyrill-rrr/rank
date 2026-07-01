package com.rank.domain.common;

/**
 * CccWorkflow 临时 Mock 开关。
 * 由 CccWorkflow 注入全局静态开关，JUnit 环境下启用后使领域外方法直接返回 mock 数据而不执行真实依赖。
 * 注意：此类为临时代码，后续 cleanup 流程会删除。
 */
public class CccWorkflowMockSwitch {

    private static volatile boolean enabled = false;

    private CccWorkflowMockSwitch() {
    }

    /**
     * 判断是否启用临时 mock 模式
     */
    public static boolean isEnabled() {
        return enabled;
    }

    /**
     * 设置临时 mock 模式状态（由外部 workflow 或测试框架调用）
     */
    public static void setEnabled(boolean flag) {
        enabled = flag;
    }
}
