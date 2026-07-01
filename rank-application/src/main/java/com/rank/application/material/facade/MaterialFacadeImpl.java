package com.rank.application.material.facade;

import com.alibaba.fastjson.JSON;
import com.rank.api.common.Response;
import com.rank.api.material.dto.OperateMaterialRequestDTO;
import com.rank.api.material.dto.OperateMaterialResponseDTO;
import com.rank.api.material.dto.QueryCurrentMaterialRequestDTO;
import com.rank.api.material.dto.QueryCurrentMaterialResponseDTO;
import com.rank.api.material.facade.MaterialFacade;
import com.rank.application.material.assembler.MaterialAssembler;
import com.rank.application.material.command.MaterialCommand;
import com.rank.application.material.qry.MaterialQry;
import com.rank.application.material.service.MaterialCommandAppService;
import com.rank.application.material.service.MaterialQueryAppService;
import com.rank.domain.common.exception.BizException;
import com.rank.domain.material.shared.MaterialOperationEnum;
import com.rank.domain.material.vo.CurrentMaterialResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 司南榜材料提报Facade实现
 */
@Slf4j
@Service
public class MaterialFacadeImpl implements MaterialFacade {

    private static final String EMPTY_JSON = "{}";

    private final MaterialAssembler materialAssembler;
    private final MaterialCommandAppService materialCommandAppService;
    private final MaterialQueryAppService materialQueryAppService;

    public MaterialFacadeImpl(MaterialAssembler materialAssembler,
                               MaterialCommandAppService materialCommandAppService,
                               MaterialQueryAppService materialQueryAppService) {
        this.materialAssembler = materialAssembler;
        this.materialCommandAppService = materialCommandAppService;
        this.materialQueryAppService = materialQueryAppService;
    }

    @Override
    public Response<OperateMaterialResponseDTO> operateMaterial(OperateMaterialRequestDTO request) {
        long start = System.currentTimeMillis();
        try {
            // 1. 参数校验（快速失败）
            validateOperateMaterial(request);

            // 2. 送审时额外拦截空材料
            if (MaterialOperationEnum.SUBMIT_AUDIT.name().equals(request.getOperationType())) {
                validateSubmitAuditMaterial(request);
            }

            // 3. DTO转Command
            MaterialCommand command = materialAssembler.toCommand(request);

            // 4. 执行材料操作
            Long materialId = materialCommandAppService.operateMaterial(command);

            // 5. 结果转换
            OperateMaterialResponseDTO response = materialAssembler.toOperateMaterialResponse(materialId);
            return Response.success(response);
        } catch (IllegalArgumentException e) {
            log.error("[MaterialFacadeImpl operateMaterial] 参数校验失败, request={}", JSON.toJSONString(request), e);
            return Response.fail(400011, e.getMessage());
        } catch (BizException e) {
            log.error("[MaterialFacadeImpl operateMaterial] 业务异常, request={}", JSON.toJSONString(request), e);
            return Response.fail(400011, e.getMessage());
        } catch (Exception e) {
            log.error("[MaterialFacadeImpl operateMaterial] 材料操作异常, request={}", JSON.toJSONString(request), e);
            return Response.fail(400011, "材料操作失败");
        } finally {
            log.info("[MaterialFacadeImpl operateMaterial] 耗时:{}ms", System.currentTimeMillis() - start);
        }
    }

    @Override
    public Response<QueryCurrentMaterialResponseDTO> queryCurrentMaterial(QueryCurrentMaterialRequestDTO request) {
        long start = System.currentTimeMillis();
        try {
            // 1. 参数校验
            validateQueryCurrentMaterial(request);

            // 2. DTO转Qry
            MaterialQry qry = materialAssembler.toQry(request);

            // 3. 查询
            CurrentMaterialResult result = materialQueryAppService.queryCurrentMaterial(qry);

            // 4. 结果转换
            QueryCurrentMaterialResponseDTO response = materialAssembler.toQueryCurrentMaterialResponse(result);
            return Response.success(response);
        } catch (IllegalArgumentException e) {
            log.error("[MaterialFacadeImpl queryCurrentMaterial] 参数校验失败, request={}", JSON.toJSONString(request), e);
            return Response.fail(400012, e.getMessage());
        } catch (BizException e) {
            log.error("[MaterialFacadeImpl queryCurrentMaterial] 业务异常, request={}", JSON.toJSONString(request), e);
            return Response.fail(400012, e.getMessage());
        } catch (Exception e) {
            log.error("[MaterialFacadeImpl queryCurrentMaterial] 查询材料异常, request={}", JSON.toJSONString(request), e);
            return Response.fail(400012, "查询材料信息失败");
        } finally {
            log.info("[MaterialFacadeImpl queryCurrentMaterial] 耗时:{}ms", System.currentTimeMillis() - start);
        }
    }

    /**
     * 校验材料操作参数
     */
    private void validateOperateMaterial(OperateMaterialRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("请求参数不能为空");
        }
        if (request.getMaterialScene() == null || request.getMaterialScene().trim().isEmpty()) {
            throw new IllegalArgumentException("材料场景不能为空");
        }
        if (request.getAuditSubjectId() == null || request.getAuditSubjectId().trim().isEmpty()) {
            throw new IllegalArgumentException("审计主体ID不能为空");
        }
        if (request.getOperationType() == null || request.getOperationType().trim().isEmpty()) {
            throw new IllegalArgumentException("操作类型不能为空");
        }
    }

    /**
     * 校验送审时材料内容非空
     */
    private void validateSubmitAuditMaterial(OperateMaterialRequestDTO request) {
        String jsonStr = request.getMaterialJsonStr();
        if (jsonStr == null || jsonStr.trim().isEmpty() || EMPTY_JSON.equals(jsonStr.trim())) {
            throw new IllegalArgumentException("送审时材料内容不能为空");
        }
    }

    /**
     * 校验当前材料查询参数
     */
    private void validateQueryCurrentMaterial(QueryCurrentMaterialRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("请求参数不能为空");
        }
        if (request.getMaterialScene() == null || request.getMaterialScene().trim().isEmpty()) {
            throw new IllegalArgumentException("材料场景不能为空");
        }
        if (request.getAuditSubjectId() == null || request.getAuditSubjectId().trim().isEmpty()) {
            throw new IllegalArgumentException("审计主体ID不能为空");
        }
    }
}
