package com.rank.domain.sign.shared;

/**
 * 报名场景枚举
 */
public enum SignSceneEnum {

    /**
     * 机构榜
     */
    INSTITUTION(1),
    /**
     * 医生榜
     */
    DOCTOR(2);

    private final int code;

    SignSceneEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    /**
     * 根据code获取枚举
     */
    public static SignSceneEnum fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (SignSceneEnum value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }
}
