package cn.cheers.x.module.dynamicbusiness.service.model;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldGroupCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldGroupRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldGroupUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.util.SparseSortUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 模型字段分组 Service 实现类
 * 
 * 采用方案A：JSON字段存储
 * 
 * 设计原则：
 * 1. 分组信息存储在 ModelDO.fieldGroupsConfig 字段中（JSON格式），与字段库完全解耦
 * 2. 分组信息只属于 Model，不依赖任何字段
 * 3. 字段与分组的关联通过 Model.fieldGroupsConfig（JSON）中的 fields 引用列表实现
 * 4. 字段库是全局通用的，不存储任何与 model 相关的内容
 * 
 * 职责：
 * - 只负责管理分组信息（创建、更新、删除、查询）
 * - 不涉及字段操作
 * - 不涉及字段与分组的关联操作（由 ModelFieldAssignmentService 负责）
 */
@Service
@Validated
public class ModelFieldGroupServiceImpl implements ModelFieldGroupService {

    private static final String DEFAULT_BASE_GROUP_NAME = "基础信息";

    @Resource
    private ModelMapper modelMapper;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 字段分组配置数据结构
     */
    private static class FieldGroupsConfig {
        /**
         * 分组列表
         */
        private List<FieldGroupItem> groups = new ArrayList<>();

        public List<FieldGroupItem> getGroups() {
            return groups;
        }

        public void setGroups(List<FieldGroupItem> groups) {
            this.groups = groups;
        }
    }

    /**
     * 字段分组项
     *
     * 说明：
     * - id/name/color/sort 为分组自身信息
     * - fields 为该分组下的字段引用列表，包含字段ID以及在分组内的排序等信息
     */
    private static class FieldGroupItem {
        /**
         * 分组内部字符串 ID（存储在 JSON 中）
         */
        private String id;
        /**
         * 分组名称
         */
        private String name;
        /**
         * 分组颜色
         */
        private String color;
        /**
         * 分组排序
         */
        private Integer sort;
        /**
         * 分组下的字段列表
         */
        private List<FieldRefItem> fields = new ArrayList<>();

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public Integer getSort() {
            return sort;
        }

        public void setSort(Integer sort) {
            this.sort = sort;
        }

        public List<FieldRefItem> getFields() {
            return fields;
        }

        public void setFields(List<FieldRefItem> fields) {
            this.fields = fields;
        }
    }

    /**
     * 分组内字段引用信息
     *
     * 说明：
     * - fieldId：字段ID，可以是基础字段或自定义字段
     * - sort：字段在该分组内的排序
     * - extra：预留扩展信息（将来可记录更多元数据）
     */
    private static class FieldRefItem {
        private Long fieldId;
        private Integer sort;
        private Map<String, Object> extra = new HashMap<>();

        public Long getFieldId() {
            return fieldId;
        }

        public void setFieldId(Long fieldId) {
            this.fieldId = fieldId;
        }

        public Integer getSort() {
            return sort;
        }

        public void setSort(Integer sort) {
            this.sort = sort;
        }

        public Map<String, Object> getExtra() {
            return extra;
        }

        @SuppressWarnings("unused")
        public void setExtra(Map<String, Object> extra) {
            this.extra = extra;
        }
    }

    /**
     * 从 Model 中读取分组配置
     */
    private FieldGroupsConfig readFieldGroupsConfig(Long modelId) {
        ModelDO model = modelMapper.selectById(modelId);
        if (model == null) {
            throw new ServiceException(404, "模型不存在，ID：" + modelId);
        }

        String configJson = model.getFieldGroupsConfig();
        if (configJson == null || configJson.trim().isEmpty()) {
            return new FieldGroupsConfig();
        }

        try {
            FieldGroupsConfig config = objectMapper.readValue(configJson, new TypeReference<FieldGroupsConfig>() {});
            // 兼容旧数据：确保 groups 非空，且每个分组的 fields 列表非空
            if (config.getGroups() == null) {
                config.setGroups(new ArrayList<>());
            } else {
                for (FieldGroupItem group : config.getGroups()) {
                    if (group.getFields() == null) {
                        group.setFields(new ArrayList<>());
                    }
                }
            }
            return config;
        } catch (Exception e) {
            // JSON 解析失败，返回空配置
            return new FieldGroupsConfig();
        }
    }

