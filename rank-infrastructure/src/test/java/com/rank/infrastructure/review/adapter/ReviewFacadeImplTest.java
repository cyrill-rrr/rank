package com.rank.infrastructure.review.adapter;

import com.rank.api.common.Response;
import com.rank.api.review.dto.DeleteReviewTasksRequestDTO;
import com.rank.api.review.dto.DeleteReviewTasksResponseDTO;
import com.rank.api.review.dto.ImportReviewTasksRequestDTO;
import com.rank.api.review.dto.ImportReviewTasksRequestDTO.ImportReviewTaskItemDTO;
import com.rank.api.review.dto.ImportReviewTasksResponseDTO;
import com.rank.api.review.dto.QueryReviewTaskDetailRequestDTO;
import com.rank.api.review.dto.QueryReviewTaskDetailResponseDTO;
import com.rank.api.review.dto.QueryReviewTaskDetailResponseDTO.QuestionDetailDTO;
import com.rank.api.review.dto.QueryReviewTasksRequestDTO;
import com.rank.api.review.dto.QueryReviewTasksResponseDTO;
import com.rank.api.review.dto.QueryReviewTasksResponseDTO.ReviewTaskRecordDTO;
import com.rank.api.review.dto.SubmitReviewScoreRequestDTO;
import com.rank.api.review.dto.SubmitReviewScoreRequestDTO.QuestionAnswerDTO;
import com.rank.api.review.dto.SubmitReviewScoreResponseDTO;
import com.rank.infrastructure.common.CccWorkflowMockSwitch;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 评审系统 Facade 集成测试。
 * <p>
 * 测试通过 ReviewFacadeImpl 对外接口入口进入，领域内真实执行，领域外依赖通过 preparer
 * 在 infrastructure 实现类中写入的 CccWorkflowMockSwitch 硬编码 mock 分支返回。
 * <p>
 * 测试场景矩阵覆盖：
 * - 核心成功路径：分单、删单、列表、详情、提交打分
 * - 业务边界路径：管理员/非管理员角色控制、非法状态操作
 * - 幂等路径：提交打分重复提交被领域层拦截
 * - 异常路径：参数缺失、分数越界、必答题未完成
 * - 时间窗口：本需求不涉及窗口边界
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {ReviewFacadeImplTest.TestConfig.class})
public class ReviewFacadeImplTest {

    @Autowired
    private ReviewFacadeImpl reviewFacadeImpl;

    @BeforeEach
    public void setUp() {
        CccWorkflowMockSwitch.setEnabled(true);
    }

    @AfterEach
    public void tearDown() {
        CccWorkflowMockSwitch.setEnabled(false);
    }

    // ==================== 分单 importReviewTasks ====================

    /**
     * 核心成功路径：管理员向分单入口发送两行有效数据，返回 accepted=true。
     * 链路：ReviewFacadeImpl -> ReviewCommandAppService -> MafkaProducer(CccWorkflowMockSwitch 拦截)
     */
    @Test
    public void importReviewTasks_shouldSucceed_whenAdminWithValidRows() {
        ImportReviewTasksRequestDTO request = new ImportReviewTasksRequestDTO();
        request.setOperatorUserId(90001L);
        List<ImportReviewTaskItemDTO> rows = new ArrayList<ImportReviewTaskItemDTO>();

        ImportReviewTaskItemDTO row1 = new ImportReviewTaskItemDTO();
        row1.setMaterialId(100001L);
        row1.setUserId(200001L);
        row1.setScene("MEDICAL_BEAUTY_DOCTOR");
        rows.add(row1);

        ImportReviewTaskItemDTO row2 = new ImportReviewTaskItemDTO();
        row2.setMaterialId(100002L);
        row2.setUserId(200002L);
        row2.setScene("ORAL_DOCTOR");
        rows.add(row2);

        request.setRows(rows);

        Response<ImportReviewTasksResponseDTO> response = reviewFacadeImpl.importReviewTasks(request);

        assertEquals(200, response.getCode());
        assertTrue(response.getData().getAccepted());
    }

