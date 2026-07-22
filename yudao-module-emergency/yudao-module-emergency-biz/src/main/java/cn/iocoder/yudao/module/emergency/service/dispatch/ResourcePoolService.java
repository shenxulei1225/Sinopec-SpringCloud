package cn.iocoder.yudao.module.emergency.service.dispatch;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emergency.controller.admin.dispatch.vo.ResourcePoolCreateReqVO;
import cn.iocoder.yudao.module.emergency.controller.admin.dispatch.vo.ResourcePoolPageReqVO;
import cn.iocoder.yudao.module.emergency.controller.admin.dispatch.vo.ResourcePoolRespVO;
import cn.iocoder.yudao.module.emergency.controller.admin.dispatch.vo.ResourcePoolUpdateReqVO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.dispatch.ResourcePoolDO;

import java.util.Collection;
import java.util.List;

public interface ResourcePoolService {

    /**
     * 创建资源
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createResource(ResourcePoolCreateReqVO createReqVO);

    /**
     * 更新资源
     *
     * @param updateReqVO 更新信息
     */
    void updateResource(ResourcePoolUpdateReqVO updateReqVO);

    /**
     * 删除资源
     *
     * @param id 编号
     */
    void deleteResource(Long id);

    /**
     * 获得资源
     *
     * @param id 编号
     * @return 资源
     */
    ResourcePoolDO getResource(Long id);

    /**
     * 获得资源详情
     *
     * @param id 编号
     * @return 资源详情
     */
    ResourcePoolRespVO getResourceDetail(Long id);

    /**
     * 获得资源分页
     *
     * @param pageReqVO 分页查询
     * @return 资源分页
     */
    PageResult<ResourcePoolRespVO> getResourcePage(ResourcePoolPageReqVO pageReqVO);

    /**
     * 获得资源列表
     *
     * @param ids 编号数组
     * @return 资源列表
     */
    List<ResourcePoolRespVO> getResourceList(Collection<Long> ids);

    /**
     * 根据类型获取可用资源
     *
     * @param type 资源类型
     * @return 可用资源列表
     */
    List<ResourcePoolDO> getAvailableResourcesByType(String type);

    /**
     * 更新资源状态
     *
     * @param id     资源ID
     * @param status 新状态
     */
    void updateResourceStatus(Long id, String status);

    /**
     * 根据类型统计资源数量
     *
     * @param type   资源类型
     * @param status 资源状态
     * @return 资源数量
     */
    Integer countResourcesByTypeAndStatus(String type, String status);
}



