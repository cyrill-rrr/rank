package com.rank.application.sign.assembler;

import com.rank.api.sign.dto.DoctorDTO;
import com.rank.api.sign.dto.OperateSignRequestDTO;
import com.rank.api.sign.dto.OperateSignResponseDTO;
import com.rank.api.sign.dto.QuerySelectableProjectsRequestDTO;
import com.rank.api.sign.dto.QuerySelectableProjectsResponseDTO;
import com.rank.api.sign.dto.QuerySelectableProjectsResponseDTO.ProjectDTO;
import com.rank.api.sign.dto.QueryShopDoctorsRequestDTO;
import com.rank.api.sign.dto.QueryShopDoctorsResponseDTO;
import com.rank.api.sign.dto.QuerySignPageRequestDTO;
import com.rank.api.sign.dto.QuerySignPageResponseDTO;
import com.rank.api.sign.dto.QuerySignPageResponseDTO.SignRecordDTO;
import com.rank.application.sign.command.OperateSignCommand;
import com.rank.application.sign.qry.SelectableProjectQry;
import com.rank.application.sign.qry.ShopDoctorQry;
import com.rank.application.sign.qry.SignPageQry;
import com.rank.domain.sign.model.SignEntity;
import com.rank.domain.sign.shared.SignSceneEnum;
import com.rank.domain.sign.vo.SignProjectVO;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 报名Assembler：DTO <-> Command/Qry 以及 Result <-> ResponseDTO 的双向转换
 */
@Component
public class SignAssembler {

    /**
     * 报名操作请求DTO 转为 Command
     */
    public OperateSignCommand toCommand(OperateSignRequestDTO dto) {
        OperateSignCommand command = new OperateSignCommand();
        command.setSignScene(SignSceneEnum.fromCode(dto.getSignScene()).name());  // DTO的Integer转domain的String枚举名
        command.setOperationType(dto.getOperationType());                         // 操作类型：SUBMIT/CANCEL
        command.setSubjectId(dto.getSubjectId());                                 // 报名主体ID
        command.setProjectCodeList(dto.getProjectCodeList());                     // 提报项目code列表
        command.setShopId(dto.getShopId());                                       // 冗余机构ID
        return command;
    }

    /**
     * 报名操作结果 转为 ResponseDTO
     */
    public OperateSignResponseDTO toOperateSignResponse(List<Long> signIdList) {
        OperateSignResponseDTO dto = new OperateSignResponseDTO();
        dto.setSignIdList(signIdList);   // 本次操作影响的报名记录主键列表
        return dto;
    }

    /**
     * 分页查询请求DTO 转为 Qry
     */
    public SignPageQry toQry(QuerySignPageRequestDTO dto) {
        SignPageQry qry = new SignPageQry();
        qry.setSignScene(SignSceneEnum.fromCode(dto.getSignScene()).name());  // 报名场景
        qry.setIndexShopId(dto.getShopId());                                    // 机构ID映射到indexShopId
        qry.setStatus(dto.getStatus());                                         // 报名状态筛选
        qry.setPageNo(dto.getPageNo());                                         // 页码
        qry.setPageSize(dto.getPageSize());                                     // 每页大小
        return qry;
    }

    /**
     * 分页查询结果 转为 ResponseDTO
     */
    public QuerySignPageResponseDTO toQuerySignPageResponse(long total, int pageNo, int pageSize,
                                                            List<SignEntity> records) {
        QuerySignPageResponseDTO dto = new QuerySignPageResponseDTO();
        dto.setTotal(total);     // 总条数
        dto.setPageNo(pageNo);   // 当前页
        dto.setPageSize(pageSize);  // 每页大小
        if (records != null && !records.isEmpty()) {
            List<SignRecordDTO> recordDTOs = records.stream()
                    .map(this::toSignRecordDTO)
                    .collect(Collectors.toList());
            dto.setRecords(recordDTOs);   // 报名记录列表
        } else {
            dto.setRecords(Collections.emptyList());
        }
        return dto;
    }

    /**
     * SignEntity 转 SignRecordDTO
     */
    private SignRecordDTO toSignRecordDTO(SignEntity entity) {
        SignRecordDTO dto = new SignRecordDTO();
        dto.setSignId(entity.getId());                           // 报名记录ID
        dto.setSignScene(SignSceneEnum.valueOf(entity.getSignScene()).getCode());  // 报名场景
        dto.setSubjectId(entity.getSubjectId());                 // 报名主体ID
        dto.setProjectCode(entity.getProjectCode());             // 提报项目code
        dto.setStatus(convertStatusToInt(entity.getStatus().name()));  // 报名状态转Integer
        return dto;
    }

    /**
     * 可选提报项目请求DTO 转为 Qry
     */
    public SelectableProjectQry toQry(QuerySelectableProjectsRequestDTO dto) {
        SelectableProjectQry qry = new SelectableProjectQry();
        qry.setSignScene(SignSceneEnum.fromCode(dto.getSignScene()).name());  // 报名场景
        return qry;
    }

    /**
     * 可选项目列表 转为 ResponseDTO
     */
    public QuerySelectableProjectsResponseDTO toSelectableProjectResponse(List<SignProjectVO> projectList) {
        QuerySelectableProjectsResponseDTO dto = new QuerySelectableProjectsResponseDTO();
        if (projectList != null && !projectList.isEmpty()) {
            List<ProjectDTO> projectDTOs = projectList.stream()
                    .map(p -> {
                        ProjectDTO pd = new ProjectDTO();
                        pd.setProjectCode(p.getProjectCode());   // 提报项目code
                        return pd;
                    })
                    .collect(Collectors.toList());
            dto.setProjects(projectDTOs);   // 可选提报项目列表
        } else {
            dto.setProjects(Collections.emptyList());
        }
        return dto;
    }

    /**
     * 机构医生列表请求DTO 转为 Qry
     */
    public ShopDoctorQry toQry(QueryShopDoctorsRequestDTO dto) {
        ShopDoctorQry qry = new ShopDoctorQry();
        qry.setShopId(dto.getShopId());                       // 机构ID
        qry.setIncludeOffline(dto.getIncludeOffline());       // 是否包含不在线医生
        return qry;
    }

    /**
     * 医生列表 转为 ResponseDTO
     */
    public QueryShopDoctorsResponseDTO toShopDoctorResponse(List<DoctorDTO> doctors) {
        QueryShopDoctorsResponseDTO dto = new QueryShopDoctorsResponseDTO();
        dto.setDoctors(doctors);   // 医生列表
        return dto;
    }

    /**
     * 状态String转Integer
     */
    private int convertStatusToInt(String status) {
        if ("SIGNED".equals(status)) {
            return 1;
        }
        return 2;
    }
}
