package cn.iocoder.yudao.module.emergency.service.resource;

import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.emergency.dal.dataobject.dispatch.ResourceTypeShareRuleDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.dispatch.ResourceTypeShareRuleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 资源类型共享规则服务实现
 */
@Slf4j
@Service
public class ResourceTypeShareRuleServiceImpl implements ResourceTypeShareRuleService {

    private final ResourceTypeShareRuleMapper shareRuleMapper;

    public ResourceTypeShareRuleServiceImpl(ResourceTypeShareRuleMapper shareRuleMapper) {
        this.shareRuleMapper = shareRuleMapper;
    }

    @Override
    public ResourceTypeShareRuleDO getShareRule(String resourceType) {
        return shareRuleMapper.selectOne(
                new LambdaQueryWrapperX<ResourceTypeShareRuleDO>()
                        .eq(ResourceTypeShareRuleDO::getResourceType, resourceType)
                        .last("LIMIT 1"));
    }

    @Override
    public boolean canAssignToEvent(String resourceType, Long resourceId, Long eventId) {
        ResourceTypeShareRuleDO rule = getShareRule(resourceType);
        
        // 如果没有配置规则，默认不允许共享
        if (rule == null) {
            log.warn("资源类型 {} 没有配置共享规则，默认不允许共享", resourceType);
            return false;
        }

        // 如果不允许多事件共享，检查资源是否已经被其他事件占用
        if (!Boolean.TRUE.equals(rule.getAllowMultiEventShare())) {
            // TODO: 检查资源是否已经被其他事件占用
            // 这里需要查询ResourceDispatch表，检查资源是否已经被分配给其他事件
            // 如果已经被分配，则不允许再分配给新事件
            return true; // 简化处理，实际应该查询ResourceDispatch表
        }

        // 如果允许多事件共享，检查是否达到最大共享数量
        if (rule.getMaxShareCount() != null) {
            return !isMaxShareCountReached(resourceType, resourceId);
        }

        return true;
    }

    @Override
    public boolean isMaxShareCountReached(String resourceType, Long resourceId) {
        ResourceTypeShareRuleDO rule = getShareRule(resourceType);
        if (rule == null || rule.getMaxShareCount() == null) {
            return false;
        }

        // TODO: 查询ResourceDispatch表，统计资源当前被多少个事件共享
        // 如果当前共享数量 >= 最大共享数量，则返回true
        // 这里简化处理，实际应该查询ResourceDispatch表
        return false;
    }
}



