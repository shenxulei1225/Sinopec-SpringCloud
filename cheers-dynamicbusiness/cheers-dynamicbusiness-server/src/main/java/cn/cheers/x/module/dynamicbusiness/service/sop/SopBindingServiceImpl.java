package cn.cheers.x.module.dynamicbusiness.service.sop;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.SopInstanceBindingRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.SopInstanceBindingUpsertReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.SopInstanceCreateFromTemplateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.SopMethodBindingRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.SopMethodBindingUpsertReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.sop.SopInstanceBindingDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.sop.SopMethodBindingDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.sop.SopInstanceBindingMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.sop.SopMethodBindingMapper;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import cn.cheers.x.module.dynamicbusiness.service.sop.dto.SopActionTreeNode;
import cn.cheers.x.module.dynamicbusiness.service.sop.dto.SopMergeResult;
import cn.cheers.x.module.dynamicbusiness.service.sop.dto.SopTemplateSnapshot;
import cn.cheers.x.module.dynamicbusiness.service.sop.dto.SopTreeOverride;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import jakarta.annotation.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * 通用 SOP 绑定实现。
 *
 * <p><b>权威</b>：V80 {@code dynamic_sop_method_binding} / {@code dynamic_sop_instance_binding}。</p>
 * <p><b>键全部入参</b>：subjectType / hostType / dimensionKey 由调用方传入，本类不写死业务类型码。</p>
 * <p><b>禁止</b>：多宿主共用同一实例；createInstance 复用已有实例行；读路径补绑定。</p>
 */
@Service
public class SopBindingServiceImpl implements SopBindingService {

    @Resource
    private SopMethodBindingMapper methodBindingMapper;

    @Resource
    private SopInstanceBindingMapper instanceBindingMapper;

    @Resource
    private EntityService entityService;

    @Resource
    private SopMergeService sopMergeService;

