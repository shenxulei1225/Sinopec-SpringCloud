package cn.cheers.x.alarm.service.type;

import cn.cheers.x.framework.common.biz.system.category.CategoryCommonApi;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.alarm.controller.admin.vo.type.AlarmTypeCategoryVO;
import cn.cheers.x.alarm.controller.admin.vo.type.AlarmTypeEntityVO;
import cn.cheers.x.alarm.controller.admin.vo.type.AlarmTypeModelVO;
import cn.cheers.x.alarm.framework.cache.AlarmCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 告警类型服务实现类
 * 
 * <p>通过调用 System 模块的 CategoryApi 实现告警类型的三层模型查询。</p>
 * 
 * <p>告警类型层级说明：
 * <ul>
 *   <li>level = 1：根节点（ALARM_TYPE_ROOT）- 不返回给前端</li>
 *   <li>level = 2：Category 层（告警大类）</li>
 *   <li>level = 3：Model 层（告警子类）</li>
 *   <li>level = 4：Entity 层（具体告警项）</li>
 * </ul>
 * </p>
 * 
 * <p>性能优化：
 * <ul>
 *   <li>告警类型树使用 Redis 缓存，缓存时间 5 分钟</li>
 *   <li>告警类型路径使用 Redis 缓存，缓存时间 5 分钟</li>
 *   <li>告警类型实体使用 Redis 缓存，缓存时间 5 分钟</li>
 * </ul>
 * </p>
 *
 * 
 */
@Service
@Slf4j
public class AlarmTypeServiceImpl implements AlarmTypeService {

    @Resource
    private CategoryCommonApi categoryApi;

    @Resource
    private AlarmCacheService alarmCacheService;

    /**
     * 路径分隔符
     */
    private static final String PATH_SEPARATOR = " > ";

    @Override
    public List<AlarmTypeCategoryVO> getAlarmTypeTree() {
        return getAlarmTypeTree(null);
    }

    @Override
    public List<AlarmTypeCategoryVO> getAlarmTypeTree(Integer status) {
        // 1. 尝试从缓存获取（仅当 status 为 null 时使用缓存）
        if (status == null) {
            List<AlarmTypeCategoryVO> cached = alarmCacheService.getAlarmTypeTreeCache();
            if (cached != null) {
                log.debug("[getAlarmTypeTree] 从缓存获取告警类型树");
                return cached;
            }
        }

        // 2. 调用 CategoryApi 获取分类树
        CommonResult<List<Map<String, Object>>> result = categoryApi.getCategoryTree(BUSINESS_TYPE_CODE, status);
        if (!result.isSuccess() || CollectionUtils.isEmpty(result.getData())) {
            log.warn("[getAlarmTypeTree] 获取告警类型树失败或为空，status={}", status);
            return Collections.emptyList();
        }

        // 3. 转换为告警类型树结构
        // 返回的树结构中，根节点（level=1）的 children 就是 Category 层
        List<Map<String, Object>> treeData = result.getData();
        
        // 4. 找到根节点，获取其子节点作为 Category 层
        List<AlarmTypeCategoryVO> categories = new ArrayList<>();
        for (Map<String, Object> node : treeData) {
            Integer level = getIntValue(node, "level");
            if (level != null && level == 1) {
                // 根节点，获取其子节点
                List<Map<String, Object>> children = getChildren(node);
                if (!CollectionUtils.isEmpty(children)) {
                    for (Map<String, Object> categoryNode : children) {
                        AlarmTypeCategoryVO categoryVO = convertToCategory(categoryNode);
                        categories.add(categoryVO);
                    }
                }
            } else if (level != null && level == 2) {
                // 如果直接返回的是 Category 层（没有根节点包装）
                AlarmTypeCategoryVO categoryVO = convertToCategory(node);
                categories.add(categoryVO);
            }
        }

        // 5. 按 sort 排序
        categories.sort(Comparator.comparing(AlarmTypeCategoryVO::getSort, Comparator.nullsLast(Comparator.naturalOrder())));
        
        // 6. 设置缓存（仅当 status 为 null 时缓存）
        if (status == null && !categories.isEmpty()) {
            alarmCacheService.setAlarmTypeTreeCache(categories);
            log.debug("[getAlarmTypeTree] 从数据库查询并缓存告警类型树");
        }
        
        return categories;
    }

