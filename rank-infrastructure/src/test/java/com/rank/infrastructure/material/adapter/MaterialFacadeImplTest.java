package com.rank.infrastructure.material.adapter;

import com.rank.api.common.Response;
import com.rank.api.material.dto.OperateMaterialRequestDTO;
import com.rank.api.material.dto.OperateMaterialResponseDTO;
import com.rank.api.material.dto.QueryCurrentMaterialRequestDTO;
import com.rank.api.material.dto.QueryCurrentMaterialResponseDTO;
import com.rank.application.material.assembler.MaterialAssembler;
import com.rank.application.material.factory.MaterialFactory;
import com.rank.application.material.service.MaterialCommandAppService;
import com.rank.application.material.service.MaterialQueryAppService;
import com.rank.domain.common.CccWorkflowMockSwitch;
import com.rank.domain.material.repository.MaterialConfigRepository;
import com.rank.domain.material.repository.MaterialRepository;
import com.rank.domain.material.repository.UapAuditRepository;
import com.rank.domain.material.service.MaterialDomainService;
import com.rank.domain.material.service.MaterialSceneStrategy;
import com.rank.domain.material.service.MedicalBeautyDoctorMaterialStrategy;
import com.rank.domain.material.service.OralDoctorMaterialStrategy;
import com.rank.domain.material.shared.MaterialOperationEnum;
import com.rank.domain.material.shared.MaterialSceneEnum;
import com.rank.domain.sign.repository.SignRepository;
import com.rank.infrastructure.material.config.MaterialConfigRepositoryImpl;
import com.rank.infrastructure.material.converter.MaterialConverter;
import com.rank.infrastructure.material.mapper.MaterialMapper;
import com.rank.infrastructure.material.mq.UapAuditCallbackConsumer;
import com.rank.infrastructure.material.mq.UapAuditCallbackMafkaEvent;
import com.rank.infrastructure.material.po.MaterialPO;
import com.rank.infrastructure.material.repository.MaterialRepositoryImpl;
import com.rank.infrastructure.material.acl.UapAuditAclMockImpl;
import com.rank.infrastructure.sign.converter.SignConverter;
import com.rank.infrastructure.sign.mapper.SignMapper;
import com.rank.infrastructure.sign.po.SignPO;
import com.rank.infrastructure.sign.repository.SignRepositoryImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * MaterialFacadeImpl 集成测试。
 * - 通过 MaterialFacade 接口入口调用真实链路。
 * - 开启 CccWorkflowMockSwitch 使领域外方法走 preparer 写入的 mock 分支。
 * - 领域内（Facade/Assembler/AppService/DomainService/Entity/Strategy/VO）真实执行。
 */
public class MaterialFacadeImplTest {

    private MaterialFacadeImpl materialFacade;
    private UapAuditCallbackConsumer uapAuditCallbackConsumer;

