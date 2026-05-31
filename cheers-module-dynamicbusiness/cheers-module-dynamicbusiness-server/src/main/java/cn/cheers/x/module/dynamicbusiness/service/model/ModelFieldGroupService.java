package cn.cheers.x.module.dynamicbusiness.service.model;

import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldGroupCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldGroupRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldGroupUpdateReqVO;

import java.util.List;
import java.util.Map;

/**
 * 模型字段分组 Service 接口
 */
public interface ModelFieldGroupService {

    /**
     * 创建模型字段分组
     *
     * @param reqVO 创建信息
     * @return 分组ID
     */
    Long createModelFieldGroup(ModelFieldGroupCreateReqVO reqVO);

    /**
     * 更新模型字段分组
     *
     * @param modelId 模型ID
     * @param reqVO 更新信息
     */
    void updateModelFieldGroup(Long modelId, ModelFieldGroupUpdateReqVO reqVO);

    /**
     * 删除模型字段分组
     *
     * @param modelId 模型ID
     * @param groupId 分组ID
     */
    void deleteModelFieldGroup(Long modelId, Long groupId);

    /**
     * 获取模型字段分组详情
     *
     * @param modelId 模型ID
     * @param groupId 分组ID
     * @return 分组详情
     */
    ModelFieldGroupRespVO getModelFieldGroup(Long modelId, Long groupId);

    /**
     * 根据模型ID查询字段分组列表
     *
     * @param modelId 模型ID
     * @return 分组列表
     */
    List<ModelFieldGroupRespVO> listModelFieldGroupsByModelId(Long modelId);

    /**
     * 将字段分配到指定分组（配置层）
     *
     * 说明：
     * - 会确保该字段只属于一个分组：在添加到目标分组前，会先从所有分组的字段列表中移除该字段
     * - 分组内排序由服务自动维护
     *
     * @param modelId 模型ID
     * @param fieldId 字段ID
     * @param groupId 分组ID（对外 Long 形式）
     */
    void assignFieldToGroup(Long modelId, Long fieldId, Long groupId);

    /**
     * 从所有分组中移除指定字段（配置层）
     *
     * @param modelId 模型ID
     * @param fieldId 字段ID
     */
    void unassignFieldFromGroup(Long modelId, Long fieldId);

    /**
     * 获取指定分组下的字段ID列表（按分组内排序返回）
     *
     * @param modelId 模型ID
     * @param groupId 分组ID
     * @return 字段ID列表
     */
    List<Long> getFieldIdsByGroup(Long modelId, Long groupId);

    /**
     * 重排指定分组下的字段顺序（配置层）
     *
     * 说明：
     * - orderedFieldIds 为该分组下最终希望保留的字段 ID 顺序
     * - 会根据该顺序重建分组内的字段引用列表，并按顺序重新赋值 sort
     * - 多余或缺失的字段将被忽略（只保留 orderedFieldIds 中出现的字段）
     *
     * @param modelId         模型ID
     * @param groupId         分组ID
     * @param orderedFieldIds 期望的字段ID顺序列表
     */
    void reorderFieldsInGroup(Long modelId, Long groupId, List<Long> orderedFieldIds);

    /**
     * 获取模型下“字段ID -> 分组ID”的映射关系
     *
     * @param modelId 模型ID
     * @return 映射：key 为字段ID，value 为分组ID
     */
    Map<Long, Long> getFieldGroupMapping(Long modelId);
}