    /**
     * 保存分组配置到 Model
     */
    private void saveFieldGroupsConfig(Long modelId, FieldGroupsConfig config) {
        ModelDO model = modelMapper.selectById(modelId);
        if (model == null) {
            throw new ServiceException(404, "模型不存在，ID：" + modelId);
        }

        try {
            String configJson = objectMapper.writeValueAsString(config);
            model.setFieldGroupsConfig(configJson);
            modelMapper.updateById(model);
        } catch (Exception e) {
            throw new ServiceException(500, "保存分组配置失败：" + e.getMessage());
        }
    }

    /**
     * 生成分组ID（字符串格式，用于JSON存储）
     */
    private String generateGroupId(List<FieldGroupItem> existingGroups) {
        // 使用基于时间戳的唯一 ID：group-<epochMillis>[-<suffix>]
        // 说明：
        // 1) 解决并发/回滚/解析失败导致的 “每次都生成 group-1” 的重复问题
        // 2) 以毫秒级时间戳为主键，若同毫秒内出现冲突，则追加递增后缀确保唯一
        long ts = System.currentTimeMillis();
        String base = "group-" + ts;
        if (existingGroups == null || existingGroups.isEmpty()) {
            return base;
        }
        boolean exists = existingGroups.stream()
                .map(FieldGroupItem::getId)
                .anyMatch(id -> id != null && id.equals(base));
        if (!exists) {
            return base;
        }
        // 同毫秒碰撞：追加后缀
        int suffix = 1;
        while (true) {
            String candidate = base + "-" + suffix;
            boolean candidateExists = existingGroups.stream()
                    .map(FieldGroupItem::getId)
                    .anyMatch(id -> id != null && id.equals(candidate));
            if (!candidateExists) {
                return candidate;
            }
            suffix++;
        }
    }

    /**
     * 将字符串ID转换为Long（用于API返回）
     */
    private Long stringIdToLong(String stringId) {
        if (stringId == null) {
            return null;
        }
        // 使用字符串的hashCode，但确保是正数
        return (long) Math.abs(stringId.hashCode());
    }

    /**
     * 将Long ID转换为字符串ID（用于查找）
     */
    private String longIdToString(Long longId, List<FieldGroupItem> groups) {
        if (longId == null) {
            return null;
        }
        // 遍历所有分组，找到hashCode匹配的
        for (FieldGroupItem group : groups) {
            if (stringIdToLong(group.getId()).equals(longId)) {
                return group.getId();
            }
        }
        return null;
    }

    @Override
    public Long createModelFieldGroup(ModelFieldGroupCreateReqVO reqVO) {
        // 校验模型是否存在
        ModelDO model = modelMapper.selectById(reqVO.getModelId());
        if (model == null) {
            throw new ServiceException(404, "模型不存在，ID：" + reqVO.getModelId());
        }

        // 读取现有配置
        FieldGroupsConfig config = readFieldGroupsConfig(reqVO.getModelId());

        // 校验同一模型下分组名称唯一性
        boolean nameExists = config.getGroups().stream()
                .anyMatch(g -> g.getName().equals(reqVO.getName()));
        if (nameExists) {
            throw new ServiceException(400, "该模型下字段分组名称已存在：" + reqVO.getName());
        }

        // 创建新分组
        FieldGroupItem newGroup = new FieldGroupItem();
        newGroup.setId(generateGroupId(config.getGroups()));
        newGroup.setName(reqVO.getName());
        newGroup.setColor(reqVO.getColor() != null && !reqVO.getColor().isEmpty()
                ? reqVO.getColor() : "#409eff");
        newGroup.setFields(new ArrayList<>());

        // 统一稀疏排序：末尾追加 last + STEP
        Integer currentMax = config.getGroups().stream()
                .map(FieldGroupItem::getSort)
                .filter(Objects::nonNull)
                .max(Integer::compareTo)
                .orElse(null);
        newGroup.setSort(SparseSortUtils.next(currentMax));

        // 添加到配置
        config.getGroups().add(newGroup);
        
        // 按排序值排序
        config.getGroups().sort(Comparator.comparingInt(g -> g.getSort() != null ? g.getSort() : 0));

        // 保存配置
        saveFieldGroupsConfig(reqVO.getModelId(), config);

        // 返回分组ID（转换为Long）
        return stringIdToLong(newGroup.getId());
    }