    @Before
    public void setUp() {
        // ===== 构造基础设施层 =====

        // MaterialMapper: 纯MyBatis DAO, MockSwitch开启后不被调用, 提供无操作实现以满足构造函数
        MaterialMapper noopMaterialMapper = new MaterialMapper() {
            @Override
            public MaterialPO selectByBizKey(String materialScene, String auditSubjectId) {
                return null;
            }

            @Override
            public MaterialPO selectByUapUniqueId(String uapUniqueId) {
                return null;
            }

            @Override
            public int insert(MaterialPO po) {
                return 1;
            }

            @Override
            public int updateByBizKey(MaterialPO po) {
                return 1;
            }
        };

        // MaterialConverter: 纯字段映射, 无外部依赖
        MaterialConverter materialConverter = new MaterialConverter();

        // MaterialSceneStrategy 实现: 无外部依赖
        MedicalBeautyDoctorMaterialStrategy medicalStrategy = new MedicalBeautyDoctorMaterialStrategy();
        OralDoctorMaterialStrategy oralStrategy = new OralDoctorMaterialStrategy();
        List<MaterialSceneStrategy> strategies = Arrays.asList(medicalStrategy, oralStrategy);

        // MaterialRepositoryImpl: 真实实现, Mock分支由preparer写入
        MaterialRepository materialRepository = new MaterialRepositoryImpl(
                noopMaterialMapper, materialConverter, strategies);

        // MaterialConfigRepositoryImpl: 真实Mock实现
        MaterialConfigRepository materialConfigRepository = new MaterialConfigRepositoryImpl();

        // UapAuditAclMockImpl: 真实Mock实现
        UapAuditRepository uapAuditRepository = new UapAuditAclMockImpl();

        // ===== 构造报名域基础设施层 =====
        SignMapper noopSignMapper = new SignMapper() {
            @Override
            public List<SignPO> queryBySceneSubject(String signScene, Long subjectId) {
                return Collections.emptyList();
            }

            @Override
            public Integer countSignedDoctorsByIndexShopId(String signScene, Long indexShopId) {
                return 0;
            }

            @Override
            public int insert(SignPO po) {
                return 1;
            }

            @Override
            public int updateStatus(SignPO po) {
                return 1;
            }

            @Override
            public List<SignPO> queryShopSignPage(String signScene, Long indexShopId,
                                                   String status, int offset, int limit) {
                return Collections.emptyList();
            }

            @Override
            public Long countShopSignPage(String signScene, Long indexShopId, String status) {
                return 0L;
            }
        };
        SignConverter signConverter = new SignConverter();
        SignRepository signRepository = new SignRepositoryImpl(noopSignMapper, signConverter);

        // ===== 构造领域层 =====
        MaterialDomainService materialDomainService = new MaterialDomainService(signRepository);

        // ===== 构造应用层 =====
        MaterialFactory materialFactory = new MaterialFactory();
        MaterialCommandAppService commandAppService = new MaterialCommandAppService(
                materialConfigRepository, materialRepository, materialDomainService,
                materialFactory, strategies, uapAuditRepository);
        MaterialQueryAppService queryAppService = new MaterialQueryAppService(
                materialRepository, strategies);
        MaterialAssembler materialAssembler = new MaterialAssembler();

        // ===== 构造对外接口入口 =====
        materialFacade = new MaterialFacadeImpl(materialAssembler, commandAppService, queryAppService);

        // ===== 构造MQ Consumer入口（UAP审核回调） =====
        uapAuditCallbackConsumer = new UapAuditCallbackConsumer(commandAppService);

        // ===== 启用preparer的Mock开关 =====
        CccWorkflowMockSwitch.setEnabled(true);
    }

    @After
    public void tearDown() {
        CccWorkflowMockSwitch.setEnabled(false);
    }

    // ==================== 保存草稿成功路径 ====================

    @Test
    public void testSaveDraft_Success() {
        OperateMaterialRequestDTO request = new OperateMaterialRequestDTO();
        request.setMaterialScene(MaterialSceneEnum.MEDICAL_BEAUTY_DOCTOR.name());
        request.setAuditSubjectId("900001");
        request.setOperationType(MaterialOperationEnum.SAVE_DRAFT.name());
        request.setMaterialJsonStr("{\"cases\":[],\"qualifications\":[],\"introduction\":\"test\"}");

        Response<OperateMaterialResponseDTO> response = materialFacade.operateMaterial(request);

        assertEquals("返回码应为200", 200, response.getCode());
        assertNotNull("data不应为null", response.getData());
        assertNotNull("materialId不应为null", response.getData().getMaterialId());
    }

    @Test
    public void testSaveDraft_WithNullMaterialJson_ShouldSucceed() {
        OperateMaterialRequestDTO request = new OperateMaterialRequestDTO();
        request.setMaterialScene(MaterialSceneEnum.ORAL_DOCTOR.name());
        request.setAuditSubjectId("900002");
        request.setOperationType(MaterialOperationEnum.SAVE_DRAFT.name());
        request.setMaterialJsonStr(null);

        Response<OperateMaterialResponseDTO> response = materialFacade.operateMaterial(request);

        assertEquals("草稿空材料应返回200", 200, response.getCode());
        assertNotNull("data不应为null", response.getData());
        assertNotNull("materialId不应为null", response.getData().getMaterialId());
    }

    @Test
    public void testSaveDraft_WithEmptyJson_ShouldSucceed() {
        OperateMaterialRequestDTO request = new OperateMaterialRequestDTO();
        request.setMaterialScene(MaterialSceneEnum.MEDICAL_BEAUTY_DOCTOR.name());
        request.setAuditSubjectId("900003");
        request.setOperationType(MaterialOperationEnum.SAVE_DRAFT.name());
        request.setMaterialJsonStr("{}");

        Response<OperateMaterialResponseDTO> response = materialFacade.operateMaterial(request);

        assertEquals("草稿空JSON应返回200", 200, response.getCode());
        assertNotNull("data不应为null", response.getData());
        assertNotNull("materialId不应为null", response.getData().getMaterialId());
    }

    // ==================== 送审成功路径 ====================

