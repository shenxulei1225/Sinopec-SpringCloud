package cn.iocoder.yudao.module.inspection.inspection_content.service.library.impl;

import cn.iocoder.yudao.module.inspection.inspection_content.controller.admin.library.vo.*;
import cn.iocoder.yudao.module.inspection.inspection_content.dal.dataobject.item.InspectionItemDO;
import cn.iocoder.yudao.module.inspection.inspection_content.dal.dataobject.object.InspectionObjectSourceDO;
import cn.iocoder.yudao.module.inspection.inspection_content.dal.mysql.object.InspectionObjectSourceMapper;
import cn.iocoder.yudao.module.inspection.inspection_content.service.item.InspectionItemQueryService;
import cn.iocoder.yudao.module.inspection.inspection_content.service.library.InspectionLibraryService;
import cn.iocoder.yudao.module.inspection.inspection_content.service.source.ObjectSourceAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 巡检对象库服务实现。
 *
 * <p>内部通过适配器调用各个业务系统，提供统一的巡检对象库查询能力。</p>
 */
@Slf4j
@Service
public class InspectionLibraryServiceImpl implements InspectionLibraryService {

    private final Map<String, ObjectSourceAdapter> adapterMap;
    private final InspectionObjectSourceMapper sourceMapper;
    private final InspectionItemQueryService itemQueryService;

    public InspectionLibraryServiceImpl(List<ObjectSourceAdapter> adapters,
                                       InspectionObjectSourceMapper sourceMapper,
                                       InspectionItemQueryService itemQueryService) {
        this.sourceMapper = sourceMapper;
        this.itemQueryService = itemQueryService;
        this.adapterMap = adapters.stream()
                .collect(Collectors.toMap(
                        ObjectSourceAdapter::getSourceCode,
                        a -> a,
                        (a, b) -> a
                ));
    }

    // ==================== 来源管理 ====================

    @Override
    public List<LibrarySourceVO> getSources() {
        return sourceMapper.selectListByStatus("enabled").stream()
                .map(source -> {
                    LibrarySourceVO vo = new LibrarySourceVO();
                    vo.setSourceCode(source.getSourceCode());
                    vo.setSourceName(source.getSourceName());
                    vo.setSourceType(source.getSourceType());
                    vo.setStatus(source.getStatus());
                    vo.setRemark(source.getRemark());
                    return vo;
                })
                .collect(Collectors.toList());
    }

    // ==================== 方式一：分步获取（推荐） ====================

