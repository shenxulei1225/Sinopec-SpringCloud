package cn.cheers.x.intelligentanalysis.service;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.intelligentanalysis.api.dto.AnalysisJobRespDTO;
import cn.cheers.x.intelligentanalysis.api.dto.AnalysisJobSubmitReqDTO;
import cn.cheers.x.intelligentanalysis.controller.admin.vo.AnalysisCapabilityRespVO;
import cn.cheers.x.intelligentanalysis.dal.dataobject.AnalysisCapabilityDO;
import cn.cheers.x.intelligentanalysis.dal.dataobject.AnalysisJobDO;
import cn.cheers.x.intelligentanalysis.dal.dataobject.AnalysisProviderDO;
import cn.cheers.x.intelligentanalysis.dal.mysql.AnalysisCapabilityMapper;
import cn.cheers.x.intelligentanalysis.dal.mysql.AnalysisJobMapper;
import cn.cheers.x.intelligentanalysis.dal.mysql.AnalysisProviderMapper;
import cn.cheers.x.intelligentanalysis.service.provider.AnalysisProviderAdapter;
import cn.cheers.x.intelligentanalysis.service.result.AnalysisJobResultSink;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class IntelligentAnalysisServiceImplTest {

    private AnalysisProviderMapper providerMapper;
    private AnalysisCapabilityMapper capabilityMapper;
    private AnalysisJobMapper jobMapper;
    private AnalysisProviderAdapter adapter;
    private AnalysisJobResultSink resultSink;
    private IntelligentAnalysisServiceImpl service;

    @BeforeEach
    void setUp() {
        TenantContextHolder.setTenantId(1L);
        providerMapper = mock(AnalysisProviderMapper.class);
        capabilityMapper = mock(AnalysisCapabilityMapper.class);
        jobMapper = mock(AnalysisJobMapper.class);
        adapter = mock(AnalysisProviderAdapter.class);
        resultSink = mock(AnalysisJobResultSink.class);
        when(adapter.supports("TEST_ADAPTER")).thenReturn(true);
        AnalysisCapabilityAvailabilityChecker availabilityChecker =
                new AnalysisCapabilityAvailabilityChecker(
                        capabilityMapper, providerMapper, List.of(adapter));
        service = new IntelligentAnalysisServiceImpl(
                providerMapper,
                capabilityMapper,
                jobMapper,
                List.of(adapter),
                List.of(resultSink),
                new ObjectMapper(),
                availabilityChecker);
    }

    @AfterEach
    void tearDown() {
        TenantContextHolder.clear();
    }

    @Test
    void listCapabilities_onlyAvailableRequiresCompleteEnabledProvider() {
        AnalysisProviderDO provider = provider(true);
        AnalysisCapabilityDO capability = capability(List.of("IMAGE"));
        when(providerMapper.selectAllOrdered()).thenReturn(List.of(provider));
        when(capabilityMapper.selectAllOrdered()).thenReturn(List.of(capability));
        when(adapter.configurationError(provider)).thenReturn("缺少回调地址");

        assertTrue(service.listCapabilities(true).isEmpty());

        when(adapter.configurationError(provider)).thenReturn(null);
        List<AnalysisCapabilityRespVO> available = service.listCapabilities(true);
        assertEquals(1, available.size());
        assertTrue(available.get(0).getAvailable());
    }

    @Test
    void submitJob_freezesCapabilityAndParameters() {
        AnalysisProviderDO provider = provider(true);
        AnalysisCapabilityDO capability = capability(List.of("IMAGE"));
        when(providerMapper.selectById(7L)).thenReturn(provider);
        when(capabilityMapper.selectById(41L)).thenReturn(capability);
        when(adapter.configurationError(provider)).thenReturn(null);
        when(adapter.submit(any(), any(), any()))
                .thenReturn(AnalysisProviderAdapter.ProviderSubmitResult.accepted("provider-job-1"));

        AnalysisJobSubmitReqDTO request = request("IMAGE");
        request.setParameters(Map.of("threshold", 0.8));
        AnalysisJobRespDTO result = service.submitJob(request);

        assertEquals("RUNNING", result.getStatus());
        assertEquals("LIQUID_LEAKAGE", result.getCapabilityCode());
        assertEquals("1.0", result.getCapabilityVersion());
        ArgumentCaptor<AnalysisJobDO> jobCaptor = ArgumentCaptor.forClass(AnalysisJobDO.class);
        verify(jobMapper).insert(jobCaptor.capture());
        assertEquals("{\"threshold\":0.8}", jobCaptor.getValue().getParameterSnapshot());
        assertEquals("TASK_STEP", jobCaptor.getValue().getBusinessRefType());
        assertEquals("9:analysis-step", jobCaptor.getValue().getBusinessRefId());
    }

    @Test
    void submitJob_completesAndDeliversSynchronousProviderResult() {
        AnalysisProviderDO provider = provider(true);
        AnalysisCapabilityDO capability = capability(List.of("IMAGE"));
        when(providerMapper.selectById(7L)).thenReturn(provider);
        when(capabilityMapper.selectById(41L)).thenReturn(capability);
        when(adapter.configurationError(provider)).thenReturn(null);
        when(resultSink.supports("TASK_STEP")).thenReturn(true);
        Map<String, Object> normalized = Map.of(
                "verdict", "NORMAL",
                "message", "本地联调：模拟仪表读数 42.0",
                "simulated", true);
        when(adapter.submit(any(), any(), any()))
                .thenReturn(AnalysisProviderAdapter.ProviderSubmitResult.completed(
                        "SIM-AJ-1", normalized));

        AnalysisJobRespDTO result = service.submitJob(request("IMAGE"));

        assertEquals("COMPLETED", result.getStatus());
        verify(resultSink).deliver(any(), eq(normalized));
    }

    @Test
    void submitJob_rejectsUnsupportedMedia() {
        AnalysisProviderDO provider = provider(true);
        when(providerMapper.selectById(7L)).thenReturn(provider);
        when(capabilityMapper.selectById(41L)).thenReturn(capability(List.of("IMAGE")));

        ServiceException error = assertThrows(ServiceException.class,
                () -> service.submitJob(request("VIDEO")));

        assertTrue(error.getMessage().contains("不支持"));
    }

    @Test
    void callback_normalizesAlarmAndDeliversByBusinessReference() {
        AnalysisJobDO job = new AnalysisJobDO();
        job.setJobNo("AJ-1");
        job.setBusinessRefType("TASK_STEP");
        job.setBusinessRefId("9:analysis-step");
        when(jobMapper.selectByJobNo("AJ-1")).thenReturn(job);
        when(resultSink.supports("TASK_STEP")).thenReturn(true);

        service.applyLegacyCallback("""
                {"code":0,"algorithmTempId":"AJ-1","info":{"boxs":[
                  {"parameter":"1","display":"检测到漏液","scores":"0.95","box":[1,2,3,4]}
                ]}}
                """);

        ArgumentCaptor<Map<String, Object>> resultCaptor = ArgumentCaptor.forClass(Map.class);
        verify(resultSink).deliver(any(), resultCaptor.capture());
        assertEquals("ALARM", resultCaptor.getValue().get("verdict"));
        assertEquals("COMPLETED", job.getStatus());
    }

    private static AnalysisProviderDO provider(boolean enabled) {
        AnalysisProviderDO row = new AnalysisProviderDO();
        row.setId(7L);
        row.setName("测试分析服务");
        row.setAdapterType("TEST_ADAPTER");
        row.setBaseUrl("https://analysis.example.com");
        row.setEnabled(enabled);
        return row;
    }

    private static AnalysisCapabilityDO capability(List<String> media) {
        AnalysisCapabilityDO row = new AnalysisCapabilityDO();
        row.setId(41L);
        row.setCode("LIQUID_LEAKAGE");
        row.setName("液体泄漏识别");
        row.setProviderId(7L);
        row.setProviderCapabilityCode("0_0_0_3005_0_0");
        row.setCapabilityVersion("1.0");
        row.setSupportedMedia(new ObjectMapper().valueToTree(media).toString());
        row.setParameterSchemaJson("{}");
        row.setResultSchemaJson("{}");
        row.setEnabled(true);
        return row;
    }

    private static AnalysisJobSubmitReqDTO request(String inputType) {
        AnalysisJobSubmitReqDTO request = new AnalysisJobSubmitReqDTO();
        request.setAnalysisCapabilityId(41L);
        request.setInputType(inputType);
        request.setInputRef("https://media.example.com/frame.jpg");
        request.setBusinessRefType("TASK_STEP");
        request.setBusinessRefId("9:analysis-step");
        return request;
    }
}
