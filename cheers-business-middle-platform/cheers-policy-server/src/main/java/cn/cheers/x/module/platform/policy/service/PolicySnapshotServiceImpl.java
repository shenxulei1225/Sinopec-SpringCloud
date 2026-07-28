package cn.cheers.x.module.platform.policy.service;

import cn.cheers.x.module.platform.contract.dto.schedule.SchedulingSpecDTO;
import cn.cheers.x.module.platform.policy.api.dto.PolicyResolveForRunReqDTO;
import cn.cheers.x.module.platform.policy.api.dto.PolicyRunContextDTO;
import cn.cheers.x.module.platform.policy.api.dto.PolicySnapshotRespDTO;
import cn.cheers.x.module.platform.policy.dal.dataobject.PolicySetDO;
import cn.cheers.x.module.platform.policy.dal.dataobject.PolicySnapshotDO;
import cn.cheers.x.module.platform.policy.dal.dataobject.PolicyTemplateDO;
import cn.cheers.x.module.platform.policy.dal.mysql.PolicySnapshotMapper;
import cn.cheers.x.module.platform.policy.dal.mysql.PolicyTemplateMapper;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static cn.cheers.x.module.platform.policy.enums.ErrorCodeConstants.POLICY_SNAPSHOT_NOT_FOUND;
import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;

@Service
public class PolicySnapshotServiceImpl implements PolicySnapshotService {

    private static final String DEFAULT_PLATFORM_LAW_VERSION = "2.4.0-mvp";

    @Resource
    private PolicySetService policySetService;
    @Resource
    private PolicyTemplateMapper policyTemplateMapper;
    @Resource
    private PolicySnapshotMapper policySnapshotMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PolicyRunContextDTO resolveForRun(PolicyResolveForRunReqDTO request) {
        PolicySetDO policySet = policySetService.requirePublishedDo(request.getPolicySetId());
        PolicySnapshotDO snapshot = insertSnapshot(policySet);
        SchedulingSpecDTO schedulingSpec = PolicySpecHelper.extractSchedulingSpec(snapshot.getResolvedSpec());
        return PolicyRunContextDTO.builder()
                .policySnapshotId(snapshot.getId())
                .policySetId(snapshot.getPolicySetId())
                .schedulingSpec(schedulingSpec)
                .build();
    }

    @Override
    public PolicySnapshotRespDTO getSnapshot(String policySnapshotId) {
        PolicySnapshotDO snapshot = policySnapshotMapper.selectById(policySnapshotId);
        if (snapshot == null) {
            throw exception(POLICY_SNAPSHOT_NOT_FOUND);
        }
        return toResp(snapshot);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PolicySnapshotRespDTO createSnapshotForRun(PolicySetDO policySet) {
        return toResp(insertSnapshot(policySet));
    }

    private PolicySnapshotDO insertSnapshot(PolicySetDO policySet) {
        PolicyTemplateDO template = policyTemplateMapper.selectById(policySet.getTemplateId());
        String provenanceIndex = buildProvenanceIndex(policySet, template);
        PolicySnapshotDO snapshot = PolicySnapshotDO.builder()
                .id("psnap_" + UUID.randomUUID())
                .policySetId(policySet.getId())
                .policySetVersion(policySet.getVersion())
                .entityTypeCode(policySet.getEntityTypeCode())
                .platformLawVersion(DEFAULT_PLATFORM_LAW_VERSION)
                .resolvedSpec(policySet.getSpecParams())
                .provenanceIndex(provenanceIndex)
                .facilityId(policySet.getFacilityId())
                .build();
        policySnapshotMapper.insert(snapshot);
        return snapshot;
    }

    private static String buildProvenanceIndex(PolicySetDO policySet, PolicyTemplateDO template) {
        JSONArray index = new JSONArray();
        if (template != null) {
            JSONObject templateRef = new JSONObject();
            templateRef.put("refType", "policy_template");
            templateRef.put("refId", template.getId());
            templateRef.put("version", template.getVersion());
            templateRef.put("provenance", "template");
            index.add(templateRef);
        }
        JSONObject setRef = new JSONObject();
        setRef.put("refType", "policy_set");
        setRef.put("refId", policySet.getId());
        setRef.put("version", policySet.getVersion());
        setRef.put("provenance", "user");
        index.add(setRef);
        return index.toJSONString();
    }

    private static PolicySnapshotRespDTO toResp(PolicySnapshotDO snapshot) {
        return PolicySnapshotRespDTO.builder()
                .policySnapshotId(snapshot.getId())
                .policySetId(snapshot.getPolicySetId())
                .policySetVersion(snapshot.getPolicySetVersion())
                .entityTypeCode(snapshot.getEntityTypeCode())
                .schedulingSpec(PolicySpecHelper.extractSchedulingSpec(snapshot.getResolvedSpec()))
                .build();
    }
}