    @Override
    public void updateModelFieldGroup(Long modelId, ModelFieldGroupUpdateReqVO reqVO) {
        // 读取配置
        FieldGroupsConfig config = readFieldGroupsConfig(modelId);

        // 查找分组（通过Long ID转换为字符串ID）
        String groupIdStr = longIdToString(reqVO.getId(), config.getGroups());
        if (groupIdStr == null) {
            throw new ServiceException(404, "模型字段分组不存在");
        }

        FieldGroupItem group = config.getGroups().stream()
                .filter(g -> g.getId().equals(groupIdStr))
                .findFirst()
                .orElse(null);

        if (group == null) {
            throw new ServiceException(404, "模型字段分组不存在");
        }

        // 校验同一模型下分组名称唯一性（排除自己）
        boolean nameExists = config.getGroups().stream()
                .anyMatch(g -> g.getName().equals(reqVO.getName()) 
                        && !g.getId().equals(group.getId()));
        if (nameExists) {
            throw new ServiceException(400, "该模型下字段分组名称已存在：" + reqVO.getName());
        }

        // 更新分组信息
        group.setName(reqVO.getName());
        if (reqVO.getColor() != null) {
            group.setColor(reqVO.getColor());
        }
        if (reqVO.getSort() != null) {
            group.setSort(reqVO.getSort());
        }

        // 重新排序
        config.getGroups().sort(Comparator.comparingInt(g -> g.getSort() != null ? g.getSort() : 0));

        // 保存配置
        saveFieldGroupsConfig(modelId, config);
    }

    @Override
    public void deleteModelFieldGroup(Long modelId, Long groupId) {
        // 读取配置
        FieldGroupsConfig config = readFieldGroupsConfig(modelId);

        // 查找并删除分组
        String groupIdStr = longIdToString(groupId, config.getGroups());
        if (groupIdStr == null) {
            throw new ServiceException(404, "模型字段分组不存在");
        }

        boolean removed = config.getGroups().removeIf(g -> g.getId().equals(groupIdStr));

        if (!removed) {
            throw new ServiceException(404, "模型字段分组不存在");
        }

        // 保存配置
        saveFieldGroupsConfig(modelId, config);
    }

    @Override
    public ModelFieldGroupRespVO getModelFieldGroup(Long modelId, Long groupId) {
        // 读取配置
        FieldGroupsConfig config = readFieldGroupsConfig(modelId);

        // 查找分组
        String groupIdStr = longIdToString(groupId, config.getGroups());
        if (groupIdStr == null) {
            throw new ServiceException(404, "模型字段分组不存在");
        }

        FieldGroupItem group = config.getGroups().stream()
                .filter(g -> g.getId().equals(groupIdStr))
                .findFirst()
                .orElse(null);

        if (group == null) {
            throw new ServiceException(404, "模型字段分组不存在");
        }

        // 转换为 VO
        ModelFieldGroupRespVO respVO = new ModelFieldGroupRespVO();
        respVO.setId(groupId);
        respVO.setModelId(modelId);
        respVO.setName(group.getName());
        respVO.setColor(group.getColor());
        respVO.setSort(group.getSort());
        respVO.setFields(convertFieldRefs(group.getFields()));

        return respVO;
    }

    @Override
    public List<ModelFieldGroupRespVO> listModelFieldGroupsByModelId(Long modelId) {
        // 读取配置
        FieldGroupsConfig config = readFieldGroupsConfig(modelId);

        // 自动补齐默认“基础信息”分组（避免前端出现“未分组”）
        ensureDefaultBaseGroup(modelId, config);

        // 转换为 VO 列表
        return config.getGroups().stream()
                .map(group -> {
                    ModelFieldGroupRespVO respVO = new ModelFieldGroupRespVO();
                    respVO.setId(stringIdToLong(group.getId()));
                    respVO.setModelId(modelId);
                    respVO.setName(group.getName());
                    respVO.setColor(group.getColor());
                    respVO.setSort(group.getSort());
                    respVO.setFields(convertFieldRefs(group.getFields()));
                    return respVO;
                })
                .collect(Collectors.toList());
    }

