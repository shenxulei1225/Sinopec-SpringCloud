package cn.cheers.x.module.dynamicbusiness.service.entity;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.IncomingRecordRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityRelationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.framework.facility.FacilityOwningFieldCodes;
import cn.cheers.x.module.dynamicbusiness.service.entity.refroute.EntityRefLookupQueryService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 详情页签打开时，列出指向当前这条记录的对方。
 *
 * 管什么：找出引用了当前记录的对方（单值专用列查列，其余查实体关联表），并按型号名、所属场站收窄。
 * 场站指向管线时，带上这一笔关联上的里程或顺序，以及场站自己的经纬度。
 * 不管什么：把对方编号写回当前这条；详情栏勾了哪些字段；编一条管线走向。
 * 禁止：没有引用时编一份名单。
 */
@Service
public class IncomingRecordQueryService {

    private static final int MAX_ROWS = 200;
    private static final int CANDIDATE_LIMIT = 2000;
    /** 场站「所属管线段」指向当前管线的那一笔。里程和顺序写在这一笔上，不写在管线上。 */
    private static final String PIPELINE_SEGMENT_FIELD = "pipeline_segment_ids";

    @Resource
    private EntityRefLookupQueryService entityRefLookupQueryService;
    @Resource
    private ModelMapper modelMapper;
    @Resource
    private EntityService entityService;
    @Resource
    private EntityRelationMapper entityRelationMapper;

    public List<IncomingRecordRespVO> list(String registryCode,
                                            Long hostEntityId,
                                            String hostEntityTypeCode,
                                            String modelName,
                                            Long facilityId) {
        if (!StringUtils.hasText(registryCode)) {
            throw new ServiceException(400, "请选择要打开的目录");
        }
        if (hostEntityId == null || hostEntityId <= 0) {
            throw new ServiceException(400, "请先点中一条记录");
        }
        if (!StringUtils.hasText(hostEntityTypeCode)) {
            throw new ServiceException(400, "请说明当前记录属于哪本目录");
        }
        String sourceType = registryCode.trim();
        String hostType = hostEntityTypeCode.trim();
        String wantedModel = modelName == null ? "" : modelName.trim();
        Map<Long, String> segmentAttrs = "pipeline".equals(hostType)
                ? segmentAttrsBySource(hostEntityId)
                : Map.of();
        List<IncomingRecordRespVO> out = new ArrayList<>();
        // 候选多取一些：后面还要按型号名、所属场站收窄
        for (Long sourceId : entityRefLookupQueryService.listReferencingSubjectIds(
                sourceType, hostType, hostEntityId, CANDIDATE_LIMIT)) {
            EntityRespVO entity = loadEntity(sourceId, sourceType);
            if (entity == null) {
                continue;
            }
            String rowModelName = modelNameOf(entity.getModelId());
            if (StringUtils.hasText(wantedModel) && !wantedModel.equals(rowModelName)) {
                continue;
            }
            if (facilityId != null && facilityId > 0 && !facilityMatches(entity, facilityId)) {
                continue;
            }
            IncomingRecordRespVO item = new IncomingRecordRespVO();
            item.setId(sourceId);
            item.setName(entity.getName());
            item.setModelName(rowModelName);
            fillStopPosition(item, entity.getBaseFields(), segmentAttrs.get(sourceId));
            out.add(item);
            if (out.size() >= MAX_ROWS) {
                break;
            }
        }
        return out;
    }

    private EntityRespVO loadEntity(Long id, String entityTypeCode) {
        try {
            return entityService.get(id, entityTypeCode);
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private String modelNameOf(Long modelId) {
        if (modelId == null) {
            return "";
        }
        ModelDO model = modelMapper.selectById(modelId);
        if (model == null || !StringUtils.hasText(model.getName())) {
            return "";
        }
        return model.getName().trim();
    }

    private static boolean facilityMatches(EntityRespVO entity, Long facilityId) {
        Map<String, Object> base = entity.getBaseFields();
        if (base == null) {
            return false;
        }
        Long owning = FacilityOwningFieldCodes.extractId(base.get(FacilityOwningFieldCodes.FIELD_CODE));
        return facilityId.equals(owning);
    }

    /**
     * 当前这条被哪些场站的「所属管线段」指着。
     * 同一座站在不同管线上的里程、顺序各记一笔，这里只取指向当前这条的那一笔。
     */
    private Map<Long, String> segmentAttrsBySource(Long hostEntityId) {
        Map<Long, String> attrs = new HashMap<>();
        for (EntityRelationDO row : entityRelationMapper.selectByTargetEntityIdAndFieldCode(
                hostEntityId, PIPELINE_SEGMENT_FIELD)) {
            if (row == null || row.getSourceEntityId() == null) {
                continue;
            }
            attrs.put(row.getSourceEntityId(), row.getRelationAttributes());
        }
        return attrs;
    }

    /**
     * 先用这一笔关联上的里程、顺序。没有再读场站自己的里程、顺序。
     * 经纬度、地址只读场站自己的。缺了就空着，不编一个位置。
     */
    private static void fillStopPosition(IncomingRecordRespVO item, Map<String, Object> base, String relationAttributes) {
        Double relMileage = attrNumber(relationAttributes, "lineMileage");
        Double relSeq = attrNumber(relationAttributes, "segmentSeq");
        item.setLineMileage(relMileage != null ? relMileage : number(base, "line_mileage"));
        item.setSegmentSeq(relSeq != null ? relSeq : number(base, "segment_seq"));
        item.setLongitude(number(base, "longitude"));
        item.setLatitude(number(base, "latitude"));
        item.setAddress(text(base, "address"));
    }

    private static String text(Map<String, Object> base, String key) {
        if (base == null || key == null || base.get(key) == null) {
            return null;
        }
        String value = String.valueOf(base.get(key)).trim();
        return value.isEmpty() ? null : value;
    }

    private static Double number(Map<String, Object> base, String key) {
        if (base == null || key == null) {
            return null;
        }
        return toDouble(base.get(key));
    }

    /** 关联上只认 {"lineMileage":数字,"segmentSeq":数字}。解析失败当没有。 */
    private static Double attrNumber(String json, String key) {
        if (!StringUtils.hasText(json) || !StringUtils.hasText(key)) {
            return null;
        }
        String token = "\"" + key + "\"";
        int at = json.indexOf(token);
        if (at < 0) {
            return null;
        }
        int colon = json.indexOf(':', at + token.length());
        if (colon < 0) {
            return null;
        }
        return toDouble(json.substring(colon + 1).trim());
    }

    private static Double toDouble(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof Number number) {
            double value = number.doubleValue();
            return Double.isFinite(value) ? value : null;
        }
        String text = String.valueOf(raw).trim();
        if (text.isEmpty()) {
            return null;
        }
        int end = 0;
        while (end < text.length()) {
            char ch = text.charAt(end);
            if ((ch >= '0' && ch <= '9') || ch == '.' || ch == '-' || ch == '+') {
                end++;
                continue;
            }
            break;
        }
        if (end == 0) {
            return null;
        }
        try {
            double value = Double.parseDouble(text.substring(0, end));
            return Double.isFinite(value) ? value : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
