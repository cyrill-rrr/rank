package com.rank.application.sign.facade;

import com.rank.api.common.Response;
import com.rank.api.sign.dto.DoctorDTO;
import com.rank.api.sign.dto.OperateSignRequestDTO;
import com.rank.api.sign.dto.OperateSignResponseDTO;
import com.rank.api.sign.dto.QuerySelectableProjectsRequestDTO;
import com.rank.api.sign.dto.QuerySelectableProjectsResponseDTO;
import com.rank.api.sign.dto.QueryShopDoctorsRequestDTO;
import com.rank.api.sign.dto.QueryShopDoctorsResponseDTO;
import com.rank.api.sign.dto.QuerySignPageRequestDTO;
import com.rank.api.sign.dto.QuerySignPageResponseDTO;
import com.rank.api.sign.facade.SignFacade;
import com.rank.application.sign.assembler.SignAssembler;
import com.rank.application.sign.command.OperateSignCommand;
import com.rank.application.sign.qry.SelectableProjectQry;
import com.rank.application.sign.qry.ShopDoctorQry;
import com.rank.application.sign.qry.SignPageQry;
import com.rank.application.sign.service.SignCommandAppService;
import com.rank.application.sign.service.SignQueryAppService;
import com.rank.domain.common.PageResult;
import com.rank.domain.common.exception.BizException;
import com.rank.domain.sign.model.SignEntity;
import com.rank.domain.sign.vo.SignProjectVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 司南榜报名Facade实现
 */
@Slf4j
@Service
public class SignFacadeImpl implements SignFacade {

    private final SignAssembler signAssembler;
    private final SignCommandAppService signCommandAppService;
    private final SignQueryAppService signQueryAppService;

    public SignFacadeImpl(SignAssembler signAssembler,
                          SignCommandAppService signCommandAppService,
                          SignQueryAppService signQueryAppService) {
        this.signAssembler = signAssembler;
        this.signCommandAppService = signCommandAppService;
        this.signQueryAppService = signQueryAppService;
    }

    @Override
    public Response<OperateSignResponseDTO> operateSign(OperateSignRequestDTO request) {
        long start = System.currentTimeMillis();
        try {
            // 1. 参数校验（快速失败）
            validateOperateSign(request);

            // 2. DTO转Command
            OperateSignCommand command = signAssembler.toCommand(request);

            // 3. 执行报名操作
            List<Long> signIdList = signCommandAppService.operateSign(command);

            // 4. 结果转换
            OperateSignResponseDTO response = signAssembler.toOperateSignResponse(signIdList);
            return Response.success(response);
        } catch (IllegalArgumentException e) {
            log.error("[SignFacadeImpl operateSign] 参数校验失败, request={}", request, e);
            return Response.fail(400001, e.getMessage());
        } catch (BizException e) {
            log.error("[SignFacadeImpl operateSign] 业务异常, request={}", request, e);
            return Response.fail(400001, e.getMessage());
        } catch (Exception e) {
            log.error("[SignFacadeImpl operateSign] 报名操作异常, request={}", request, e);
            return Response.fail(400001, "报名操作失败");
        } finally {
            log.info("[SignFacadeImpl operateSign] 耗时:{}ms", System.currentTimeMillis() - start);
        }
    }

    @Override
    public Response<QuerySignPageResponseDTO> querySignPage(QuerySignPageRequestDTO request) {
        long start = System.currentTimeMillis();
        try {
            // 1. 参数校验
            validateQuerySignPage(request);

            // 2. DTO转Qry
            SignPageQry qry = signAssembler.toQry(request);

            // 3. 查询
            PageResult<SignEntity> pageResult = signQueryAppService.querySignPage(qry);

            // 4. 结果转换
            QuerySignPageResponseDTO response = signAssembler.toQuerySignPageResponse(
                    pageResult.getTotal(), pageResult.getPageNo(),
                    pageResult.getPageSize(), pageResult.getRecords());
            return Response.success(response);
        } catch (IllegalArgumentException e) {
            log.error("[SignFacadeImpl querySignPage] 参数校验失败, request={}", request, e);
            return Response.fail(400002, e.getMessage());
        } catch (BizException e) {
            log.error("[SignFacadeImpl querySignPage] 业务异常, request={}", request, e);
            return Response.fail(400002, e.getMessage());
        } catch (Exception e) {
            log.error("[SignFacadeImpl querySignPage] 查询报名记录异常, request={}", request, e);
            return Response.fail(400002, "查询报名记录失败");
        } finally {
            log.info("[SignFacadeImpl querySignPage] 耗时:{}ms", System.currentTimeMillis() - start);
        }
    }

