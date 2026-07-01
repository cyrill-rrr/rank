package com.rank.application.material.assembler;

import com.rank.api.material.dto.OperateMaterialRequestDTO;
import com.rank.api.material.dto.OperateMaterialResponseDTO;
import com.rank.api.material.dto.QueryCurrentMaterialRequestDTO;
import com.rank.api.material.dto.QueryCurrentMaterialResponseDTO;
import com.rank.application.material.command.MaterialCommand;
import com.rank.application.material.qry.MaterialQry;
import com.rank.domain.material.shared.MaterialAuditStatusEnum;
import com.rank.domain.material.shared.MaterialDraftStatusEnum;
import com.rank.domain.material.vo.CurrentMaterialResult;
import org.springframework.stereotype.Component;

/**
 * 材料Assembler：DTO <-> Command/Qry 以及 Result <-> ResponseDTO 的双向转换
 */
@Component
public class MaterialAssembler {

    /**
     * 材料操作请求DTO 转为 Command
     */
    public MaterialCommand toCommand(OperateMaterialRequestDTO dto) {
        MaterialCommand command = new MaterialCommand();
        command.setMaterialScene(dto.getMaterialScene());       // 材料场景
        command.setAuditSubjectId(dto.getAuditSubjectId());     // 审计主体ID
        command.setOperationType(dto.getOperationType());       // 操作类型：SAVE_DRAFT/SUBMIT_AUDIT
        command.setMaterialJsonStr(dto.getMaterialJsonStr());   // 材料内容JSON
        return command;
    }

    /**
     * 材料操作结果 转为 ResponseDTO
     */
    public OperateMaterialResponseDTO toOperateMaterialResponse(Long materialId) {
        OperateMaterialResponseDTO dto = new OperateMaterialResponseDTO();
        dto.setMaterialId(materialId);   // 材料记录主键ID
        return dto;
    }

    /**
     * 当前材料查询请求DTO 转为 Qry
     */
    public MaterialQry toQry(QueryCurrentMaterialRequestDTO dto) {
        MaterialQry qry = new MaterialQry();
        qry.setMaterialScene(dto.getMaterialScene());       // 材料场景
        qry.setAuditSubjectId(dto.getAuditSubjectId());     // 审计主体ID
        return qry;
    }

    /**
     * 当前材料查询结果 转为 ResponseDTO
     * materialId=null时所有字段返回null
     */
    public QueryCurrentMaterialResponseDTO toQueryCurrentMaterialResponse(CurrentMaterialResult result) {
        QueryCurrentMaterialResponseDTO dto = new QueryCurrentMaterialResponseDTO();
        if (result == null || result.getMaterialId() == null) {
            // 无材料时全部返回null，前端根据materialId判空
            return dto;
        }
        dto.setMaterialId(result.getMaterialId());                                       // 材料主键ID
        dto.setHasDraft(result.getHasDraft() != null ? result.getHasDraft().name() : null);   // 草稿状态
        dto.setAuditStatus(result.getAuditStatus() != null ? result.getAuditStatus().name() : null); // 审核状态
        dto.setMaterialJsonStr(result.getMaterialJsonStr());                             // 材料内容JSON
        dto.setRejectReason(result.getRejectReason());                                   // 驳回原因
        return dto;
    }
}
