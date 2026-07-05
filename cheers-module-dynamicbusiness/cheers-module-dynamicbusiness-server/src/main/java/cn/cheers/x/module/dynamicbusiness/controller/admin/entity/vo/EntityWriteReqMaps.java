package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import cn.cheers.x.module.dynamicbusiness.convert.entity.EntityFieldMapsSupport;

import java.util.LinkedHashMap;
import java.util.Map;

/** 构造 Write Req 的 baseFields / Create / Update 请求。 */
public final class EntityWriteReqMaps {

    private EntityWriteReqMaps() {
    }

    public static Map<String, Object> baseOf(
            String businessTypeCode,
            Long modelId,
            String name,
            Integer status,
            Long parentId) {
        Map<String, Object> base = new LinkedHashMap<>();
        if (businessTypeCode != null) {
            base.put("businessTypeCode", businessTypeCode);
        }
        if (modelId != null) {
            base.put("modelId", modelId);
        }
        if (name != null) {
            base.put("name", name);
        }
        if (status != null) {
            base.put("status", status);
        }
        if (parentId != null) {
            base.put("parentId", parentId);
        }
        return base;
    }

    public static Map<String, Object> mergeBase(Map<String, Object> primary, Map<String, Object> overlay) {
        Map<String, Object> merged = EntityFieldMapsSupport.normalizeMap(primary);
        if (overlay != null) {
            merged.putAll(overlay);
        }
        return merged;
    }

    public static EntityCreateReqVO createReq(
            String businessTypeCode,
            Long modelId,
            String name,
            Integer status,
            Long parentId,
            Map<String, Object> baseFieldsOverlay,
            Map<String, Object> customFields) {
        EntityCreateReqVO req = new EntityCreateReqVO();
        req.setBaseFields(mergeBase(baseOf(businessTypeCode, modelId, name, status, parentId), baseFieldsOverlay));
        req.setCustomFields(customFields);
        return req;
    }

    public static EntityUpdateReqVO updateReq(
            Long id,
            String businessTypeCode,
            Long modelId,
            String name,
            Integer status,
            Long parentId,
            Map<String, Object> baseFieldsOverlay,
            Map<String, Object> customFields) {
        EntityUpdateReqVO req = new EntityUpdateReqVO();
        req.setId(id);
        req.setBaseFields(mergeBase(baseOf(businessTypeCode, modelId, name, status, parentId), baseFieldsOverlay));
        req.setCustomFields(customFields);
        return req;
    }
}
