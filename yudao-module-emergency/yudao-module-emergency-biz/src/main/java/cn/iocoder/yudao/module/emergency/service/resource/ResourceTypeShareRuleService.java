package cn.iocoder.yudao.module.emergency.service.resource;

import cn.iocoder.yudao.module.emergency.dal.dataobject.dispatch.ResourceTypeShareRuleDO;

/**
 * 资源类型共享规则服务接口
 * 
 * 负责管理资源类型的共享规则配置，包括：
 * - 配置每个资源类型是否允许多事件共享
 * - 配置最大共享数量
 * - 在资源分配时校验共享规则
 */
public interface ResourceTypeShareRuleService {

    /**
     * 获取资源类型的共享规则
     * 
     * @param resourceType 资源类型
     * @return 共享规则配置
     */
    ResourceTypeShareRuleDO getShareRule(String resourceType);

    /**
     * 校验资源是否可以分配给指定事件
     * 
     * @param resourceType 资源类型
     * @param resourceId 资源ID
     * @param eventId 事件ID
     * @return 是否可以分配
     */
    boolean canAssignToEvent(String resourceType, Long resourceId, Long eventId);

    /**
     * 检查资源是否已达到最大共享数量
     * 
     * @param resourceType 资源类型
     * @param resourceId 资源ID
     * @return 是否已达到最大共享数量
     */
    boolean isMaxShareCountReached(String resourceType, Long resourceId);
}