    @Test
    public void testSubmitAudit_Success() {
        OperateMaterialRequestDTO request = new OperateMaterialRequestDTO();
        request.setMaterialScene(MaterialSceneEnum.MEDICAL_BEAUTY_DOCTOR.name());
        request.setAuditSubjectId("900001");
        request.setOperationType(MaterialOperationEnum.SUBMIT_AUDIT.name());
        request.setMaterialJsonStr("{\"cases\":[{\"title\":\"案例1\",\"description\":\"描述1\",\"attachments\":[{\"name\":\"附1\",\"url\":\"url1\",\"type\":\"jpg\"}]}],\"qualifications\":[{\"name\":\"资质1\",\"url\":\"url2\",\"type\":\"pdf\"}],\"introduction\":\"简介\"}");

        Response<OperateMaterialResponseDTO> response = materialFacade.operateMaterial(request);

        assertEquals("送审成功应返回200", 200, response.getCode());
        assertNotNull("data不应为null", response.getData());
        assertNotNull("materialId不应为null", response.getData().getMaterialId());
    }

    @Test
    public void testSubmitAudit_OralDoctor_Success() {
        OperateMaterialRequestDTO request = new OperateMaterialRequestDTO();
        request.setMaterialScene(MaterialSceneEnum.ORAL_DOCTOR.name());
        request.setAuditSubjectId("900001");
        request.setOperationType(MaterialOperationEnum.SUBMIT_AUDIT.name());
        request.setMaterialJsonStr("{\"cases\":[{\"title\":\"病例1\",\"description\":\"描述1\"}],\"licenseNumber\":\"123456\"}");

        Response<OperateMaterialResponseDTO> response = materialFacade.operateMaterial(request);

        assertEquals("口腔医生送审成功应返回200", 200, response.getCode());
        assertNotNull("data不应为null", response.getData());
        assertNotNull("materialId不应为null", response.getData().getMaterialId());
    }

    // ==================== 送审失败路径 ====================

    @Test
    public void testSubmitAudit_EmptyMaterialJson_ShouldFail() {
        OperateMaterialRequestDTO request = new OperateMaterialRequestDTO();
        request.setMaterialScene(MaterialSceneEnum.MEDICAL_BEAUTY_DOCTOR.name());
        request.setAuditSubjectId("900001");
        request.setOperationType(MaterialOperationEnum.SUBMIT_AUDIT.name());
        request.setMaterialJsonStr(null);

        Response<OperateMaterialResponseDTO> response = materialFacade.operateMaterial(request);

        assertEquals("送审空材料应返回400011", 400011, response.getCode());
        assertNull("data应为null", response.getData());
    }

    @Test
    public void testSubmitAudit_EmptyJsonObject_ShouldFail() {
        OperateMaterialRequestDTO request = new OperateMaterialRequestDTO();
        request.setMaterialScene(MaterialSceneEnum.MEDICAL_BEAUTY_DOCTOR.name());
        request.setAuditSubjectId("900001");
        request.setOperationType(MaterialOperationEnum.SUBMIT_AUDIT.name());
        request.setMaterialJsonStr("{}");

        Response<OperateMaterialResponseDTO> response = materialFacade.operateMaterial(request);

        assertEquals("送审空JSON应返回400011", 400011, response.getCode());
        assertNull("data应为null", response.getData());
    }

    @Test
    public void testSubmitAudit_InvalidMaterialScene_ShouldFail() {
        OperateMaterialRequestDTO request = new OperateMaterialRequestDTO();
        request.setMaterialScene("UNKNOWN_SCENE");
        request.setAuditSubjectId("900001");
        request.setOperationType(MaterialOperationEnum.SUBMIT_AUDIT.name());
        request.setMaterialJsonStr("{\"cases\":[{\"title\":\"t\"}]}");

        Response<OperateMaterialResponseDTO> response = materialFacade.operateMaterial(request);

        assertEquals("未知场景应返回400011", 400011, response.getCode());
        assertNull("data应为null", response.getData());
    }

    // ==================== 参数校验失败路径 ====================

    @Test
    public void testOperateMaterial_NullRequest_ShouldFail() {
        Response<OperateMaterialResponseDTO> response = materialFacade.operateMaterial(null);

        assertEquals("null请求应返回400011", 400011, response.getCode());
        assertNull("data应为null", response.getData());
    }

    @Test
    public void testOperateMaterial_MissingScene_ShouldFail() {
        OperateMaterialRequestDTO request = new OperateMaterialRequestDTO();
        request.setMaterialScene(null);
        request.setAuditSubjectId("900001");
        request.setOperationType(MaterialOperationEnum.SAVE_DRAFT.name());
        request.setMaterialJsonStr("{}");

        Response<OperateMaterialResponseDTO> response = materialFacade.operateMaterial(request);

        assertEquals("缺少scene应返回400011", 400011, response.getCode());
        assertNull("data应为null", response.getData());
    }

