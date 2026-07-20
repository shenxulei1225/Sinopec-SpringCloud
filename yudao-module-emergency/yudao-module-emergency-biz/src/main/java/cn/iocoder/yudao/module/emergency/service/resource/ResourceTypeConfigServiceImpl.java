package cn.iocoder.yudao.module.emergency.service.resource;

import cn.iocoder.yudao.module.emergency.dal.dataobject.resource.ResourceTypeConfigDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.resource.ResourceTypeConfigMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 资源类型配置 Service 实现类
 *
 * 提供资源类型共享规则的查询功能
 *
 * @author 芋道源码
 */
@Service
@Slf4j
public class ResourceTypeConfigServiceImpl implements ResourceTypeConfigService {

    @Resource
    private ResourceTypeConfigMapper resourceTypeConfigMapper;

    @Override
    public boolean isAllowMultiEventShare(String resourceType) {
        if (resourceType == null || resourceType.trim().isEmpty()) {
            // 如果资源类型为空，默认不允许多事件共享（更安全）
            log.warn("资源类型为空，默认不允许多事件共享");
            return false;
        }

        // 查询资源类型配置
        ResourceTypeConfigDO config = resourceTypeConfigMapper.selectOne(
                new LambdaQueryWrapper<ResourceTypeConfigDO>()
                        .eq(ResourceTypeConfigDO::getResourceType, resourceType)
        );

        if (config == null) {
            // 如果没有配置，默认不允许多事件共享（更安全）
            log.debug("资源类型 {} 未配置共享规则，默认不允许多事件共享", resourceType);
            return false;
        }

        // 返回配置的共享规则
        Boolean allowMultiEventShare = config.getAllowMultiEventShare();
        return allowMultiEventShare != null && allowMultiEventShare;
    }
}