    @Override
    public List<SopMethodBindingRespVO> listMethods(String subjectType, long subjectId) {
        String type = requireTypeCode(subjectType, "subjectType");
        List<SopMethodBindingDO> rows = methodBindingMapper.selectBySubject(type, subjectId);
        List<SopMethodBindingRespVO> out = new ArrayList<>(rows.size());
        for (SopMethodBindingDO row : rows) {
            out.add(toMethodResp(row));
        }
        return out;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long upsertMethod(SopMethodBindingUpsertReqVO req) {
        String subjectType = requireTypeCode(req.getSubjectType(), "subjectType");
        String dimensionKey = requireTypeCode(req.getDimensionKey(), "dimensionKey");
        String dimensionValue = normalizeDimensionValue(req.getDimensionValue());
        requireSopTemplate(req.getSopTemplateId());

        SopMethodBindingDO existing = methodBindingMapper.selectByIdentity(
                subjectType, req.getSubjectId(), dimensionKey, dimensionValue);
        try {
            if (existing != null) {
                existing.setSopTemplateId(req.getSopTemplateId());
                methodBindingMapper.updateById(existing);
                return existing.getId();
            }
            SopMethodBindingDO created = SopMethodBindingDO.builder()
                    .subjectType(subjectType)
                    .subjectId(req.getSubjectId())
                    .dimensionKey(dimensionKey)
                    .dimensionValue(dimensionValue)
                    .sopTemplateId(req.getSopTemplateId())
                    .build();
            methodBindingMapper.insert(created);
            return created.getId();
        } catch (DataIntegrityViolationException ex) {
            throw new ServiceException(400, "方法选用唯一约束冲突（同对象同维度已存在）");
        }
    }

    @Override
    public SopInstanceBindingRespVO getInstanceBinding(
            String hostType,
            long hostId,
            String subjectType,
            long subjectId,
            String dimensionKey,
            String dimensionValue) {
        SopInstanceBindingDO row = instanceBindingMapper.selectByIdentity(
                requireTypeCode(hostType, "hostType"),
                hostId,
                requireTypeCode(subjectType, "subjectType"),
                subjectId,
                requireTypeCode(dimensionKey, "dimensionKey"),
                normalizeDimensionValue(dimensionValue));
        return row == null ? null : toInstanceResp(row);
    }

    @Override
    public List<SopInstanceBindingRespVO> listInstanceBindings(
            String hostType, long hostId, String subjectType, long subjectId) {
        List<SopInstanceBindingDO> rows = instanceBindingMapper.selectByHostAndSubject(
                requireTypeCode(hostType, "hostType"),
                hostId,
                requireTypeCode(subjectType, "subjectType"),
                subjectId);
        List<SopInstanceBindingRespVO> out = new ArrayList<>(rows.size());
        for (SopInstanceBindingDO row : rows) {
            out.add(toInstanceResp(row));
        }
        return out;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upsertInstanceBinding(SopInstanceBindingUpsertReqVO req) {
        String hostType = requireTypeCode(req.getHostType(), "hostType");
        String subjectType = requireTypeCode(req.getSubjectType(), "subjectType");
        String dimensionKey = requireTypeCode(req.getDimensionKey(), "dimensionKey");
        String dimensionValue = normalizeDimensionValue(req.getDimensionValue());
        requireSopInstance(req.getSopInstanceId());
        assertInstanceNotBoundToOtherHost(req.getSopInstanceId(), hostType, req.getHostId());

        SopInstanceBindingDO existing = instanceBindingMapper.selectByIdentity(
                hostType, req.getHostId(), subjectType, req.getSubjectId(), dimensionKey, dimensionValue);
        try {
            if (existing != null) {
                existing.setSopInstanceId(req.getSopInstanceId());
                instanceBindingMapper.updateById(existing);
                return;
            }
            SopInstanceBindingDO created = SopInstanceBindingDO.builder()
                    .hostType(hostType)
                    .hostId(req.getHostId())
                    .subjectType(subjectType)
                    .subjectId(req.getSubjectId())
                    .dimensionKey(dimensionKey)
                    .dimensionValue(dimensionValue)
                    .sopInstanceId(req.getSopInstanceId())
                    .build();
            instanceBindingMapper.insert(created);
        } catch (DataIntegrityViolationException ex) {
            throw new ServiceException(400, "实例绑定唯一约束冲突（同宿主同对象同维度已存在）");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long createInstanceFromTemplate(SopInstanceCreateFromTemplateReqVO req) {
        String hostType = requireTypeCode(req.getHostType(), "hostType");
        String subjectType = requireTypeCode(req.getSubjectType(), "subjectType");
        String dimensionKey = requireTypeCode(req.getDimensionKey(), "dimensionKey");
        String dimensionValue = normalizeDimensionValue(req.getDimensionValue());

        EntityRespVO template = requireSopTemplate(req.getSopTemplateId());
        Map<String, Object> templateBase = emptyIfNull(template.getBaseFields());

        SopTreeOverride treeOverride = parseTreeOverride(req.getTreeOverride());
        Map<String, Map<String, Object>> paramOverride = parseParamsByNode(req.getParamOverride());
        SopTemplateSnapshot snapshot = toTemplateSnapshot(templateBase);
        SopMergeResult merged = sopMergeService.merge(snapshot, treeOverride, paramOverride);
        if (!merged.isOk()) {
            throw new ServiceException(400, "无法创建实例：merge 缺口 " + merged.getGapCodes());
        }

        String name = StringUtils.hasText(req.getName())
                ? req.getName().trim()
                : template.getName() + " · 宿主" + req.getHostId();

        EntityCreateReqVO createReq = new EntityCreateReqVO();
        Map<String, Object> baseFields = new LinkedHashMap<>();
        baseFields.put("entityTypeCode", SopFieldCodes.ENTITY_TYPE_CODE);
        baseFields.put("modelId", template.getModelId());
        baseFields.put("name", name);
        baseFields.put("status", 1);
        baseFields.put(SopFieldCodes.IS_TEMPLATE, false);
        baseFields.put(SopFieldCodes.SOP_TEMPLATE_ID, req.getSopTemplateId());
        baseFields.put(SopFieldCodes.ACTION_TREE_OVERRIDE_JSON,
                treeOverride != null ? JSON.toJSONString(treeOverride) : null);
        baseFields.put(SopFieldCodes.PARAM_OVERRIDE_JSON,
                paramOverride != null && !paramOverride.isEmpty()
                        ? JSON.toJSONString(paramOverride) : null);
        baseFields.put(SopFieldCodes.ACTION_TREE_JSON, "[]");
        baseFields.put(SopFieldCodes.DEFAULT_PARAMS_BY_NODE_JSON, "{}");
        // SOP 实体仍有 execution_means 列：当维度键即该字段时写入维度值；其它维度不猜列
        if (SopFieldCodes.EXECUTION_MEANS.equals(dimensionKey)) {
            baseFields.put(SopFieldCodes.EXECUTION_MEANS, dimensionValue);
        }
        Object kind = templateBase.get(SopFieldCodes.PROCEDURE_KIND);
        if (kind != null) {
            baseFields.put(SopFieldCodes.PROCEDURE_KIND, kind);
        }
        baseFields.put(SopFieldCodes.VERSION_NO, 1);
        baseFields.put(SopFieldCodes.PUBLISH_STATUS, "DRAFT");
        createReq.setBaseFields(baseFields);

        Long instanceId = entityService.create(createReq);

        SopInstanceBindingUpsertReqVO upsert = new SopInstanceBindingUpsertReqVO();
        upsert.setHostType(hostType);
        upsert.setHostId(req.getHostId());
        upsert.setSubjectType(subjectType);
        upsert.setSubjectId(req.getSubjectId());
        upsert.setDimensionKey(dimensionKey);
        upsert.setDimensionValue(dimensionValue);
        upsert.setSopInstanceId(instanceId);
        upsertInstanceBinding(upsert);
        return instanceId;
    }

    private void assertInstanceNotBoundToOtherHost(Long sopInstanceId, String hostType, Long hostId) {
        List<SopInstanceBindingDO> rows = instanceBindingMapper.selectBySopInstanceId(sopInstanceId);
        for (SopInstanceBindingDO row : rows) {
            if (!Objects.equals(hostType, row.getHostType()) || !hostId.equals(row.getHostId())) {
                throw new ServiceException(400,
                        "SOP 实例已被其它宿主占用，禁止多宿主共用同一可写实例：" + sopInstanceId);
            }
        }
    }

    private void requireSopInstance(Long sopInstanceId) {
        EntityRespVO sop = entityService.get(sopInstanceId, SopFieldCodes.ENTITY_TYPE_CODE);
        if (sop == null || sop.getId() == null) {
            throw new ServiceException(404, "SOP 实例不存在：" + sopInstanceId);
        }
        Map<String, Object> base = emptyIfNull(sop.getBaseFields());
        if (readBool(base.get(SopFieldCodes.IS_TEMPLATE))) {
            throw new ServiceException(400, "绑定必须指向 SOP 实例（is_template=false），不能绑模板");
        }
    }

    private EntityRespVO requireSopTemplate(Long sopTemplateId) {
        EntityRespVO sop = entityService.get(sopTemplateId, SopFieldCodes.ENTITY_TYPE_CODE);
        if (sop == null || sop.getId() == null) {
            throw new ServiceException(404, "SOP 模板不存在：" + sopTemplateId);
        }
        Map<String, Object> base = emptyIfNull(sop.getBaseFields());
        if (!readBool(base.get(SopFieldCodes.IS_TEMPLATE))) {
            throw new ServiceException(400, "sopTemplateId 必须指向 SOP 模板行");
        }
        return sop;
    }

    private SopMethodBindingRespVO toMethodResp(SopMethodBindingDO row) {
        SopMethodBindingRespVO vo = new SopMethodBindingRespVO();
        vo.setId(row.getId());
        vo.setSubjectType(row.getSubjectType());
        vo.setSubjectId(row.getSubjectId());
        vo.setDimensionKey(row.getDimensionKey());
        vo.setDimensionValue(row.getDimensionValue());
        vo.setSopTemplateId(row.getSopTemplateId());
        if (row.getSopTemplateId() != null) {
            EntityRespVO sop = entityService.get(row.getSopTemplateId(), SopFieldCodes.ENTITY_TYPE_CODE);
            if (sop != null) {
                vo.setSopName(sop.getName());
                Map<String, Object> base = sop.getBaseFields();
                Object isTpl = base != null ? base.get(SopFieldCodes.IS_TEMPLATE) : null;
                vo.setSopIsTemplate(readBool(isTpl));
            }
        }
        return vo;
    }

    private SopInstanceBindingRespVO toInstanceResp(SopInstanceBindingDO row) {
        SopInstanceBindingRespVO vo = new SopInstanceBindingRespVO();
        vo.setId(row.getId());
        vo.setHostType(row.getHostType());
        vo.setHostId(row.getHostId());
        vo.setSubjectType(row.getSubjectType());
        vo.setSubjectId(row.getSubjectId());
        vo.setDimensionKey(row.getDimensionKey());
        vo.setDimensionValue(row.getDimensionValue());
        vo.setSopInstanceId(row.getSopInstanceId());
        return vo;
    }

    private SopTemplateSnapshot toTemplateSnapshot(Map<String, Object> base) {
        SopTemplateSnapshot snapshot = new SopTemplateSnapshot();
        Object treeRaw = parseJsonValue(base.get(SopFieldCodes.ACTION_TREE_JSON), List.of());
        snapshot.setActionTree(JSON.parseObject(JSON.toJSONString(treeRaw),
                new TypeReference<List<SopActionTreeNode>>() {
                }));
        snapshot.setParamsByNode(parseParamsByNode(base.get(SopFieldCodes.DEFAULT_PARAMS_BY_NODE_JSON)));
        return snapshot;
    }

    private SopTreeOverride parseTreeOverride(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof SopTreeOverride o) {
            return o;
        }
        if (raw instanceof String s && StringUtils.hasText(s)) {
            return JSON.parseObject(s, SopTreeOverride.class);
        }
        return JSON.parseObject(JSON.toJSONString(raw), SopTreeOverride.class);
    }

    private Map<String, Map<String, Object>> parseParamsByNode(Object raw) {
        if (raw == null) {
            return new LinkedHashMap<>();
        }
        if (raw instanceof Map<?, ?> map) {
            Map<String, Map<String, Object>> out = new LinkedHashMap<>();
            for (Map.Entry<?, ?> e : map.entrySet()) {
                if (e.getKey() == null || !(e.getValue() instanceof Map<?, ?> nested)) {
                    continue;
                }
                Map<String, Object> nodeParams = new LinkedHashMap<>();
                for (Map.Entry<?, ?> ne : nested.entrySet()) {
                    if (ne.getKey() != null) {
                        nodeParams.put(String.valueOf(ne.getKey()), ne.getValue());
                    }
                }
                out.put(String.valueOf(e.getKey()), nodeParams);
            }
            return out;
        }
        if (raw instanceof String s) {
            if (!StringUtils.hasText(s) || "null".equalsIgnoreCase(s.trim())) {
                return new LinkedHashMap<>();
            }
            Map<String, Map<String, Object>> parsed = JSON.parseObject(s,
                    new TypeReference<Map<String, Map<String, Object>>>() {
                    });
            return parsed != null ? parsed : new LinkedHashMap<>();
        }
        Map<String, Map<String, Object>> parsed = JSON.parseObject(JSON.toJSONString(raw),
                new TypeReference<Map<String, Map<String, Object>>>() {
                });
        return parsed != null ? parsed : new LinkedHashMap<>();
    }

    private Object parseJsonValue(Object raw, Object fallback) {
        if (raw == null) {
            return fallback;
        }
        if (raw instanceof String s) {
            if (!StringUtils.hasText(s)) {
                return fallback;
            }
            return JSON.parse(s);
        }
        return raw;
    }

    private Map<String, Object> emptyIfNull(Map<String, Object> map) {
        return map != null ? map : Map.of();
    }

    /** 类型码 / 维度键：去空白，禁止空串；不校验业务枚举。 */
    static String requireTypeCode(String raw, String fieldName) {
        if (!StringUtils.hasText(raw)) {
            throw new ServiceException(400, fieldName + " 不能为空");
        }
        return raw.trim();
    }

    /** 维度取值：去空白并统一大写（与常见手段枚举一致）；不限制取值集合。 */
    static String normalizeDimensionValue(String raw) {
        if (!StringUtils.hasText(raw)) {
            throw new ServiceException(400, "dimensionValue 不能为空");
        }
        return raw.trim().toUpperCase(Locale.ROOT);
    }

    private static boolean readBool(Object v) {
        if (v instanceof Boolean b) {
            return b;
        }
        if (v instanceof Number n) {
            return n.intValue() != 0;
        }
        if (v instanceof String s) {
            return "true".equalsIgnoreCase(s.trim()) || "1".equals(s.trim());
        }
        return false;
    }
}
