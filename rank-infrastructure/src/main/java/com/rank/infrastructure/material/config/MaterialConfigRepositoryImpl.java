package com.rank.infrastructure.material.config;

import com.alibaba.fastjson.JSON;
import com.rank.domain.common.CccWorkflowMockSwitch;
import com.rank.domain.material.repository.MaterialConfigRepository;
import com.rank.domain.material.shared.MaterialSceneEnum;
import com.rank.domain.material.vo.MaterialConfigVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 材料配置仓储Mock实现
 * 硬编码返回测试数据，真实接入Lion时替换
 */
@Slf4j
@Repository
public class MaterialConfigRepositoryImpl implements MaterialConfigRepository {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public MaterialConfigVO findByScene(String materialScene) {
        if (CccWorkflowMockSwitch.isEnabled()) {
            try {
                MaterialConfigVO mockConfig = new MaterialConfigVO(
                        DATE_FORMAT.parse("2026-01-01 00:00:00"),
                        DATE_FORMAT.parse("2026-12-31 23:59:59"),
                        "uap_template_mock");
                log.info("ccc-workflow-mock-subagent [MaterialConfigRepositoryImpl findByScene] mock return config={}", JSON.toJSONString(mockConfig));
                return mockConfig;
            } catch (ParseException e) {
                log.error("ccc-workflow-mock-subagent [MaterialConfigRepositoryImpl findByScene] mock data parse error", e);
                return null;
            }
        }
        try {
            log.info("[MaterialConfigRepositoryImpl findByScene] 读取材料配置, materialScene={}", materialScene);

            // 窗口期：2026-07-01 ~ 2026-12-31
            MaterialConfigVO config;
            if (MaterialSceneEnum.MEDICAL_BEAUTY_DOCTOR.name().equals(materialScene)) {
                config = new MaterialConfigVO(
                        DATE_FORMAT.parse("2026-07-01 00:00:00"),
                        DATE_FORMAT.parse("2026-12-31 23:59:59"),
                        "uap_template_medical_beauty_doctor");
            } else if (MaterialSceneEnum.ORAL_DOCTOR.name().equals(materialScene)) {
                config = new MaterialConfigVO(
                        DATE_FORMAT.parse("2026-07-01 00:00:00"),
                        DATE_FORMAT.parse("2026-12-31 23:59:59"),
                        "uap_template_oral_doctor");
            } else {
                log.error("[MaterialConfigRepositoryImpl findByScene] 未知材料场景, materialScene={}", materialScene);
                return null;
            }

            log.info("[MaterialConfigRepositoryImpl findByScene] 读取材料配置完成, materialScene={}", materialScene);
            return config;
        } catch (ParseException e) {
            log.error("[MaterialConfigRepositoryImpl findByScene] 配置日期解析异常", e);
            return null;
        }
    }
}