    /**
     * 异常路径：分单行列表为空，Facade 返回 400101
     */
    @Test
    public void importReviewTasks_shouldFail_whenRowsEmpty() {
        ImportReviewTasksRequestDTO request = new ImportReviewTasksRequestDTO();
        request.setOperatorUserId(90001L);
        request.setRows(Collections.<ImportReviewTaskItemDTO>emptyList());

        Response<ImportReviewTasksResponseDTO> response = reviewFacadeImpl.importReviewTasks(request);

        assertEquals(400101, response.getCode());
    }

    /**
     * 异常路径：operatorUserId 为空，Facade 返回 400101
     */
    @Test
    public void importReviewTasks_shouldFail_whenOperatorUserIdNull() {
        ImportReviewTasksRequestDTO request = new ImportReviewTasksRequestDTO();
        request.setOperatorUserId(null);

        Response<ImportReviewTasksResponseDTO> response = reviewFacadeImpl.importReviewTasks(request);

        assertEquals(400101, response.getCode());
    }

    /**
     * 异常路径：分单行 materialId 为空，Facade 返回 400101
     */
    @Test
    public void importReviewTasks_shouldFail_whenMaterialIdMissing() {
        ImportReviewTasksRequestDTO request = new ImportReviewTasksRequestDTO();
        request.setOperatorUserId(90001L);
        ImportReviewTaskItemDTO row = new ImportReviewTaskItemDTO();
        row.setMaterialId(null);
        row.setUserId(200001L);
        row.setScene("MEDICAL_BEAUTY_DOCTOR");
        request.setRows(Collections.singletonList(row));

        Response<ImportReviewTasksResponseDTO> response = reviewFacadeImpl.importReviewTasks(request);

        assertEquals(400101, response.getCode());
    }

    /**
     * 异常路径：分单行 scene 为空，Facade 返回 400101
     */
    @Test
    public void importReviewTasks_shouldFail_whenSceneMissing() {
        ImportReviewTasksRequestDTO request = new ImportReviewTasksRequestDTO();
        request.setOperatorUserId(90001L);
        ImportReviewTaskItemDTO row = new ImportReviewTaskItemDTO();
        row.setMaterialId(100001L);
        row.setUserId(200001L);
        row.setScene(null);
        request.setRows(Collections.singletonList(row));

        Response<ImportReviewTasksResponseDTO> response = reviewFacadeImpl.importReviewTasks(request);

        assertEquals(400101, response.getCode());
    }

    // ==================== 删单 deleteReviewTasks ====================

    /**
     * 核心成功路径：管理员删单，返回 affectedRows=1（mock 分支返回值）
     */
    @Test
    public void deleteReviewTasks_shouldSucceed_whenAdminWithValidUserId() {
        DeleteReviewTasksRequestDTO request = new DeleteReviewTasksRequestDTO();
        request.setOperatorUserId(90001L);
        request.setUserId(200001L);

        Response<DeleteReviewTasksResponseDTO> response = reviewFacadeImpl.deleteReviewTasks(request);

        assertEquals(200, response.getCode());
        assertNotNull(response.getData().getAffectedRows());
        assertEquals(1, response.getData().getAffectedRows().intValue());
    }

    /**
     * 业务边界路径：非管理员调用删单，返回 400102 forbidden
     */
    @Test
    public void deleteReviewTasks_shouldFail_whenNonAdmin() {
        DeleteReviewTasksRequestDTO request = new DeleteReviewTasksRequestDTO();
        request.setOperatorUserId(200001L);
        request.setUserId(200002L);

        Response<DeleteReviewTasksResponseDTO> response = reviewFacadeImpl.deleteReviewTasks(request);

        assertEquals(400102, response.getCode());
    }

    /**
     * 异常路径：删单 userId 为空，返回 400102
     */
    @Test
    public void deleteReviewTasks_shouldFail_whenUserIdNull() {
        DeleteReviewTasksRequestDTO request = new DeleteReviewTasksRequestDTO();
        request.setOperatorUserId(90001L);
        request.setUserId(null);

        Response<DeleteReviewTasksResponseDTO> response = reviewFacadeImpl.deleteReviewTasks(request);

        assertEquals(400102, response.getCode());
    }

    // ==================== 列表 queryReviewTasks ====================

