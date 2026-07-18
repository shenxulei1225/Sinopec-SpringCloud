package cn.iocoder.yudao.module.inspection.inspection_content.service.source.impl;

import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRpcDtoSupport;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.inspection.inspection_content.service.source.ObjectSourceAdapter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 设施对象来源适配器：通过动态业务 {@link EntityRpcApi} 读取 {@code entityTypeCode=facility} 实体。
 */
@Slf4j
@Service
public class FacilityObjectSourceAdapter implements ObjectSourceAdapter {

    public static final String SOURCE_CODE = "facility";
    private static final String ENTITY_TYPE_FACILITY = "facility";

    @Resource
    private EntityRpcApi entityRpcApi;

    @Override
    public String getSourceCode() {
        return SOURCE_CODE;
    }

    @Override
    public List<InspectionObject> listObjects(Long categoryId, String objectModel) {
        try {
            CommonResult<List<EntityRespDTO>> result = entityRpcApi.listEntities(ENTITY_TYPE_FACILITY, categoryId);
            List<EntityRespDTO> entities = unwrapList(result);
            List<InspectionObject> objects = entities.stream()
                    .map(this::toInspectionObject)
                    .collect(Collectors.toList());
            if (objectModel != null && !objectModel.isEmpty()) {
                return objects.stream()
                        .filter(obj -> objectModel.equals(obj.getObjectModel()))
                        .collect(Collectors.toList());
            }
            return objects;
        } catch (Exception e) {
            log.error("查询设施对象列表失败", e);
            return Collections.emptyList();
        }
    }

    @Override
    public InspectionObject getObjectByCode(String objectCode) {
        try {
            CommonResult<EntityRespDTO> result = entityRpcApi.getEntityByCode(objectCode, ENTITY_TYPE_FACILITY);
            EntityRespDTO entity = unwrapOne(result);
            return entity == null ? null : toInspectionObject(entity);
        } catch (Exception e) {
            log.error("根据编码查询设施失败: {}", objectCode, e);
            return null;
        }
    }

    @Override
    public List<String> listObjectModels() {
        try {
            CommonResult<List<EntityRespDTO>> result = entityRpcApi.listEntities(ENTITY_TYPE_FACILITY, null);
            List<EntityRespDTO> entities = unwrapList(result);
            return entities.stream()
                    .map(this::readObjectModel)
                    .filter(model -> model != null && !model.isEmpty())
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询设施型号列表失败", e);
            return Collections.emptyList();
        }
    }

    @Override
    public ObjectDetail getObjectDetail(String objectCode) {
        try {
            CommonResult<EntityRespDTO> result = entityRpcApi.getEntityByCode(objectCode, ENTITY_TYPE_FACILITY);
            EntityRespDTO entity = unwrapOne(result);
            return entity == null ? null : toObjectDetail(entity);
        } catch (Exception e) {
            log.error("查询设施详情失败: {}", objectCode, e);
            return null;
        }
    }

    @Override
    public java.util.Map<String, ObjectDetail> listObjectDetails(List<String> objectCodes) {
        if (objectCodes == null || objectCodes.isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            CommonResult<List<EntityRespDTO>> result = entityRpcApi.listEntitiesByCodes(objectCodes, ENTITY_TYPE_FACILITY);
            List<EntityRespDTO> entities = unwrapList(result);
            return entities.stream()
                    .collect(Collectors.toMap(
                            EntityRpcDtoSupport::readCode,
                            this::toObjectDetail,
                            (v1, v2) -> v1
                    ));
        } catch (Exception e) {
            log.error("批量查询设施详情失败", e);
            return Collections.emptyMap();
        }
    }

    private InspectionObject toInspectionObject(EntityRespDTO entity) {
        InspectionObject object = new InspectionObject();
        object.setObjectCode(EntityRpcDtoSupport.readCode(entity));
        object.setObjectName(EntityRpcDtoSupport.readName(entity));
        object.setCategoryId(EntityRpcDtoSupport.readModelId(entity));
        object.setObjectModel(readObjectModel(entity));
        return object;
    }

    private ObjectDetail toObjectDetail(EntityRespDTO entity) {
        ObjectDetail detail = new ObjectDetail();
        detail.setObjectCode(EntityRpcDtoSupport.readCode(entity));
        detail.setObjectName(EntityRpcDtoSupport.readName(entity));
        detail.setLocation(EntityRpcDtoSupport.readField(entity, "address", "location"));
        Integer status = readStatus(entity);
        detail.setStatus(status != null ? String.valueOf(status) : null);
        return detail;
    }

    private String readObjectModel(EntityRespDTO entity) {
        String model = EntityRpcDtoSupport.readField(entity, "facilityType", "model");
        if (model != null) {
            return model;
        }
        Long modelId = EntityRpcDtoSupport.readModelId(entity);
        return modelId != null ? String.valueOf(modelId) : null;
    }

    private Integer readStatus(EntityRespDTO entity) {
        String status = EntityRpcDtoSupport.readField(entity, "status");
        if (status == null) {
            return null;
        }
        try {
            return Integer.parseInt(status);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static EntityRespDTO unwrapOne(CommonResult<EntityRespDTO> result) {
        if (result == null || !result.isSuccess()) {
            return null;
        }
        return result.getData();
    }

    private static List<EntityRespDTO> unwrapList(CommonResult<List<EntityRespDTO>> result) {
        if (result == null || !result.isSuccess() || result.getData() == null) {
            return Collections.emptyList();
        }
        return result.getData();
    }
}
