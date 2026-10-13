package cn.cheers.x.intelligentanalysis.service;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.framework.tenant.core.util.TenantUtils;
import cn.cheers.x.intelligentanalysis.api.dto.AnalysisJobRespDTO;
import cn.cheers.x.intelligentanalysis.api.dto.AnalysisJobSubmitReqDTO;
import cn.cheers.x.intelligentanalysis.controller.admin.vo.AnalysisCapabilityRespVO;
import cn.cheers.x.intelligentanalysis.controller.admin.vo.AnalysisCapabilitySaveReqVO;
import cn.cheers.x.intelligentanalysis.controller.admin.vo.AnalysisProviderRespVO;
import cn.cheers.x.intelligentanalysis.controller.admin.vo.AnalysisProviderSaveReqVO;
import cn.cheers.x.intelligentanalysis.dal.dataobject.AnalysisCapabilityDO;
import cn.cheers.x.intelligentanalysis.dal.dataobject.AnalysisJobDO;
import cn.cheers.x.intelligentanalysis.dal.dataobject.AnalysisProviderDO;
import cn.cheers.x.intelligentanalysis.dal.mysql.AnalysisCapabilityMapper;
import cn.cheers.x.intelligentanalysis.dal.mysql.AnalysisJobMapper;
import cn.cheers.x.intelligentanalysis.dal.mysql.AnalysisProviderMapper;
import cn.cheers.x.intelligentanalysis.service.provider.AnalysisProviderAdapter;
import cn.cheers.x.intelligentanalysis.service.result.AnalysisJobResultSink;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 智能分析模块标准实现。
 *
 * <p>权威：能力目录决定调用哪个适配器，任务创建时冻结能力版本和参数，回调按 jobNo 更新任务。</p>
 * <p>不负责：设备协议、动作名称识别、巡检业务流程。缺服务配置时任务明确失败。</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IntelligentAnalysisServiceImpl implements IntelligentAnalysisService {

    private static final List<String> INPUT_TYPES = List.of("IMAGE", "VIDEO");

    private final AnalysisProviderMapper providerMapper;
    private final AnalysisCapabilityMapper capabilityMapper;
    private final AnalysisJobMapper jobMapper;
    private final List<AnalysisProviderAdapter> providerAdapters;
    private final List<AnalysisJobResultSink> resultSinks;
    private final ObjectMapper objectMapper;
    private final AnalysisCapabilityAvailabilityChecker capabilityAvailabilityChecker;

    @Override
    public List<AnalysisCapabilityRespVO> listCapabilities(boolean onlyAvailable) {
        Map<Long, AnalysisProviderDO> providers = providerMapper.selectAllOrdered().stream()
                .collect(Collectors.toMap(AnalysisProviderDO::getId, Function.identity()));
        return capabilityMapper.selectAllOrdered().stream()
                .map(row -> toCapabilityResp(row, providers.get(row.getProviderId())))
                .filter(row -> !onlyAvailable || Boolean.TRUE.equals(row.getAvailable()))
                .toList();
    }

    @Override
    public void assertCapabilityAvailable(Long capabilityId) {
        capabilityAvailabilityChecker.assertAvailable(capabilityId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveCapability(AnalysisCapabilitySaveReqVO request) {
        AnalysisProviderDO provider = requireProvider(request.getProviderId());
        String code = normalizeCode(request.getCode(), "能力编码");
        String version = requiredText(request.getCapabilityVersion(), "能力版本");
        List<String> media = normalizeMedia(request.getSupportedMedia());
        if (media.isEmpty()) {
            throw new ServiceException(400, "分析能力至少支持图片或录像一种输入");
        }
        AnalysisCapabilityDO row = request.getId() == null
                ? new AnalysisCapabilityDO()
                : requireCapability(request.getId());
        if (row.getId() == null) {
            row.setTenantId(currentTenantId());
        }
        row.setCode(code);
        row.setName(requiredText(request.getName(), "能力名称"));
        row.setDescription(trimToNull(request.getDescription()));
        row.setProviderId(provider.getId());
        row.setProviderCapabilityCode(requiredText(
                request.getProviderCapabilityCode(), "服务能力编号"));
        row.setCapabilityVersion(version);
        row.setSupportedMedia(writeJson(media));
        row.setParameterSchemaJson(writeJson(defaultMap(request.getParameterSchema())));
        row.setResultSchemaJson(writeJson(defaultMap(request.getResultSchema())));
        row.setEnabled(Boolean.TRUE.equals(request.getEnabled()));
        if (row.getId() == null) {
            capabilityMapper.insert(row);
        } else {
            capabilityMapper.updateById(row);
        }
        return row.getId();
    }

    @Override
    public List<AnalysisProviderRespVO> listProviders() {
        return providerMapper.selectAllOrdered().stream().map(this::toProviderResp).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveProvider(AnalysisProviderSaveReqVO request) {
        String adapterType = requiredText(request.getAdapterType(), "适配器种类");
        AnalysisProviderAdapter adapter = requireAdapter(adapterType);
        AnalysisProviderDO row = request.getId() == null
                ? new AnalysisProviderDO()
                : requireProvider(request.getId());
        if (row.getId() == null) {
            row.setTenantId(currentTenantId());
        }
        row.setCode(normalizeCode(request.getCode(), "接入编码"));
        row.setName(requiredText(request.getName(), "接入名称"));
        row.setAdapterType(adapterType);
        row.setBaseUrl(trimToNull(request.getBaseUrl()));
        row.setCredentialRef(trimToNull(request.getCredentialRef()));
        row.setConfigJson(writeJson(defaultMap(request.getConfig())));
        row.setEnabled(Boolean.TRUE.equals(request.getEnabled()));
        String configurationError = adapter.configurationError(row);
        if (Boolean.TRUE.equals(row.getEnabled()) && configurationError != null) {
            throw new ServiceException(400, configurationError);
        }
        if (row.getId() == null) {
            providerMapper.insert(row);
        } else {
            providerMapper.updateById(row);
        }
        return row.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AnalysisJobRespDTO submitJob(AnalysisJobSubmitReqDTO request) {
        AnalysisCapabilityDO capability = requireCapability(request.getAnalysisCapabilityId());
        AnalysisProviderDO provider = requireProvider(capability.getProviderId());
        if (!Boolean.TRUE.equals(capability.getEnabled())) {
            throw new ServiceException(400, "所选分析能力已停用");
        }
        if (!Boolean.TRUE.equals(provider.getEnabled())) {
            throw new ServiceException(400, "所选分析能力的服务接入尚未启用");
        }
        AnalysisProviderAdapter adapter = requireAdapter(provider.getAdapterType());
        String configurationError = adapter.configurationError(provider);
        if (configurationError != null) {
            throw new ServiceException(400, configurationError);
        }
        String inputType = requiredText(request.getInputType(), "媒体类型").toUpperCase(Locale.ROOT);
        if (!INPUT_TYPES.contains(inputType) || !readStringList(capability.getSupportedMedia()).contains(inputType)) {
            throw new ServiceException(400, "所选分析能力不支持这种媒体类型");
        }

        AnalysisJobDO job = new AnalysisJobDO();
        job.setTenantId(currentTenantId());
        job.setJobNo("AJ-" + UUID.randomUUID().toString().replace("-", ""));
        job.setCapabilityId(capability.getId());
        job.setCapabilityCodeSnapshot(capability.getCode());
        job.setCapabilityVersionSnapshot(capability.getCapabilityVersion());
        job.setProviderIdSnapshot(provider.getId());
        job.setInputType(inputType);
        job.setInputRef(requiredText(request.getInputRef(), "媒体引用"));
        job.setParameterSnapshot(writeJson(defaultMap(request.getParameters())));
        job.setBusinessRefType(trimToNull(request.getBusinessRefType()));
        job.setBusinessRefId(trimToNull(request.getBusinessRefId()));
        job.setStatus("SUBMITTING");
        jobMapper.insert(job);

        AnalysisProviderAdapter.ProviderSubmitResult submitted = adapter
                .submit(provider, capability, new AnalysisProviderAdapter.ProviderSubmitRequest(
                        job.getJobNo(),
                        inputType,
                        job.getInputRef(),
                        defaultMap(request.getParameters()),
                        job.getBusinessRefId()));
        if (!submitted.accepted()) {
            job.setStatus("FAILED");
            job.setFailureMessage(submitted.failureMessage());
            job.setCompletedTime(LocalDateTime.now());
        } else if (submitted.normalizedResult() != null) {
            completeSubmittedJob(job, submitted);
        } else {
            job.setStatus("RUNNING");
            job.setProviderJobId(submitted.providerJobId());
            job.setAcceptedTime(LocalDateTime.now());
        }
        jobMapper.updateById(job);
        if (submitted.accepted() && submitted.normalizedResult() != null) {
            deliverResult(job, submitted.normalizedResult());
        }
        return toJobResp(job);
    }

    /**
     * 同步服务在提交响应中已经给出最终结果时，直接冻结结果并完成分析任务。
     *
     * <p>异步服务仍保持 RUNNING 并等待回调。本方法不猜测业务动作，也不把模拟结果写进能力目录。</p>
     */
    private void completeSubmittedJob(
            AnalysisJobDO job, AnalysisProviderAdapter.ProviderSubmitResult submitted) {
        Map<String, Object> normalized = submitted.normalizedResult();
        boolean failed = "FAILED".equals(normalized.get("verdict"));
        job.setStatus(failed ? "FAILED" : "COMPLETED");
        job.setProviderJobId(submitted.providerJobId());
        job.setNormalizedResultJson(writeJson(normalized));
        job.setFailureMessage(failed
                ? String.valueOf(normalized.getOrDefault("message", "分析结果无效"))
                : null);
        job.setAcceptedTime(LocalDateTime.now());
        job.setCompletedTime(LocalDateTime.now());
    }

    @Override
    public AnalysisJobRespDTO getJob(String jobNo) {
        AnalysisJobDO job = jobMapper.selectByJobNo(requiredText(jobNo, "分析任务编号"));
        if (job == null) {
            throw new ServiceException(404, "没有这条分析任务");
        }
        return toJobResp(job);
    }

    @Override
    public List<AnalysisJobRespDTO> listRecentJobs(int limit) {
        return jobMapper.selectRecent(limit).stream().map(this::toJobResp).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyLegacyCallback(String body) {
        JsonNode root = readTree(body);
        String jobNo = root.path("algorithmTempId").asText("").trim();
        // 算法回调没有租户请求头。先不按租户过滤，只用任务编号找到原任务。
        AtomicReference<AnalysisJobDO> found = new AtomicReference<>();
        TenantUtils.executeIgnore(() -> found.set(jobMapper.selectByJobNo(jobNo)));
        AnalysisJobDO job = found.get();
        if (job == null) {
            throw new ServiceException(404, "回调对不上分析任务");
        }
        if (job.getTenantId() == null) {
            throw new ServiceException(400, "分析任务没有租户");
        }
        // 保存结果和写回任务步骤都用这条分析任务自己的租户。
        TenantUtils.execute(job.getTenantId(), () -> completeCallback(job, root));
    }

    private void completeCallback(AnalysisJobDO job, JsonNode root) {
        if ("COMPLETED".equals(job.getStatus())) {
            return;
        }
        Map<String, Object> normalized = normalizeLegacyResult(root);
        job.setRawResultJson(writeJson(objectMapper.convertValue(root, new TypeReference<Map<String, Object>>() {
        })));
        job.setNormalizedResultJson(writeJson(normalized));
        job.setStatus("FAILED".equals(normalized.get("verdict")) ? "FAILED" : "COMPLETED");
        job.setFailureMessage("FAILED".equals(normalized.get("verdict"))
                ? String.valueOf(normalized.getOrDefault("message", "分析结果无效"))
                : null);
        job.setCompletedTime(LocalDateTime.now());
        jobMapper.updateById(job);
        deliverResult(job, normalized);
    }

    private void deliverResult(AnalysisJobDO job, Map<String, Object> normalized) {
        if (!StringUtils.hasText(job.getBusinessRefType())) {
            return;
        }
        AnalysisJobResultSink sink = resultSinks.stream()
                .filter(candidate -> candidate.supports(job.getBusinessRefType()))
                .findFirst()
                .orElseThrow(() -> new ServiceException(400, "分析结果没有已登记的业务接收方"));
        try {
            sink.deliver(job, normalized);
        } catch (RuntimeException ex) {
            job.setStatus("DELIVERY_FAILED");
            job.setFailureMessage("分析完成，但业务结果交付失败");
            jobMapper.updateById(job);
            throw ex;
        }
    }

    private Map<String, Object> normalizeLegacyResult(JsonNode root) {
        Map<String, Object> result = new LinkedHashMap<>();
        JsonNode boxes = root.path("info").path("boxs");
        if (root.path("code").asInt(-1) != 0 || !boxes.isArray() || boxes.isEmpty()) {
            result.put("verdict", "FAILED");
            result.put("message", root.path("msg").asText("分析结果为空"));
            result.put("detections", List.of());
            return result;
        }
        JsonNode box = boxes.get(0);
        String parameter = box.path("parameter").asText("");
        result.put("verdict", "1".equals(parameter) ? "ALARM" : "0".equals(parameter) ? "NORMAL" : "FAILED");
        String display = box.path("display").asText("");
        result.put("message", display);
        result.put("display", display);
        result.put("confidence", decimalOrNull(box.path("scores").asText("")));
        List<Map<String, Object>> detections = new ArrayList<>();
        Map<String, Object> detection = new LinkedHashMap<>();
        detection.put("label", box.path("display_name").asText(""));
        detection.put("boundingBox", objectMapper.convertValue(box.path("box"), Object.class));
        detection.put("confidence", decimalOrNull(box.path("scores").asText("")));
        detections.add(detection);
        result.put("detections", detections);
        return result;
    }

    private AnalysisCapabilityRespVO toCapabilityResp(
            AnalysisCapabilityDO row, AnalysisProviderDO provider) {
        AnalysisCapabilityRespVO vo = new AnalysisCapabilityRespVO();
        vo.setId(row.getId());
        vo.setCode(row.getCode());
        vo.setName(row.getName());
        vo.setDescription(row.getDescription());
        vo.setProviderId(row.getProviderId());
        vo.setProviderName(provider == null ? null : provider.getName());
        vo.setProviderCapabilityCode(row.getProviderCapabilityCode());
        vo.setCapabilityVersion(row.getCapabilityVersion());
        vo.setSupportedMedia(readStringList(row.getSupportedMedia()));
        vo.setParameterSchema(readMap(row.getParameterSchemaJson()));
        vo.setResultSchema(readMap(row.getResultSchemaJson()));
        vo.setEnabled(Boolean.TRUE.equals(row.getEnabled()));
        vo.setAvailable(Boolean.TRUE.equals(row.getEnabled())
                && provider != null
                && Boolean.TRUE.equals(provider.getEnabled())
                && capabilityAvailabilityChecker.configurationError(provider) == null);
        return vo;
    }

    private AnalysisProviderRespVO toProviderResp(AnalysisProviderDO row) {
        AnalysisProviderRespVO vo = new AnalysisProviderRespVO();
        vo.setId(row.getId());
        vo.setCode(row.getCode());
        vo.setName(row.getName());
        vo.setAdapterType(row.getAdapterType());
        vo.setBaseUrl(row.getBaseUrl());
        vo.setCredentialRef(row.getCredentialRef());
        vo.setConfig(readMap(row.getConfigJson()));
        vo.setEnabled(Boolean.TRUE.equals(row.getEnabled()));
        return vo;
    }

    private AnalysisJobRespDTO toJobResp(AnalysisJobDO row) {
        AnalysisJobRespDTO dto = new AnalysisJobRespDTO();
        dto.setJobNo(row.getJobNo());
        dto.setAnalysisCapabilityId(row.getCapabilityId());
        dto.setCapabilityCode(row.getCapabilityCodeSnapshot());
        dto.setCapabilityVersion(row.getCapabilityVersionSnapshot());
        dto.setInputType(row.getInputType());
        dto.setStatus(row.getStatus());
        dto.setNormalizedResult(readMap(row.getNormalizedResultJson()));
        dto.setFailureMessage(row.getFailureMessage());
        dto.setCreateTime(row.getCreateTime());
        dto.setCompletedTime(row.getCompletedTime());
        return dto;
    }

    private AnalysisCapabilityDO requireCapability(Long id) {
        if (id == null) {
            throw new ServiceException(400, "必须选择分析能力");
        }
        AnalysisCapabilityDO row = capabilityMapper.selectById(id);
        if (row == null) {
            throw new ServiceException(404, "没有这项分析能力");
        }
        return row;
    }

    private AnalysisProviderDO requireProvider(Long id) {
        if (id == null) {
            throw new ServiceException(400, "必须选择服务接入");
        }
        AnalysisProviderDO row = providerMapper.selectById(id);
        if (row == null) {
            throw new ServiceException(404, "没有这项服务接入");
        }
        return row;
    }

    private AnalysisProviderAdapter requireAdapter(String adapterType) {
        return providerAdapters.stream().filter(adapter -> adapter.supports(adapterType))
                .findFirst()
                .orElseThrow(() -> new ServiceException(400, "没有对应的分析服务适配器"));
    }

    private JsonNode readTree(String body) {
        try {
            return objectMapper.readTree(body == null ? "" : body);
        } catch (Exception ex) {
            throw new ServiceException(400, "分析回调不是合法 JSON");
        }
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception ex) {
            throw new ServiceException(400, "分析配置无法保存为 JSON");
        }
    }

    private Map<String, Object> readMap(String json) {
        if (!StringUtils.hasText(json)) {
            return new LinkedHashMap<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (Exception ex) {
            throw new ServiceException(500, "分析配置 JSON 已损坏");
        }
    }

    private List<String> readStringList(String json) {
        if (!StringUtils.hasText(json)) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (Exception ex) {
            throw new ServiceException(500, "分析能力媒体类型 JSON 已损坏");
        }
    }

    private static List<String> normalizeMedia(List<String> input) {
        if (input == null) {
            return List.of();
        }
        return input.stream()
                .filter(Objects::nonNull)
                .map(value -> value.trim().toUpperCase(Locale.ROOT))
                .filter(INPUT_TYPES::contains)
                .distinct()
                .toList();
    }

    private static Map<String, Object> defaultMap(Map<String, Object> input) {
        return input == null ? Map.of() : new LinkedHashMap<>(input);
    }

    private static String normalizeCode(String raw, String label) {
        return requiredText(raw, label).toUpperCase(Locale.ROOT).replace('-', '_');
    }

    private static String requiredText(String raw, String label) {
        if (!StringUtils.hasText(raw)) {
            throw new ServiceException(400, label + "不能为空");
        }
        return raw.trim();
    }

    private static String trimToNull(String raw) {
        return StringUtils.hasText(raw) ? raw.trim() : null;
    }

    private static BigDecimal decimalOrNull(String raw) {
        if (!StringUtils.hasText(raw)) {
            return null;
        }
        try {
            return new BigDecimal(raw.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static Long currentTenantId() {
        return Objects.requireNonNullElse(TenantContextHolder.getRequiredTenantId(), 0L);
    }
}
