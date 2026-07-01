package com.rank.infrastructure.review.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rank.domain.review.vo.QuestionAnswerVO;
import com.rank.domain.review.model.ReviewTaskEntity;
import com.rank.domain.review.shared.ReviewTaskStatusEnum;
import com.rank.infrastructure.review.po.ReviewTaskPO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ReviewTaskEntity <-> ReviewTaskPO 转换器
 * 处理枚举与数据库字符串的显式映射，以及 question_answer_json_str 的序列化/反序列化。
 */
@Slf4j
@Component
public class ReviewConverter {

    private final ObjectMapper objectMapper;

    public ReviewConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * PO 转 Entity
     */
    public ReviewTaskEntity toEntity(ReviewTaskPO po) {
        if (po == null) {
            return null;
        }
        ReviewTaskEntity entity = new ReviewTaskEntity();
        entity.setId(po.getId());
        entity.setMaterialId(po.getMaterialId());
        entity.setUserId(po.getUserId());
        entity.setScene(po.getScene());
        entity.setStatus(mapStatusToEnum(po.getStatus()));
        entity.setQuestionAnswerList(deserializeQuestionAnswers(po.getQuestionAnswerJsonStr()));
        entity.setDeleted(mapDeletedToBoolean(po.getDeleted()));
        entity.setCreatedTime(po.getCtime());
        entity.setUpdatedTime(po.getMtime());
        return entity;
    }

    /**
     * Entity 转 PO
     */
    public ReviewTaskPO toPO(ReviewTaskEntity entity) {
        if (entity == null) {
            return null;
        }
        ReviewTaskPO po = new ReviewTaskPO();
        po.setId(entity.getId());
        po.setMaterialId(entity.getMaterialId());
        po.setUserId(entity.getUserId());
        po.setScene(entity.getScene());
        po.setStatus(mapStatusToDbStr(entity.getStatus()));
        po.setQuestionAnswerJsonStr(serializeQuestionAnswers(entity.getQuestionAnswerList()));
        po.setDeleted(mapDeletedToInt(entity.getDeleted()));
        po.setCtime(entity.getCreatedTime());
        po.setMtime(entity.getUpdatedTime());
        return po;
    }

    /**
     * JSON 字符串转 List<QuestionAnswerVO>
     */
    private List<QuestionAnswerVO> deserializeQuestionAnswers(String jsonStr) {
        if (jsonStr == null || jsonStr.trim().isEmpty()) {
            return new ArrayList<QuestionAnswerVO>();
        }
        try {
            List<QuestionAnswerVO> list = objectMapper.readValue(jsonStr,
                    new TypeReference<List<QuestionAnswerVO>>() {
                    });
            return list != null ? list : new ArrayList<QuestionAnswerVO>();
        } catch (IOException e) {
            log.error("[ReviewConverter deserializeQuestionAnswers] JSON反序列化失败, jsonStr={}", jsonStr, e);
            return new ArrayList<QuestionAnswerVO>();
        }
    }

    /**
     * List<QuestionAnswerVO> 转 JSON 字符串
     */
    private String serializeQuestionAnswers(List<QuestionAnswerVO> list) {
        if (CollectionUtils.isEmpty(list)) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(list);
        } catch (IOException e) {
            log.error("[ReviewConverter serializeQuestionAnswers] JSON序列化失败", e);
            return "[]";
        }
    }

    /**
     * 数据库字符串状态转枚举
     */
    private ReviewTaskStatusEnum mapStatusToEnum(String status) {
        if (status == null) {
            return null;
        }
        try {
            return ReviewTaskStatusEnum.valueOf(status);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 枚举转数据库字符串
     */
    private String mapStatusToDbStr(ReviewTaskStatusEnum status) {
        if (status == null) {
            return null;
        }
        return status.name();
    }

    /**
     * 数据库 Integer 转 Boolean
     */
    private Boolean mapDeletedToBoolean(Integer deleted) {
        return deleted != null && deleted == 1;
    }

    /**
     * Boolean 转数据库 Integer
     */
    private Integer mapDeletedToInt(Boolean deleted) {
        return Boolean.TRUE.equals(deleted) ? 1 : 0;
    }
}