    @Override
    public List<AlarmTypeModelVO> getModelsByCategory(Long categoryId) {
        if (categoryId == null) {
            return Collections.emptyList();
        }

        // 调用 CategoryApi 获取子分类
        CommonResult<List<Map<String, Object>>> result = categoryApi.getChildren(categoryId, BUSINESS_TYPE_CODE, null);
        if (!result.isSuccess() || CollectionUtils.isEmpty(result.getData())) {
            return Collections.emptyList();
        }

        // 转换为 Model VO 列表
        List<AlarmTypeModelVO> models = result.getData().stream()
                .map(this::convertToModel)
                .sorted(Comparator.comparing(AlarmTypeModelVO::getSort, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());

        return models;
    }

    @Override
    public List<AlarmTypeEntityVO> getEntitiesByModel(Long modelId) {
        if (modelId == null) {
            return Collections.emptyList();
        }

        // 调用 CategoryApi 获取子分类
        CommonResult<List<Map<String, Object>>> result = categoryApi.getChildren(modelId, BUSINESS_TYPE_CODE, null);
        if (!result.isSuccess() || CollectionUtils.isEmpty(result.getData())) {
            return Collections.emptyList();
        }

        // 转换为 Entity VO 列表，并获取完整路径
        List<AlarmTypeEntityVO> entities = new ArrayList<>();
        for (Map<String, Object> node : result.getData()) {
            AlarmTypeEntityVO entityVO = convertToEntity(node);
            // 获取完整路径
            Long entityId = entityVO.getId();
            if (entityId != null) {
                entityVO.setFullPath(getAlarmTypePath(entityId));
            }
            entities.add(entityVO);
        }

        // 按 sort 排序
        entities.sort(Comparator.comparing(AlarmTypeEntityVO::getSort, Comparator.nullsLast(Comparator.naturalOrder())));

        return entities;
    }

    @Override
    public String getAlarmTypePath(Long entityId) {
        if (entityId == null) {
            return null;
        }

        // 1. 尝试从缓存获取
        String cachedPath = alarmCacheService.getAlarmTypePathCache(entityId);
        if (cachedPath != null) {
            log.debug("[getAlarmTypePath] 从缓存获取告警类型路径: entityId={}", entityId);
            return cachedPath;
        }

        // 2. 调用 CategoryApi 获取路径
        CommonResult<List<Map<String, Object>>> result = categoryApi.getPath(entityId, BUSINESS_TYPE_CODE);
        if (!result.isSuccess() || CollectionUtils.isEmpty(result.getData())) {
            return null;
        }

        // 3. 构建路径字符串，跳过根节点（level=1）
        List<String> pathNames = result.getData().stream()
                .filter(node -> {
                    Integer level = getIntValue(node, "level");
                    return level != null && level > 1; // 跳过根节点
                })
                .sorted(Comparator.comparing(node -> getIntValue(node, "level")))
                .map(node -> getStringValue(node, "name"))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        String path = String.join(PATH_SEPARATOR, pathNames);
        
        // 4. 设置缓存
        if (path != null && !path.isEmpty()) {
            alarmCacheService.setAlarmTypePathCache(entityId, path);
            log.debug("[getAlarmTypePath] 从数据库查询并缓存告警类型路径: entityId={}", entityId);
        }
        
        return path;
    }

    @Override
    public AlarmTypeEntityVO getAlarmTypeEntity(Long entityId) {
        if (entityId == null) {
            return null;
        }

        // 1. 尝试从缓存获取
        AlarmTypeEntityVO cachedEntity = alarmCacheService.getAlarmTypeEntityCache(entityId);
        if (cachedEntity != null) {
            log.debug("[getAlarmTypeEntity] 从缓存获取告警类型实体: entityId={}", entityId);
            return cachedEntity;
        }

        // 2. 调用 CategoryApi 获取分类信息
        CommonResult<Map<String, Object>> result = categoryApi.getCategory(entityId, BUSINESS_TYPE_CODE);
        if (!result.isSuccess() || result.getData() == null) {
            return null;
        }

        AlarmTypeEntityVO entityVO = convertToEntity(result.getData());
        // 获取完整路径
        entityVO.setFullPath(getAlarmTypePath(entityId));
        
        // 获取 categoryId：通过 modelId 获取 model，再获取 categoryId
        if (entityVO.getModelId() != null) {
            AlarmTypeModelVO model = getModel(entityVO.getModelId());
            if (model != null) {
                entityVO.setCategoryId(model.getCategoryId());
            }
        }
        
        // 3. 设置缓存
        alarmCacheService.setAlarmTypeEntityCache(entityId, entityVO);
        log.debug("[getAlarmTypeEntity] 从数据库查询并缓存告警类型实体: entityId={}", entityId);
        
        return entityVO;
    }

    @Override
    public AlarmTypeEntityVO getAlarmTypeEntityByCode(String code) {
        if (code == null || code.isEmpty()) {
            return null;
        }

        // 通过搜索功能查找
        CommonResult<List<Map<String, Object>>> result = categoryApi.searchCategories(code, BUSINESS_TYPE_CODE);
        if (!result.isSuccess() || CollectionUtils.isEmpty(result.getData())) {
            return null;
        }

        // 查找精确匹配的记录
        for (Map<String, Object> node : result.getData()) {
            String nodeCode = getStringValue(node, "code");
            if (code.equals(nodeCode)) {
                AlarmTypeEntityVO entityVO = convertToEntity(node);
                Long entityId = entityVO.getId();
                if (entityId != null) {
                    entityVO.setFullPath(getAlarmTypePath(entityId));
                }
                return entityVO;
            }
        }

        return null;
    }

    @Override
    public boolean existsAlarmType(Long entityId) {
        if (entityId == null) {
            return false;
        }

        CommonResult<Boolean> result = categoryApi.existsCategory(entityId, BUSINESS_TYPE_CODE);
        return result.isSuccess() && Boolean.TRUE.equals(result.getData());
    }

    @Override
    public List<AlarmTypeCategoryVO> getAllCategories() {
        return getAllCategories(null);
    }

    @Override
    public List<AlarmTypeCategoryVO> getAllCategories(Integer status) {
        // 获取完整树，然后只返回 Category 层（不包含子节点）
        List<AlarmTypeCategoryVO> tree = getAlarmTypeTree(status);
        
        // 清除子节点
        return tree.stream()
                .map(category -> {
                    AlarmTypeCategoryVO vo = new AlarmTypeCategoryVO();
                    vo.setId(category.getId());
                    vo.setCode(category.getCode());
                    vo.setName(category.getName());
                    vo.setSort(category.getSort());
                    vo.setStatus(category.getStatus());
                    vo.setChildren(null); // 不包含子节点
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public AlarmTypeCategoryVO getCategory(Long categoryId) {
        if (categoryId == null) {
            return null;
        }

        CommonResult<Map<String, Object>> result = categoryApi.getCategory(categoryId, BUSINESS_TYPE_CODE);
        if (!result.isSuccess() || result.getData() == null) {
            return null;
        }

        Map<String, Object> data = result.getData();
        Integer level = getIntValue(data, "level");
        
        // 验证是否是 Category 层（level = 2）
        if (level == null || level != 2) {
            log.warn("[getCategory] 分类ID {} 不是 Category 层，level={}", categoryId, level);
            return null;
        }

        return convertToCategorySimple(data);
    }

    @Override
    public AlarmTypeModelVO getModel(Long modelId) {
        if (modelId == null) {
            return null;
        }

        CommonResult<Map<String, Object>> result = categoryApi.getCategory(modelId, BUSINESS_TYPE_CODE);
        if (!result.isSuccess() || result.getData() == null) {
            return null;
        }

        Map<String, Object> data = result.getData();
        Integer level = getIntValue(data, "level");
        
        // 验证是否是 Model 层（level = 3）
        if (level == null || level != 3) {
            log.warn("[getModel] 模型ID {} 不是 Model 层，level={}", modelId, level);
            return null;
        }

        return convertToModelSimple(data);
    }

    // ========== 私有方法：数据转换 ==========

    /**
     * 转换为 Category VO（包含子节点）
     */
    private AlarmTypeCategoryVO convertToCategory(Map<String, Object> node) {
        AlarmTypeCategoryVO vo = convertToCategorySimple(node);
        
        // 处理子节点（Model 层）
        List<Map<String, Object>> children = getChildren(node);
        if (!CollectionUtils.isEmpty(children)) {
            List<AlarmTypeModelVO> models = new ArrayList<>();
            for (Map<String, Object> modelNode : children) {
                AlarmTypeModelVO modelVO = convertToModel(modelNode);
                models.add(modelVO);
            }
            models.sort(Comparator.comparing(AlarmTypeModelVO::getSort, Comparator.nullsLast(Comparator.naturalOrder())));
            vo.setChildren(models);
        }
        
        return vo;
    }

    /**
     * 转换为 Category VO（不包含子节点）
     */
    private AlarmTypeCategoryVO convertToCategorySimple(Map<String, Object> node) {
        AlarmTypeCategoryVO vo = new AlarmTypeCategoryVO();
        vo.setId(getLongValue(node, "id"));
        vo.setCode(getStringValue(node, "code"));
        vo.setName(getStringValue(node, "name"));
        vo.setSort(getIntValue(node, "sort"));
        vo.setStatus(getIntValue(node, "status"));
        return vo;
    }

    /**
     * 转换为 Model VO（包含子节点）
     */
    private AlarmTypeModelVO convertToModel(Map<String, Object> node) {
        AlarmTypeModelVO vo = convertToModelSimple(node);
        
        // 处理子节点（Entity 层）
        List<Map<String, Object>> children = getChildren(node);
        if (!CollectionUtils.isEmpty(children)) {
            List<AlarmTypeEntityVO> entities = new ArrayList<>();
            for (Map<String, Object> entityNode : children) {
                AlarmTypeEntityVO entityVO = convertToEntity(entityNode);
                // 获取完整路径
                Long entityId = entityVO.getId();
                if (entityId != null) {
                    entityVO.setFullPath(getAlarmTypePath(entityId));
                }
                entities.add(entityVO);
            }
            entities.sort(Comparator.comparing(AlarmTypeEntityVO::getSort, Comparator.nullsLast(Comparator.naturalOrder())));
            vo.setChildren(entities);
        }
        
        return vo;
    }

    /**
     * 转换为 Model VO（不包含子节点）
     */
    private AlarmTypeModelVO convertToModelSimple(Map<String, Object> node) {
        AlarmTypeModelVO vo = new AlarmTypeModelVO();
        vo.setId(getLongValue(node, "id"));
        vo.setCode(getStringValue(node, "code"));
        vo.setName(getStringValue(node, "name"));
        vo.setCategoryId(getLongValue(node, "parentId"));
        vo.setSort(getIntValue(node, "sort"));
        vo.setStatus(getIntValue(node, "status"));
        return vo;
    }

    /**
     * 转换为 Entity VO
     */
    private AlarmTypeEntityVO convertToEntity(Map<String, Object> node) {
        AlarmTypeEntityVO vo = new AlarmTypeEntityVO();
        vo.setId(getLongValue(node, "id"));
        vo.setCode(getStringValue(node, "code"));
        vo.setName(getStringValue(node, "name"));
        vo.setModelId(getLongValue(node, "parentId"));
        vo.setSort(getIntValue(node, "sort"));
        vo.setStatus(getIntValue(node, "status"));
        // categoryId 需要通过路径获取，在调用处设置
        return vo;
    }

    // ========== 私有方法：Map 数据提取 ==========

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getChildren(Map<String, Object> node) {
        Object children = node.get("children");
        if (children instanceof List) {
            return (List<Map<String, Object>>) children;
        }
        return null;
    }

    private String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }

    private Long getLongValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer getIntValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
