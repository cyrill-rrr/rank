package com.rank.infrastructure.sign.config;

import com.rank.domain.sign.repository.SignConfigRepository;
import com.rank.domain.sign.vo.SignConfigVO;
import com.rank.domain.sign.vo.SignProjectVO;
import com.rank.domain.sign.vo.SignWindowVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 报名配置仓储Mock实现
 * 硬编码返回测试数据，真实接入Lion时替换
 */
@Slf4j
@Repository
public class SignConfigRepositoryImpl implements SignConfigRepository {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public SignConfigVO findByScene(String signScene) {
        try {
            log.info("[SignConfigRepositoryImpl findByScene] 读取报名配置, signScene={}", signScene);

            SignWindowVO window = new SignWindowVO(
                    DATE_FORMAT.parse("2026-07-01 00:00:00"),
                    DATE_FORMAT.parse("2026-12-31 23:59:59"));

            List<SignProjectVO> projectList;
            if ("INSTITUTION".equals(signScene)) {
                // 机构榜可选项目：101, 102, 103
                projectList = Arrays.asList(
                        new SignProjectVO(101, 1),
                        new SignProjectVO(102, 1),
                        new SignProjectVO(103, 1));
            } else if ("DOCTOR".equals(signScene)) {
                // 医生榜可选项目：101, 102
                projectList = Arrays.asList(
                        new SignProjectVO(101, 2),
                        new SignProjectVO(102, 2));
            } else {
                log.warn("[SignConfigRepositoryImpl findByScene] 未知报名场景, signScene={}", signScene);
                return null;
            }

            return new SignConfigVO(window, projectList);
        } catch (ParseException e) {
            log.error("[SignConfigRepositoryImpl findByScene] 配置日期解析异常", e);
            return null;
        }
    }
}