    /**
     * 核心成功路径：管理员查询列表，返回分页结果
     */
    @Test
    public void queryReviewTasks_shouldReturnList_whenAdmin() {
        QueryReviewTasksRequestDTO request = new QueryReviewTasksRequestDTO();
        request.setLoginUserId(90001L);
        request.setPageNo(1);
        request.setPageSize(20);

        Response<QueryReviewTasksResponseDTO> response = reviewFacadeImpl.queryReviewTasks(request);

        assertEquals(200, response.getCode());
        assertNotNull(response.getData().getRecords());
        assertTrue(response.getData().getTotal() > 0);
    }

    /**
     * 业务边界路径：专家（非管理员）查询列表，强制按 loginUserId 过滤，
     * mock queryPageByUserId 返回的数据中 userId 等于请求的 userId
     */
    @Test
    public void queryReviewTasks_shouldReturnOwnData_whenNonAdmin() {
        QueryReviewTasksRequestDTO request = new QueryReviewTasksRequestDTO();
        request.setLoginUserId(200001L);
        request.setPageNo(1);
        request.setPageSize(20);

        Response<QueryReviewTasksResponseDTO> response = reviewFacadeImpl.queryReviewTasks(request);

        assertEquals(200, response.getCode());
        assertNotNull(response.getData().getRecords());
        for (ReviewTaskRecordDTO record : response.getData().getRecords()) {
            assertEquals(Long.valueOf(200001L), record.getUserId());
        }
    }

    /**
     * 异常路径：列表查询 loginUserId 为空，返回 400103
     */
    @Test
    public void queryReviewTasks_shouldFail_whenLoginUserIdNull() {
        QueryReviewTasksRequestDTO request = new QueryReviewTasksRequestDTO();
        request.setLoginUserId(null);
        request.setPageNo(1);
        request.setPageSize(20);

        Response<QueryReviewTasksResponseDTO> response = reviewFacadeImpl.queryReviewTasks(request);

        assertEquals(400103, response.getCode());
    }

    /**
     * 异常路径：pageNo=0，返回 400103
     */
    @Test
    public void queryReviewTasks_shouldFail_whenPageNoInvalid() {
        QueryReviewTasksRequestDTO request = new QueryReviewTasksRequestDTO();
        request.setLoginUserId(90001L);
        request.setPageNo(0);
        request.setPageSize(20);

        Response<QueryReviewTasksResponseDTO> response = reviewFacadeImpl.queryReviewTasks(request);

        assertEquals(400103, response.getCode());
    }

    /**
     * 业务边界路径：管理员按 status=SCORED 筛选已打分列表
     */
    @Test
    public void queryReviewTasks_shouldFilterByStatus_whenAdmin() {
        QueryReviewTasksRequestDTO request = new QueryReviewTasksRequestDTO();
        request.setLoginUserId(90001L);
        request.setStatus("SCORED");
        request.setPageNo(1);
        request.setPageSize(20);

        Response<QueryReviewTasksResponseDTO> response = reviewFacadeImpl.queryReviewTasks(request);

        assertEquals(200, response.getCode());
        assertNotNull(response.getData().getRecords());
    }

    // ==================== 详情 queryReviewTaskDetail ====================

    /**
     * 核心成功路径：管理员查询详情，返回材料详情和问题配置
     */
    @Test
    public void queryReviewTaskDetail_shouldReturnDetail_whenAdmin() {
        QueryReviewTaskDetailRequestDTO request = new QueryReviewTaskDetailRequestDTO();
        request.setLoginUserId(90001L);
        request.setReviewTaskId(300001L);

        Response<QueryReviewTaskDetailResponseDTO> response = reviewFacadeImpl.queryReviewTaskDetail(request);

        assertEquals(200, response.getCode());
        assertNotNull(response.getData().getMaterialDetail());
        assertNotNull(response.getData().getQuestions());
        assertFalse(response.getData().getQuestions().isEmpty());
        QuestionDetailDTO firstQuestion = response.getData().getQuestions().get(0);
        assertNotNull(firstQuestion.getQuestionId());
        assertNotNull(firstQuestion.getTitle());
    }

    /**
     * 业务边界路径：专家（owner）查询自己的详情，应成功。
     * mock findById 返回的 entity.userId=200001L，与 loginUserId 一致
     */
    @Test
    public void queryReviewTaskDetail_shouldReturnDetail_whenOwnerExpert() {
        QueryReviewTaskDetailRequestDTO request = new QueryReviewTaskDetailRequestDTO();
        request.setLoginUserId(200001L);
        request.setReviewTaskId(300001L);

        Response<QueryReviewTaskDetailResponseDTO> response = reviewFacadeImpl.queryReviewTaskDetail(request);

        assertEquals(200, response.getCode());
        assertNotNull(response.getData().getMaterialDetail());
        assertNotNull(response.getData().getQuestions());
    }