    private List<ModelFieldGroupRespVO.FieldRefVO> convertFieldRefs(List<FieldRefItem> refs) {
        if (refs == null || refs.isEmpty()) {
            return new ArrayList<>();
        }
        return refs.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(ref -> ref.getSort() != null ? ref.getSort() : 0))
                .map(ref -> {
                    ModelFieldGroupRespVO.FieldRefVO vo = new ModelFieldGroupRespVO.FieldRefVO();
                    vo.setFieldId(ref.getFieldId());
                    vo.setSort(ref.getSort());
                    vo.setExtra(ref.getExtra());
                    return vo;
                })
                .collect(Collectors.toList());
    }

    /**
     * 确保存在默认“基础信息”分组。
     * 若不存在则自动创建并持久化，避免前端出现“未分组”桶。
     */
    private void ensureDefaultBaseGroup(Long modelId, FieldGroupsConfig config) {
        if (config == null) {
            return;
        }
        if (config.getGroups() == null) {
            config.setGroups(new ArrayList<>());
        }
        boolean exists = config.getGroups().stream()
                .anyMatch(g -> g != null && DEFAULT_BASE_GROUP_NAME.equals(g.getName()));
        if (exists) {
            return;
        }

        FieldGroupItem baseGroup = new FieldGroupItem();
        baseGroup.setId(generateGroupId(config.getGroups()));
        baseGroup.setName(DEFAULT_BASE_GROUP_NAME);
        baseGroup.setColor("#409eff");
        baseGroup.setFields(new ArrayList<>());

        Integer currentMax = config.getGroups().stream()
                .map(FieldGroupItem::getSort)
                .filter(Objects::nonNull)
                .max(Integer::compareTo)
                .orElse(null);
        baseGroup.setSort(SparseSortUtils.next(currentMax));

        config.getGroups().add(baseGroup);
        config.getGroups().sort(Comparator.comparingInt(g -> g.getSort() != null ? g.getSort() : 0));
        saveFieldGroupsConfig(modelId, config);
    }

    @Override
    public void assignFieldToGroup(Long modelId, Long fieldId, Long groupId) {
        Objects.requireNonNull(modelId, "modelId 不能为空");
        Objects.requireNonNull(fieldId, "fieldId 不能为空");
        Objects.requireNonNull(groupId, "groupId 不能为空");

        FieldGroupsConfig config = readFieldGroupsConfig(modelId);
        List<FieldGroupItem> groups = config.getGroups();
        if (groups == null || groups.isEmpty()) {
            throw new ServiceException(404, "模型字段分组不存在");
        }

        // 查找目标分组（通过 Long ID 转字符串 ID）
        String targetGroupIdStr = longIdToString(groupId, groups);
        if (targetGroupIdStr == null) {
            throw new ServiceException(404, "模型字段分组不存在");
        }

        // 1. 先从所有分组中移除该字段，确保一个字段只属于一个分组
        for (FieldGroupItem group : groups) {
            if (group.getFields() == null) {
                group.setFields(new ArrayList<>());
                continue;
            }
            group.getFields().removeIf(ref -> fieldId.equals(ref.getFieldId()));
        }

        // 2. 将字段添加到目标分组
        FieldGroupItem targetGroup = groups.stream()
                .filter(g -> targetGroupIdStr.equals(g.getId()))
                .findFirst()
                .orElseThrow(() -> new ServiceException(404, "模型字段分组不存在"));

        if (targetGroup.getFields() == null) {
            targetGroup.setFields(new ArrayList<>());
        }

        // 统一稀疏排序：分组内末尾追加 last + STEP
        Integer currentMax = targetGroup.getFields().stream()
                .map(FieldRefItem::getSort)
                .filter(Objects::nonNull)
                .max(Integer::compareTo)
                .orElse(null);

        FieldRefItem newRef = new FieldRefItem();
        newRef.setFieldId(fieldId);
        newRef.setSort(SparseSortUtils.next(currentMax));
        targetGroup.getFields().add(newRef);

        // 按 sort 排序字段列表
        targetGroup.getFields().sort(Comparator.comparingInt(ref -> ref.getSort() != null ? ref.getSort() : 0));

        // 保存配置
        saveFieldGroupsConfig(modelId, config);
    }

    @Override
    public void unassignFieldFromGroup(Long modelId, Long fieldId) {
        Objects.requireNonNull(modelId, "modelId 不能为空");
        Objects.requireNonNull(fieldId, "fieldId 不能为空");

        FieldGroupsConfig config = readFieldGroupsConfig(modelId);
        List<FieldGroupItem> groups = config.getGroups();
        if (groups == null || groups.isEmpty()) {
            return;
        }

        boolean changed = false;
        for (FieldGroupItem group : groups) {
            if (group.getFields() == null || group.getFields().isEmpty()) {
                continue;
            }
            boolean removed = group.getFields().removeIf(ref -> fieldId.equals(ref.getFieldId()));
            if (removed) {
                changed = true;
                // 重新整理排序（稀疏重排）
                int idx = 0;
                for (FieldRefItem ref : group.getFields()) {
                    ref.setSort(SparseSortUtils.reindexSortByPosition(idx++));
                }
            }
        }

        if (changed) {
            saveFieldGroupsConfig(modelId, config);
        }
    }

    @Override
    public List<Long> getFieldIdsByGroup(Long modelId, Long groupId) {
        Objects.requireNonNull(modelId, "modelId 不能为空");
        Objects.requireNonNull(groupId, "groupId 不能为空");

        FieldGroupsConfig config = readFieldGroupsConfig(modelId);
        List<FieldGroupItem> groups = config.getGroups();
        if (groups == null || groups.isEmpty()) {
            return new ArrayList<>();
        }

        String groupIdStr = longIdToString(groupId, groups);
        if (groupIdStr == null) {
            throw new ServiceException(404, "模型字段分组不存在");
        }

        FieldGroupItem group = groups.stream()
                .filter(g -> groupIdStr.equals(g.getId()))
                .findFirst()
                .orElseThrow(() -> new ServiceException(404, "模型字段分组不存在"));

        if (group.getFields() == null || group.getFields().isEmpty()) {
            return new ArrayList<>();
        }

        // 按 sort 排序后返回字段ID列表
        return group.getFields().stream()
                .sorted(Comparator.comparingInt(ref -> ref.getSort() != null ? ref.getSort() : 0))
                .map(FieldRefItem::getFieldId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void reorderFieldsInGroup(Long modelId, Long groupId, List<Long> orderedFieldIds) {
        Objects.requireNonNull(modelId, "modelId 不能为空");
        Objects.requireNonNull(groupId, "groupId 不能为空");
        if (orderedFieldIds == null || orderedFieldIds.isEmpty()) {
            // 没有任何字段需要重排，直接返回
            return;
        }

        FieldGroupsConfig config = readFieldGroupsConfig(modelId);
        List<FieldGroupItem> groups = config.getGroups();
        if (groups == null || groups.isEmpty()) {
            throw new ServiceException(404, "模型字段分组不存在");
        }

        String groupIdStr = longIdToString(groupId, groups);
        if (groupIdStr == null) {
            throw new ServiceException(404, "模型字段分组不存在");
        }

        FieldGroupItem group = groups.stream()
                .filter(g -> groupIdStr.equals(g.getId()))
                .findFirst()
                .orElseThrow(() -> new ServiceException(404, "模型字段分组不存在"));

        if (group.getFields() == null) {
            group.setFields(new ArrayList<>());
        }

        // 构建现有字段引用的映射（便于保留 extra 等信息）
        Map<Long, FieldRefItem> existingRefMap = new HashMap<>();
        for (FieldRefItem ref : group.getFields()) {
            if (ref != null && ref.getFieldId() != null) {
                existingRefMap.put(ref.getFieldId(), ref);
            }
        }

        // 使用 orderedFieldIds 重建字段列表，并按顺序重新赋稀疏 sort
        List<FieldRefItem> newRefs = new ArrayList<>();
        int index = 0;
        for (Long fieldId : orderedFieldIds) {
            if (fieldId == null) {
                continue;
            }
            FieldRefItem ref = existingRefMap.get(fieldId);
            if (ref == null) {
                ref = new FieldRefItem();
                ref.setFieldId(fieldId);
            }
            ref.setSort(SparseSortUtils.reindexSortByPosition(index++));
            newRefs.add(ref);
        }

        group.setFields(newRefs);
        saveFieldGroupsConfig(modelId, config);
    }

    @Override
    public Map<Long, Long> getFieldGroupMapping(Long modelId) {
        Objects.requireNonNull(modelId, "modelId 不能为空");

        FieldGroupsConfig config = readFieldGroupsConfig(modelId);
        List<FieldGroupItem> groups = config.getGroups();
        Map<Long, Long> mapping = new HashMap<>();
        if (groups == null || groups.isEmpty()) {
            return mapping;
        }

        for (FieldGroupItem group : groups) {
            Long groupLongId = stringIdToLong(group.getId());
            if (groupLongId == null) {
                continue;
            }
            if (group.getFields() == null) {
                continue;
            }
            for (FieldRefItem ref : group.getFields()) {
                if (ref.getFieldId() == null) {
                    continue;
                }
                // 同一个字段只保留最后一次映射（按读取顺序，通常不会重复）
                mapping.put(ref.getFieldId(), groupLongId);
            }
        }
        return mapping;
    }
}