    @Test
    public void testOperateMaterial_MissingAuditSubjectId_ShouldFail() {
        OperateMaterialRequestDTO request = new OperateMaterialRequestDTO();
        request.setMaterialScene(MaterialSceneEnum.MEDICAL_BEAUTY_DOCTOR.name());
        request.setAuditSubjectId(null);
        request.setOperationType(MaterialOperationEnum.SAVE_DRAFT.name());
        request.setMaterialJsonStr("{}");

        Response<OperateMaterialResponseDTO> response = materialFacade.operateMaterial(request);

        assertEquals("缺少auditSubjectId应返回400011", 400011, response.getCode());
        assertNull("data应为null", response.getData());
    }

    @Test
    public void testOperateMaterial_MissingOperationType_ShouldFail() {
        OperateMaterialRequestDTO request = new OperateMaterialRequestDTO();
        request.setMaterialScene(MaterialSceneEnum.MEDICAL_BEAUTY_DOCTOR.name());
        request.setAuditSubjectId("900001");
        request.setOperationType(null);
        request.setMaterialJsonStr("{}");

        Response<OperateMaterialResponseDTO> response = materialFacade.operateMaterial(request);

        assertEquals("缺少operationType应返回400011", 400011, response.getCode());
        assertNull("data应为null", response.getData());
    }

    @Test
    public void testOperateMaterial_UnknownOperationType_ShouldFail() {
        OperateMaterialRequestDTO request = new OperateMaterialRequestDTO();
        request.setMaterialScene(MaterialSceneEnum.MEDICAL_BEAUTY_DOCTOR.name());
        request.setAuditSubjectId("900001");
        request.setOperationType("INVALID_OP");
        request.setMaterialJsonStr("{\"cases\":[{\"title\":\"t\"}]}");

        Response<OperateMaterialResponseDTO> response = materialFacade.operateMaterial(request);

        assertEquals("未知操作类型应返回400011", 400011, response.getCode());
        assertNull("data应为null", response.getData());
    }

    // ==================== 查询成功路径 ====================

    @Test
    public void testQueryCurrentMaterial_HasMaterial() {
        QueryCurrentMaterialRequestDTO request = new QueryCurrentMaterialRequestDTO();
        request.setMaterialScene(MaterialSceneEnum.MEDICAL_BEAUTY_DOCTOR.name());
        request.setAuditSubjectId("900001");

        Response<QueryCurrentMaterialResponseDTO> response = materialFacade.queryCurrentMaterial(request);

        assertEquals("查询应返回200", 200, response.getCode());
        assertNotNull("data不应为null", response.getData());
        // MockSwitch开启时 findByBizKey 返回硬编码实体(id=1L, NO_DRAFT, PENDING_SUBMIT)
        assertNotNull("materialId不应为null", response.getData().getMaterialId());
        assertEquals("hasDraft应为NO_DRAFT", "NO_DRAFT", response.getData().getHasDraft());
        assertEquals("auditStatus应为PENDING_SUBMIT", "PENDING_SUBMIT", response.getData().getAuditStatus());
    }

    // ==================== 查询失败路径 ====================

    @Test
    public void testQueryCurrentMaterial_NullRequest_ShouldFail() {
        Response<QueryCurrentMaterialResponseDTO> response = materialFacade.queryCurrentMaterial(null);

        assertEquals("null请求应返回400012", 400012, response.getCode());
        assertNull("data应为null", response.getData());
    }

    @Test
    public void testQueryCurrentMaterial_MissingScene_ShouldFail() {
        QueryCurrentMaterialRequestDTO request = new QueryCurrentMaterialRequestDTO();
        request.setMaterialScene(null);
        request.setAuditSubjectId("900001");

        Response<QueryCurrentMaterialResponseDTO> response = materialFacade.queryCurrentMaterial(request);

        assertEquals("缺少scene应返回400012", 400012, response.getCode());
        assertNull("data应为null", response.getData());
    }

    @Test
    public void testQueryCurrentMaterial_MissingAuditSubjectId_ShouldFail() {
        QueryCurrentMaterialRequestDTO request = new QueryCurrentMaterialRequestDTO();
        request.setMaterialScene(MaterialSceneEnum.MEDICAL_BEAUTY_DOCTOR.name());
        request.setAuditSubjectId(null);

        Response<QueryCurrentMaterialResponseDTO> response = materialFacade.queryCurrentMaterial(request);

        assertEquals("缺少auditSubjectId应返回400012", 400012, response.getCode());
        assertNull("data应为null", response.getData());
    }