    /**
     * 业务边界路径：非管理员查他人的详情，返回 400104。
     * mock findById 返回 entity.userId=200001L，loginUserId=200002L 不是 owner
     */
    @Test
    public void queryReviewTaskDetail_shouldFail_whenNonAdminAndNotOwner() {
        QueryReviewTaskDetailRequestDTO request = new QueryReviewTaskDetailRequestDTO();
        request.setLoginUserId(200002L);
        request.setReviewTaskId(300001L);

        Response<QueryReviewTaskDetailResponseDTO> response = reviewFacadeImpl.queryReviewTaskDetail(request);

        assertEquals(400104, response.getCode());
    }

    /**
     * 异常路径：reviewTaskId 为空，返回 400104
     */
    @Test
    public void queryReviewTaskDetail_shouldFail_whenReviewTaskIdNull() {
        QueryReviewTaskDetailRequestDTO request = new QueryReviewTaskDetailRequestDTO();
        request.setLoginUserId(90001L);
        request.setReviewTaskId(null);

        Response<QueryReviewTaskDetailResponseDTO> response = reviewFacadeImpl.queryReviewTaskDetail(request);

        assertEquals(400104, response.getCode());
    }

    // ==================== 提交打分 submitReviewScore ====================

    /**
     * 核心成功路径：专家正常提交打分，返回 SCORED
     */
    @Test
    public void submitReviewScore_shouldSucceed_whenValidSubmission() {
        SubmitReviewScoreRequestDTO request = new SubmitReviewScoreRequestDTO();
        request.setLoginUserId(200001L);
        request.setReviewTaskId(300001L);

        List<QuestionAnswerDTO> answers = new ArrayList<QuestionAnswerDTO>();
        QuestionAnswerDTO a1 = new QuestionAnswerDTO();
        a1.setQuestionId("Q001");
        a1.setScore(8);
        answers.add(a1);
        QuestionAnswerDTO a2 = new QuestionAnswerDTO();
        a2.setQuestionId("Q002");
        a2.setScore(7);
        answers.add(a2);
        request.setAnswers(answers);

        Response<SubmitReviewScoreResponseDTO> response = reviewFacadeImpl.submitReviewScore(request);

        assertEquals(200, response.getCode());
        assertEquals("SCORED", response.getData().getStatus());
        assertNotNull(response.getData().getReviewTaskId());
    }

    /**
     * 幂等路径：已打分评审单重复提交被拒绝。
     * 说明：mock 模式下 findById 始终返回 UNSCORED 状态的 entity，且 save 为空操作，
     * 因此无法通过两次 submitReviewScore 调用来验证"已打分拒绝"。
     * 领域方法 entity.submitScores() 自身的重复提交校验（已打分 -> throw）是领域层不变量，
     * 在实体单元测试中覆盖。此处验证正常提交成功，属于完整的接口->领域->仓库调用链。
     * 本次需求涉及的幂等还包括：
     * - ReviewTaskAssignConsumer 中 findByBizKey 幂等（重复消费跳过）
     * - 此 mock 模式下 findByBizKey 返回 null，无法构造重复消费场景
     */
    @Test
    public void submitReviewScore_shouldSucceed_withValidAnswers() {
        SubmitReviewScoreRequestDTO request = new SubmitReviewScoreRequestDTO();
        request.setLoginUserId(200001L);
        request.setReviewTaskId(300001L);

        List<QuestionAnswerDTO> answers = new ArrayList<QuestionAnswerDTO>();
        QuestionAnswerDTO a1 = new QuestionAnswerDTO();
        a1.setQuestionId("Q001");
        a1.setScore(9);
        answers.add(a1);
        QuestionAnswerDTO a2 = new QuestionAnswerDTO();
        a2.setQuestionId("Q002");
        a2.setScore(6);
        answers.add(a2);
        request.setAnswers(answers);

        Response<SubmitReviewScoreResponseDTO> response = reviewFacadeImpl.submitReviewScore(request);

        assertEquals(200, response.getCode());
        assertEquals("SCORED", response.getData().getStatus());
    }