    @Override
    public Response<QuerySelectableProjectsResponseDTO> querySelectableProjects(
            QuerySelectableProjectsRequestDTO request) {
        long start = System.currentTimeMillis();
        try {
            // 1. 参数校验
            validateQuerySelectableProjects(request);

            // 2. DTO转Qry
            SelectableProjectQry qry = signAssembler.toQry(request);

            // 3. 查询
            List<SignProjectVO> projectList = signQueryAppService.querySelectableProjects(qry);

            // 4. 结果转换
            QuerySelectableProjectsResponseDTO response = signAssembler.toSelectableProjectResponse(projectList);
            return Response.success(response);
        } catch (IllegalArgumentException e) {
            log.error("[SignFacadeImpl querySelectableProjects] 参数校验失败, request={}", request, e);
            return Response.fail(400003, e.getMessage());
        } catch (BizException e) {
            log.error("[SignFacadeImpl querySelectableProjects] 业务异常, request={}", request, e);
            return Response.fail(400003, e.getMessage());
        } catch (Exception e) {
            log.error("[SignFacadeImpl querySelectableProjects] 查询可选提报项目异常, request={}", request, e);
            return Response.fail(400003, "报名配置暂不可用");
        } finally {
            log.info("[SignFacadeImpl querySelectableProjects] 耗时:{}ms", System.currentTimeMillis() - start);
        }
    }

    @Override
    public Response<QueryShopDoctorsResponseDTO> queryShopDoctors(QueryShopDoctorsRequestDTO request) {
        long start = System.currentTimeMillis();
        try {
            // 1. 参数校验
            validateQueryShopDoctors(request);

            // 2. DTO转Qry
            ShopDoctorQry qry = signAssembler.toQry(request);

            // 3. 查询并转换（Assembler负责DoctorAcl.DoctorDTO到API DoctorDTO的映射）
            List<DoctorDTO> apiDoctors = signQueryAppService.queryShopDoctors(qry, signAssembler);

            QueryShopDoctorsResponseDTO response = signAssembler.toShopDoctorResponse(apiDoctors);
            return Response.success(response);
        } catch (IllegalArgumentException e) {
            log.error("[SignFacadeImpl queryShopDoctors] 参数校验失败, request={}", request, e);
            return Response.fail(400004, e.getMessage());
        } catch (BizException e) {
            log.error("[SignFacadeImpl queryShopDoctors] 业务异常, request={}", request, e);
            return Response.fail(400004, e.getMessage());
        } catch (Exception e) {
            log.error("[SignFacadeImpl queryShopDoctors] 查询机构医生列表异常, request={}", request, e);
            return Response.fail(400004, "查询机构医生列表失败");
        } finally {
            log.info("[SignFacadeImpl queryShopDoctors] 耗时:{}ms", System.currentTimeMillis() - start);
        }
    }

    /**
     * 校验报名操作参数
     */
    private void validateOperateSign(OperateSignRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("请求参数不能为空");
        }
        if (request.getSignScene() == null) {
            throw new IllegalArgumentException("报名场景不能为空");
        }
        if (request.getOperationType() == null || request.getOperationType().trim().isEmpty()) {
            throw new IllegalArgumentException("操作类型不能为空");
        }
        if (request.getSubjectId() == null) {
            throw new IllegalArgumentException("报名主体ID不能为空");
        }
        if (request.getProjectCodeList() == null || request.getProjectCodeList().isEmpty()) {
            throw new IllegalArgumentException("提报项目code列表不能为空");
        }
        if (request.getShopId() == null) {
            throw new IllegalArgumentException("机构ID不能为空");
        }
        // 最多提报2个项目
        if (request.getProjectCodeList().size() > 2) {
            throw new IllegalArgumentException("一次最多操作2个提报项目");
        }
    }

    /**
     * 校验分页查询参数
     */
    private void validateQuerySignPage(QuerySignPageRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("请求参数不能为空");
        }
        if (request.getSignScene() == null) {
            throw new IllegalArgumentException("报名场景不能为空");
        }
        if (request.getShopId() == null) {
            throw new IllegalArgumentException("机构ID不能为空");
        }
        if (request.getPageNo() == null || request.getPageNo() < 1) {
            throw new IllegalArgumentException("页码不合法");
        }
        if (request.getPageSize() == null || request.getPageSize() < 1) {
            throw new IllegalArgumentException("每页大小不合法");
        }
    }

    /**
     * 校验可选项目查询参数
     */
    private void validateQuerySelectableProjects(QuerySelectableProjectsRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("请求参数不能为空");
        }
        if (request.getSignScene() == null) {
            throw new IllegalArgumentException("报名场景不能为空");
        }
    }

    /**
     * 校验机构医生列表查询参数
     */
    private void validateQueryShopDoctors(QueryShopDoctorsRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("请求参数不能为空");
        }
        if (request.getShopId() == null) {
            throw new IllegalArgumentException("机构ID不能为空");
        }
    }
}
