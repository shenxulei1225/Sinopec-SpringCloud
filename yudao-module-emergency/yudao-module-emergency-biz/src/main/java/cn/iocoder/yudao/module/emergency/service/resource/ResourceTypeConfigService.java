package cn.iocoder.yudao.module.emergency.service.resource;

/**
 * 资源类型配置 Service 接口
 *
 * @author 芋道源码
 */
public interface ResourceTypeConfigService {

    /**
     * 检查资源类型是否允许多事件共享
     *
     * @param resourceType 资源类型
     * @return 是否允许多事件共享
     */
    boolean isAllowMultiEventShare(String resourceType);
}
