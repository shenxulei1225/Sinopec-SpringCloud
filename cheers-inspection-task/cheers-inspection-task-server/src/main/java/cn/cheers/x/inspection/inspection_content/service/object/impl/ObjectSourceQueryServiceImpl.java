package cn.cheers.x.inspection.inspection_content.service.object.impl;

import cn.cheers.x.inspection.inspection_content.service.object.ObjectSourceQueryService;
import cn.cheers.x.inspection.inspection_content.service.source.ObjectSourceAdapter;
import cn.cheers.x.inspection.inspection_content.service.source.ObjectSourceRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 巡检对象查询服务实现。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ObjectSourceQueryServiceImpl implements ObjectSourceQueryService {

    private final ObjectSourceRegistry objectSourceRegistry;

    @Override
    public ObjectSourceAdapter.ObjectDetail getObjectDetail(String sourceType, String objectCode) {
        if (sourceType == null || objectCode == null) {
            return null;
        }
        ObjectSourceAdapter adapter = objectSourceRegistry.getAdapter(sourceType);
        if (adapter == null) {
            log.warn("未找到来源类型 [{}] 的适配器", sourceType);
            return null;
        }
        return adapter.getObjectDetail(objectCode);
    }

    @Override
    public Map<String, ObjectSourceAdapter.ObjectDetail> listObjectDetails(String sourceType, List<String> objectCodes) {
        if (sourceType == null || objectCodes == null || objectCodes.isEmpty()) {
            return Collections.emptyMap();
        }
        ObjectSourceAdapter adapter = objectSourceRegistry.getAdapter(sourceType);
        if (adapter == null) {
            log.warn("未找到来源类型 [{}] 的适配器", sourceType);
            return Collections.emptyMap();
        }
        return adapter.listObjectDetails(objectCodes);
    }

    @Override
    public boolean exists(String sourceType, String objectCode) {
        return getObjectDetail(sourceType, objectCode) != null;
    }

    @Override
    public List<ObjectSourceAdapter.InspectionObject> listObjects(String sourceType, Long categoryId, String keyword) {
        if (sourceType == null) {
            return Collections.emptyList();
        }
        ObjectSourceAdapter adapter = objectSourceRegistry.getAdapter(sourceType);
        if (adapter == null) {
            log.warn("未找到来源类型 [{}] 的适配器", sourceType);
            return Collections.emptyList();
        }
        return adapter.listObjects(categoryId, keyword);
    }

    @Override
    public List<String> listSourceTypes() {
        return objectSourceRegistry.getAllSourceTypes();
    }
}
