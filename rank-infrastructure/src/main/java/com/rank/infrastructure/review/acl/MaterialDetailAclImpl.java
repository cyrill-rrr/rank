package com.rank.infrastructure.review.acl;

import com.rank.domain.review.repository.MaterialDetailAcl;
import com.rank.domain.review.vo.MaterialDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 材料详情 ACL 实现（外部材料服务防腐层）。
 * 按 materialId 实时查询材料详情。
 * 本期不接入真实材料服务，返回硬编码示例数据。
 */
@Slf4j
@Component
public class MaterialDetailAclImpl implements MaterialDetailAcl {

    @Override
    public MaterialDetailVO queryByMaterialId(Long materialId) {
        log.info("[MaterialDetailAclImpl queryByMaterialId] 查询材料详情, materialId={}", materialId);
        if (materialId == null) {
            log.error("[MaterialDetailAclImpl queryByMaterialId] materialId为空", new IllegalArgumentException("materialId为空"));
            return null;
        }

        try {
            // 当前为硬编码示例数据
            // 接入真实 RPC/HTTP 调用后替换远程请求
            MaterialDetailVO result = MaterialDetailVO.of(
                    materialId,
                    "示例材料标题",
                    "DOCTOR",
                    "{}"
            );
            log.info("[MaterialDetailAclImpl queryByMaterialId] 查询材料详情完成, materialId={}", materialId);
            return result;
        } catch (Exception e) {
            log.error("[MaterialDetailAclImpl queryByMaterialId] 材料详情查询异常, materialId={}", materialId, e);
            return null;
        }
    }
}