    // ==================== 送审失败路径：auditSubjectId非数字 ====================

    @Test
    public void testSubmitAudit_InvalidAuditSubjectId_ShouldFail() {
        OperateMaterialRequestDTO request = new OperateMaterialRequestDTO();
        request.setMaterialScene(MaterialSceneEnum.MEDICAL_BEAUTY_DOCTOR.name());
        request.setAuditSubjectId("not_a_number");
        request.setOperationType(MaterialOperationEnum.SUBMIT_AUDIT.name());
        request.setMaterialJsonStr("{\"cases\":[{\"title\":\"案例1\",\"description\":\"描述1\"}],\"qualifications\":[{\"name\":\"资质1\"}],\"introduction\":\"简介\"}");

        Response<OperateMaterialResponseDTO> response = materialFacade.operateMaterial(request);

        assertEquals("非数字auditSubjectId应返回400011", 400011, response.getCode());
        assertNull("data应为null", response.getData());
    }

    // ==================== 送审路径：非法JSON字符串 ====================

    @Test
    public void testSubmitAudit_InvalidJsonString_ShouldFail() {
        OperateMaterialRequestDTO request = new OperateMaterialRequestDTO();
        request.setMaterialScene(MaterialSceneEnum.MEDICAL_BEAUTY_DOCTOR.name());
        request.setAuditSubjectId("900001");
        request.setOperationType(MaterialOperationEnum.SUBMIT_AUDIT.name());
        request.setMaterialJsonStr("{{{not json}}}");

        Response<OperateMaterialResponseDTO> response = materialFacade.operateMaterial(request);

        assertEquals("非法JSON应返回400011", 400011, response.getCode());
        assertNull("data应为null", response.getData());
    }

    // ==================== UAP审核回调成功路径（通过UapAuditCallbackConsumer入口） ====================

    @Test
    public void testCallback_Approved_ShouldSucceed() {
        UapAuditCallbackMafkaEvent event = new UapAuditCallbackMafkaEvent();
        event.setUapUniqueId("mock-uap-unique-id-001");
        event.setAuditStatus("APPROVED");
        event.setRejectReason(null);
        event.setPassageJson("{\"materialScene\":\"MEDICAL_BEAUTY_DOCTOR\",\"auditSubjectId\":\"12345\"}");

        // Consumer入口：模拟MQ回调，应不抛异常
        uapAuditCallbackConsumer.handleAuditCallback(event);
        // 验证通过（handleAuditCallback为void，无返回值；不抛异常即通过）
        assertTrue("APPROVED回调不应抛异常", true);
    }

    @Test
    public void testCallback_Rejected_ShouldSucceed() {
        UapAuditCallbackMafkaEvent event = new UapAuditCallbackMafkaEvent();
        event.setUapUniqueId("mock-uap-unique-id-002");
        event.setAuditStatus("REJECTED");
        event.setRejectReason("材料信息不完整");
        event.setPassageJson("{\"materialScene\":\"MEDICAL_BEAUTY_DOCTOR\",\"auditSubjectId\":\"12345\"}");

        uapAuditCallbackConsumer.handleAuditCallback(event);
        assertTrue("REJECTED回调不应抛异常", true);
    }

    @Test
    public void testCallback_UnknownAuditStatus_ShouldBeIgnored() {
        UapAuditCallbackMafkaEvent event = new UapAuditCallbackMafkaEvent();
        event.setUapUniqueId("mock-uap-unique-id-003");
        event.setAuditStatus("UNKNOWN_STATUS");
        event.setRejectReason(null);
        event.setPassageJson(null);

        // 未知状态应被幂等忽略，不抛异常
        uapAuditCallbackConsumer.handleAuditCallback(event);
        assertTrue("未知审核状态回调不应抛异常", true);
    }

    @Test
    public void testCallback_WithPassageJsonMismatch_ShouldNotThrow() {
        UapAuditCallbackMafkaEvent event = new UapAuditCallbackMafkaEvent();
        event.setUapUniqueId("mock-uap-unique-id-004");
        event.setAuditStatus("APPROVED");
        event.setRejectReason(null);
        // passageJson中materialScene与实际记录不匹配（mock实体materialScene=MEDICAL_BEAUTY_DOCTOR）
        event.setPassageJson("{\"materialScene\":\"ORAL_DOCTOR\",\"auditSubjectId\":\"99999\"}");

        // 透传字段不匹配仅记录WARN日志，不阻断处理
        uapAuditCallbackConsumer.handleAuditCallback(event);
        assertTrue("透传字段不匹配回调不应抛异常", true);
    }
}