    @Override
    public List<ModelWithItemsVO> getModelsWithItems(String sourceCode, Long categoryId) {
        ObjectSourceAdapter adapter = adapterMap.get(sourceCode);
        if (adapter == null) {
            log.warn("未找到来源适配器: {}", sourceCode);
            return Collections.emptyList();
        }

        // 1. 获取该来源下的所有型号
        List<String> models = adapter.listObjectModels();
        if (models.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 获取来源配置，确定 sourceType
        InspectionObjectSourceDO source = sourceMapper.selectBySourceCode(sourceCode);
        if (source == null) {
            return Collections.emptyList();
        }

        // 3. 遍历每个型号，查询检查项
        List<ModelWithItemsVO> result = new ArrayList<>();
        for (String model : models) {
            ModelWithItemsVO vo = new ModelWithItemsVO();
            vo.setObjectModel(model);

            // 3.1 获取该型号下的设备数量
            List<ObjectSourceAdapter.InspectionObject> objects = adapter.listObjects(categoryId, model);
            vo.setDeviceCount(objects.size());

            // 3.2 获取该型号对应的检查项
            List<InspectionItemDO> items = itemQueryService.getItemsForObject(source.getSourceType(), categoryId, model);
            vo.setItems(convertItems(items));

            result.add(vo);
        }

        return result;
    }

    @Override
    public List<LibraryObjectVO> getObjects(String sourceCode, Long categoryId, String objectModel) {
        ObjectSourceAdapter adapter = adapterMap.get(sourceCode);
        if (adapter == null) {
            log.warn("未找到来源适配器: {}", sourceCode);
            return Collections.emptyList();
        }

        return adapter.listObjects(categoryId, objectModel).stream()
                .map(this::toObjectVO)
                .collect(Collectors.toList());
    }

    // ==================== 方式二：一次性获取 ====================

    @Override
    public FullTreeVO getFullTree(String sourceCode, Long categoryId) {
        ObjectSourceAdapter adapter = adapterMap.get(sourceCode);
        if (adapter == null) {
            log.warn("未找到来源适配器: {}", sourceCode);
            return new FullTreeVO();
        }

        InspectionObjectSourceDO source = sourceMapper.selectBySourceCode(sourceCode);
        if (source == null) {
            return new FullTreeVO();
        }

        FullTreeVO tree = new FullTreeVO();
        List<FullTreeVO.ModelNodeVO> nodes = new ArrayList<>();

        // 按型号分组
        List<ObjectSourceAdapter.InspectionObject> allObjects = adapter.listObjects(categoryId, null);
        Map<String, List<ObjectSourceAdapter.InspectionObject>> objectsByModel = allObjects.stream()
                .collect(Collectors.groupingBy(
                        obj -> obj.getObjectModel() != null ? obj.getObjectModel() : "",
                        Collectors.toList()
                ));

        for (Map.Entry<String, List<ObjectSourceAdapter.InspectionObject>> entry : objectsByModel.entrySet()) {
            String model = entry.getKey();
            if (model.isEmpty()) {
                continue;
            }

            List<ObjectSourceAdapter.InspectionObject> objects = entry.getValue();
            List<InspectionItemDO> items = itemQueryService.getItemsForObject(source.getSourceType(), categoryId, model);

            FullTreeVO.ModelNodeVO node = new FullTreeVO.ModelNodeVO();
            node.setObjectModel(model);
            node.setDeviceCount(objects.size());
            node.setItems(convertItems(items));
            node.setDevices(objects.stream()
                    .map(this::toObjectVO)
                    .collect(Collectors.toList()));
            nodes.add(node);
        }

        tree.setModels(nodes);
        return tree;
    }

    // ==================== 方式三：直接查询检查项 ====================

    @Override
    public List<LibraryItemVO> getItems(String sourceCode, Long categoryId, String objectModel) {
        InspectionObjectSourceDO source = sourceMapper.selectBySourceCode(sourceCode);
        if (source == null) {
            log.warn("未找到来源配置: {}", sourceCode);
            return Collections.emptyList();
        }

        List<InspectionItemDO> items = itemQueryService.getItemsForObject(source.getSourceType(), categoryId, objectModel);
        return convertItems(items);
    }

    // ==================== 工具方法 ====================

    private LibraryObjectVO toObjectVO(ObjectSourceAdapter.InspectionObject obj) {
        LibraryObjectVO vo = new LibraryObjectVO();
        vo.setObjectCode(obj.getObjectCode());
        vo.setObjectName(obj.getObjectName());
        vo.setCategoryId(obj.getCategoryId());
        vo.setObjectModel(obj.getObjectModel());
        vo.setExtra(obj.getExtra());
        return vo;
    }

    private List<LibraryItemVO> convertItems(List<InspectionItemDO> items) {
        return items.stream()
                .map(item -> {
                    LibraryItemVO vo = new LibraryItemVO();
                    vo.setItemId(item.getId());
                    vo.setItemCode(item.getItemCode());
                    vo.setItemName(item.getItemName());
                    vo.setMethod(item.getMethod());
                    vo.setStandard(item.getStandard());
                    vo.setStatus(item.getStatus());
                    vo.setRemark(item.getRemark());
                    return vo;
                })
                .collect(Collectors.toList());
    }
}
