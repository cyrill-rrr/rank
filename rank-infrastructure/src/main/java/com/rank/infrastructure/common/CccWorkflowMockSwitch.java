package com.rank.infrastructure.common;

/**
 * 临时 mock 开关，用于 JUnit 测试场景下跳过领域外依赖的真实执行。
 * 由 ccc-workflow-bugfix 代理自动添加，后续 cleanup 会删除本类及所有引用。
 */
public class CccWorkflowMockSwitch {

    private CccWorkflowMockSwitch() {
    }

    /**
     * 是否启用 mock 模式。
     * JUnit 测试前通过 {@code CccWorkflowMockSwitch.setEnabled(true)} 开启，
     * domain-outside 方法入口检查此开关，命中后直接构造 mock 数据返回。
     */
    private static volatile boolean enabled = false;

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean enabled) {
        CccWorkflowMockSwitch.enabled = enabled;
    }
}