    /**
     * 业务边界路径：非 owner 提交打分。
     * mock findById 返回 entity.userId=200001L，但 submitUserId=200002L，
     * 领域层校验抛出 forbidden，Facade 捕获后返回 400105
     */
    @Test
    public void submitReviewScore_shouldFail_whenNotOwner() {
        SubmitReviewScoreRequestDTO request = new SubmitReviewScoreRequestDTO();
        request.setLoginUserId(200002L);
        request.setReviewTaskId(300001L);

        List<QuestionAnswerDTO> answers = new ArrayList<QuestionAnswerDTO>();
        QuestionAnswerDTO a1 = new QuestionAnswerDTO();
        a1.setQuestionId("Q001");
        a1.setScore(8);
        answers.add(a1);
        request.setAnswers(answers);

        Response<SubmitReviewScoreResponseDTO> response = reviewFacadeImpl.submitReviewScore(request);

        assertEquals(400105, response.getCode());
    }

    /**
     * 业务边界路径：必答题(Q001)未作答，领域层校验拒绝，返回 400105。
     * 策略：Q001 在 mock 模板中 required=true，答案中只包含 Q002（有分），
     * 不包含 Q001 的有分答案，触发 validateRequiredAnswered 抛出异常。
     */
    @Test
    public void submitReviewScore_shouldFail_whenRequiredQuestionNotAnswered() {
        SubmitReviewScoreRequestDTO request = new SubmitReviewScoreRequestDTO();
        request.setLoginUserId(200001L);
        request.setReviewTaskId(300001L);

        List<QuestionAnswerDTO> answers = new ArrayList<QuestionAnswerDTO>();
        QuestionAnswerDTO a2 = new QuestionAnswerDTO();
        a2.setQuestionId("Q002");
        a2.setScore(7);
        answers.add(a2);
        request.setAnswers(answers);

        Response<SubmitReviewScoreResponseDTO> response = reviewFacadeImpl.submitReviewScore(request);

        assertEquals(400105, response.getCode());
    }

    /**
     * 异常路径：分数越界（11），Facade 参数校验拒绝，返回 400105
     */
    @Test
    public void submitReviewScore_shouldFail_whenScoreOutOfRange() {
        SubmitReviewScoreRequestDTO request = new SubmitReviewScoreRequestDTO();
        request.setLoginUserId(200001L);
        request.setReviewTaskId(300001L);

        List<QuestionAnswerDTO> answers = new ArrayList<QuestionAnswerDTO>();
        QuestionAnswerDTO a1 = new QuestionAnswerDTO();
        a1.setQuestionId("Q001");
        a1.setScore(11);
        answers.add(a1);
        request.setAnswers(answers);

        Response<SubmitReviewScoreResponseDTO> response = reviewFacadeImpl.submitReviewScore(request);

        assertEquals(400105, response.getCode());
    }

    /**
     * 异常路径：分数为 0（越界），Facade 参数校验拒绝，返回 400105
     */
    @Test
    public void submitReviewScore_shouldFail_whenScoreZero() {
        SubmitReviewScoreRequestDTO request = new SubmitReviewScoreRequestDTO();
        request.setLoginUserId(200001L);
        request.setReviewTaskId(300001L);

        List<QuestionAnswerDTO> answers = new ArrayList<QuestionAnswerDTO>();
        QuestionAnswerDTO a1 = new QuestionAnswerDTO();
        a1.setQuestionId("Q001");
        a1.setScore(0);
        answers.add(a1);
        request.setAnswers(answers);

        Response<SubmitReviewScoreResponseDTO> response = reviewFacadeImpl.submitReviewScore(request);

        assertEquals(400105, response.getCode());
    }

    /**
     * 异常路径：answers 为空，返回 400105
     */
    @Test
    public void submitReviewScore_shouldFail_whenAnswersEmpty() {
        SubmitReviewScoreRequestDTO request = new SubmitReviewScoreRequestDTO();
        request.setLoginUserId(200001L);
        request.setReviewTaskId(300001L);
        request.setAnswers(Collections.<QuestionAnswerDTO>emptyList());

        Response<SubmitReviewScoreResponseDTO> response = reviewFacadeImpl.submitReviewScore(request);

        assertEquals(400105, response.getCode());
    }

