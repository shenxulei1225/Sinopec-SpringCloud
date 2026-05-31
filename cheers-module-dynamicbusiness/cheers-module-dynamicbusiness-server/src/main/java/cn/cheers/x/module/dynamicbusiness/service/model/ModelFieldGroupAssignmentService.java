package cn.cheers.x.module.dynamicbusiness.service.model;

import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldAssignmentRespVO;

import java.util.List;

/**
 * 模型字段分组关联 Service 接口
 * 
 * 职责：管理字段与分组的关联关系
 * 
 * 设计说明：
 * - 分组信息存储在 Model.fieldGroupsConfig 中（JSON格式），与字段库完全解耦
 * - 字段库是全局通用的，不存储任何与 model 相关的内容
 * - 字段与分组的关联通过 Model.fieldGroupsConfig（JSON）实现
 * - 分组信息由 ModelFieldGroupService 管理（只管理分组本身，不涉及字段）
 * - 字段关联由此服务管理（只管理字段与分组的关联关系）
 * 
 * @author yudao
 */
public interface ModelFieldGroupAssignmentService {

    /**
     * 将字段分配到分组
     *
     * @param modelId 模型ID
     * @param fieldId 字段ID
     * @param groupId 分组ID
     */
    void assignFieldToGroup(Long modelId, Long fieldId, Long groupId);

    /**
     * 从分组移除字段（取消分组关联）
     *
     * @param modelId 模型ID
     * @param fieldId 字段ID
     */
    void unassignFieldFromGroup(Long modelId, Long fieldId);

    /**
     * 获取分组下的字段列表
     *
     * @param modelId 模型ID
     * @param groupId 分组ID
     * @return 字段列表
     */
    List<ModelFieldAssignmentRespVO> getFieldsByGroup(Long modelId, Long groupId);

    /**
     * 重排指定分组下的字段顺序
     *
     * @param modelId         模型ID
     * @param groupId         分组ID
     * @param orderedFieldIds 期望的字段ID顺序列表
     */
    void reorderGroupFields(Long modelId, Long groupId, List<Long> orderedFieldIds);
}
