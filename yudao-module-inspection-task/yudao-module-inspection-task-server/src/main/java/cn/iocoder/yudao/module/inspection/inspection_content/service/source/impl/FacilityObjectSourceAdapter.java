package cn.iocoder.yudao.module.inspection.inspection_content.service.source.impl;

import cn.iocoder.yudao.module.facility.management.api.FacilityApi;
import cn.iocoder.yudao.module.facility.management.api.dto.FacilityRespDTO;
import cn.iocoder.yudao.module.inspection.inspection_content.service.source.ObjectSourceAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 设施系统对象来源适配器实现。
 *
 * <p>实现对 facility-management 模块的适配，用于：</p>
 * <ul>
 *     <li>查询设施列表（按分类、型号筛选）</li>
 *     <li>查询设施详情（位置、负责人、状态等扩展信息）</li>
 *     <li>查询设施型号列表</li>
 * </ul>
 *
 * <p>当设施模块未实现时，提供空实现或基础信息回退。</p>
 */
@Slf4j
@Service
public class FacilityObjectSourceAdapter implements ObjectSourceAdapter {

    public static final String SOURCE_CODE = "facility";

    @Autowired(required = false)
    private FacilityApi facilityApi;

    @Override
    public String getSourceCode() {
        return SOURCE_CODE;
    }

    @Override
    public List<InspectionObject> listObjects(Long categoryId, String objectModel) {
        if (facilityApi == null) {
            log.warn("FacilityApi 未实现，无法查询设施对象列表");
            return Collections.emptyList();
        }
        try {
            List<FacilityRespDTO> facilities = facilityApi.getSimpleFacilities(null, categoryId, null).getData();
            if (facilities == null) {
                return Collections.emptyList();
            }

            List<InspectionObject> objects = facilities.stream()
                    .map(this::toInspectionObject)
                    .collect(Collectors.toList());

            // 如果指定了 objectModel，进行过滤
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
        if (facilityApi == null) {
            log.warn("FacilityApi 未实现，无法根据编码查询设施");
            return null;
        }
        try {
            List<FacilityRespDTO> facilities = facilityApi.getFacilitiesByCodes(List.of(objectCode)).getData();
            if (facilities == null || facilities.isEmpty()) {
                return null;
            }
            return toInspectionObject(facilities.get(0));
        } catch (Exception e) {
            log.error("根据编码查询设施失败: {}", objectCode, e);
            return null;
        }
    }

    @Override
    public List<String> listObjectModels() {
        if (facilityApi == null) {
            log.warn("FacilityApi 未实现，无法查询设施型号列表");
            return Collections.emptyList();
        }
        try {
            List<FacilityRespDTO> facilities = facilityApi.getSimpleFacilities(null, null, null).getData();
            if (facilities == null) {
                return Collections.emptyList();
            }
            return facilities.stream()
                    .map(FacilityRespDTO::getModel)
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
        if (facilityApi == null) {
            log.warn("FacilityApi 未实现，无法查询设施详情");
            return null;
        }
        try {
            List<FacilityRespDTO> facilities = facilityApi.getFacilitiesByCodes(List.of(objectCode)).getData();
            if (facilities == null || facilities.isEmpty()) {
                return null;
            }
            return toObjectDetail(facilities.get(0));
        } catch (Exception e) {
            log.error("查询设施详情失败: {}", objectCode, e);
            return null;
        }
    }

    @Override
    public java.util.Map<String, ObjectDetail> listObjectDetails(List<String> objectCodes) {
        if (facilityApi == null) {
            log.warn("FacilityApi 未实现，无法批量查询设施详情");
            return Collections.emptyMap();
        }
        if (objectCodes == null || objectCodes.isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            List<FacilityRespDTO> facilities = facilityApi.getFacilitiesByCodes(objectCodes).getData();
            if (facilities == null) {
                return Collections.emptyMap();
            }
            return facilities.stream()
                    .collect(Collectors.toMap(
                            FacilityRespDTO::getFacilityCode,
                            this::toObjectDetail,
                            (v1, v2) -> v1
                    ));
        } catch (Exception e) {
            log.error("批量查询设施详情失败", e);
            return Collections.emptyMap();
        }
    }

    /**
     * 转换为巡检对象。
     */
    private InspectionObject toInspectionObject(FacilityRespDTO facility) {
        InspectionObject object = new InspectionObject();
        object.setObjectCode(facility.getFacilityCode());
        object.setObjectName(facility.getFacilityName());
        object.setCategoryId(facility.getCategoryId());
        object.setObjectModel(facility.getModel());
        return object;
    }

    /**
     * 转换为对象详情。
     *
     * <p>注意：FacilityRespDTO 不包含 manager 和 managerPhone 字段，
     *    这些信息需要从其他数据源获取（如 TwinModule 的设备台账）。</p>
     */
    private ObjectDetail toObjectDetail(FacilityRespDTO facility) {
        ObjectDetail detail = new ObjectDetail();
        detail.setObjectCode(facility.getFacilityCode());
        detail.setObjectName(facility.getFacilityName());
        detail.setLocation(facility.getLocation());
        // manager 和 managerPhone 字段在 FacilityRespDTO 中不存在，设为 null
        // 如需此信息，可通过 TwinModule 的设备台账关联查询
        detail.setStatus(facility.getStatus() != null ? String.valueOf(facility.getStatus()) : null);
        return detail;
    }
}