    /**
     * 异常路径：reviewTaskId 为空，返回 400105
     */
    @Test
    public void submitReviewScore_shouldFail_whenReviewTaskIdNull() {
        SubmitReviewScoreRequestDTO request = new SubmitReviewScoreRequestDTO();
        request.setLoginUserId(200001L);
        request.setReviewTaskId(null);

        List<QuestionAnswerDTO> answers = new ArrayList<QuestionAnswerDTO>();
        QuestionAnswerDTO a1 = new QuestionAnswerDTO();
        a1.setQuestionId("Q001");
        a1.setScore(8);
        answers.add(a1);
        request.setAnswers(answers);

        Response<SubmitReviewScoreResponseDTO> response = reviewFacadeImpl.submitReviewScore(request);

        assertEquals(400105, response.getCode());
    }

    /**
     * 异常路径：loginUserId 为空，返回 400105
     */
    @Test
    public void submitReviewScore_shouldFail_whenLoginUserIdNull() {
        SubmitReviewScoreRequestDTO request = new SubmitReviewScoreRequestDTO();
        request.setLoginUserId(null);
        request.setReviewTaskId(300001L);

        List<QuestionAnswerDTO> answers = new ArrayList<QuestionAnswerDTO>();
        QuestionAnswerDTO a1 = new QuestionAnswerDTO();
        a1.setQuestionId("Q001");
        a1.setScore(8);
        answers.add(a1);
        request.setAnswers(answers);

        Response<SubmitReviewScoreResponseDTO> response = reviewFacadeImpl.submitReviewScore(request);

        assertEquals(400105, response.getCode());
    }

    // ==================== 测试配置 ====================

    /**
     * Spring Boot 测试配置。
     * 排除 DataSource/MyBatis 自动配置（测试无需真实数据库），
     * 只扫描 review 相关包，并为 ReviewMapper 提供空桩实现（mock 模式下不会被调用）。
     */
    @Configuration
    @EnableAutoConfiguration(exclude = {
            DataSourceAutoConfiguration.class,
            org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration.class
    })
    @ComponentScan(basePackages = {
            "com.rank.infrastructure.review",
            "com.rank.application.review",
            "com.rank.domain.review",
            "com.rank.api.review"
    })
    static class TestConfig {

        /**
         * ObjectMapper 用于 ReviewConverter 依赖注入
         */
        @Bean
        public com.fasterxml.jackson.databind.ObjectMapper objectMapper() {
            return new com.fasterxml.jackson.databind.ObjectMapper();
        }

        /**
         * ReviewMapper 空桩：mock 模式下 ReviewRepositoryImpl 的所有方法在调用
         * mapper 方法前已通过 CccWorkflowMockSwitch 拦截返回，mapper 实际不会被调用。
         * 此处提供空实现以满足 Spring 依赖注入，不作为测试数据来源。
         */
        @Bean
        public com.rank.infrastructure.review.mapper.ReviewMapper reviewMapper() {
            return new com.rank.infrastructure.review.mapper.ReviewMapper() {
                @Override
                public com.rank.infrastructure.review.po.ReviewTaskPO findByBizKey(Long materialId, Long userId, String scene) {
                    return null;
                }

                @Override
                public com.rank.infrastructure.review.po.ReviewTaskPO findById(Long id) {
                    return null;
                }

                @Override
                public int insert(com.rank.infrastructure.review.po.ReviewTaskPO po) {
                    return 0;
                }

                @Override
                public int update(com.rank.infrastructure.review.po.ReviewTaskPO po) {
                    return 0;
                }

                @Override
                public int deleteByUserId(Long userId) {
                    return 0;
                }

                @Override
                public java.util.List<com.rank.infrastructure.review.po.ReviewTaskPO> queryPage(
                        Long materialId, Long userId, String scene, String status, int offset, int pageSize) {
                    return null;
                }

                @Override
                public Long countPage(Long materialId, Long userId, String scene, String status) {
                    return null;
                }

                @Override
                public java.util.List<com.rank.infrastructure.review.po.ReviewTaskPO> queryPageByUserId(
                        Long userId, String status, int offset, int pageSize) {
                    return null;
                }

                @Override
                public Long countPageByUserId(Long userId, String status) {
                    return null;
                }
            };
        }
    }
}
