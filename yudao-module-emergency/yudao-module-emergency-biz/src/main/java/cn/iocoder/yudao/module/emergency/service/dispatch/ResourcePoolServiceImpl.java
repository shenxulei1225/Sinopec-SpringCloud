package cn.iocoder.yudao.module.emergency.service.dispatch;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.emergency.controller.admin.dispatch.vo.ResourcePoolCreateReqVO;
import cn.iocoder.yudao.module.emergency.controller.admin.dispatch.vo.ResourcePoolPageReqVO;
import cn.iocoder.yudao.module.emergency.controller.admin.dispatch.vo.ResourcePoolRespVO;
import cn.iocoder.yudao.module.emergency.controller.admin.dispatch.vo.ResourcePoolUpdateReqVO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.dispatch.ResourcePoolDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.dispatch.ResourcePoolMapper;
import cn.iocoder.yudao.module.emergency.enums.error.ErrorCodeConstants;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
public class ResourcePoolServiceImpl implements ResourcePoolService {

    private final ResourcePoolMapper resourcePoolMapper;

    public ResourcePoolServiceImpl(ResourcePoolMapper resourcePoolMapper) {
        this.resourcePoolMapper = resourcePoolMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, noRollbackFor = ServiceException.class)
    public Long createResource(ResourcePoolCreateReqVO createReqVO) {
        // 检查资源名称是否重复（业务异常不应该导致回滚）
        // 注意：如果数据库层面也有唯一键约束，插入时可能会抛出数据库异常，这种情况下应该回滚
        validateResourceNameUnique(null, createReqVO.getName());

        ResourcePoolDO resource = BeanUtils.toBean(createReqVO, ResourcePoolDO.class);
        resource.setStatus("available"); // 默认状态为可用
        resourcePoolMapper.insert(resource);
        return resource.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class, noRollbackFor = ServiceException.class)
    public void updateResource(ResourcePoolUpdateReqVO updateReqVO) {
        // 校验存在（业务异常不应该导致回滚）
        ResourcePoolDO resource = validateResourceExists(updateReqVO.getId());

        // 检查资源名称是否重复（业务异常不应该导致回滚）
        validateResourceNameUnique(updateReqVO.getId(), updateReqVO.getName());

        ResourcePoolDO updateObj = BeanUtils.toBean(updateReqVO, ResourcePoolDO.class);
        resourcePoolMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, noRollbackFor = ServiceException.class)
    public void deleteResource(Long id) {
        // 校验存在（业务异常不应该导致回滚）
        validateResourceExists(id);

        // 删除
        resourcePoolMapper.deleteById(id);
    }

    @Override
    public ResourcePoolDO getResource(Long id) {
        return resourcePoolMapper.selectById(id);
    }

    @Override
    public ResourcePoolRespVO getResourceDetail(Long id) {
        ResourcePoolDO resource = resourcePoolMapper.selectById(id);
        return BeanUtils.toBean(resource, ResourcePoolRespVO.class);
    }

    @Override
    public PageResult<ResourcePoolRespVO> getResourcePage(ResourcePoolPageReqVO pageReqVO) {
        LambdaQueryWrapper<ResourcePoolDO> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.like(pageReqVO.getName() != null, ResourcePoolDO::getName, pageReqVO.getName())
                   .eq(pageReqVO.getType() != null, ResourcePoolDO::getType, pageReqVO.getType())
                   .eq(pageReqVO.getStatus() != null, ResourcePoolDO::getStatus, pageReqVO.getStatus())
                   .eq(pageReqVO.getOrganizationId() != null, ResourcePoolDO::getOrganizationId, pageReqVO.getOrganizationId())
                   .orderByDesc(ResourcePoolDO::getCreateTime);

        Page<ResourcePoolDO> page = new Page<>(pageReqVO.getPageNo(), pageReqVO.getPageSize());
        Page<ResourcePoolDO> result = resourcePoolMapper.selectPage(page, queryWrapper);
        List<ResourcePoolRespVO> list = BeanUtils.toBean(result.getRecords(), ResourcePoolRespVO.class);
        return new PageResult<>(list, result.getTotal());
    }

    @Override
    public List<ResourcePoolRespVO> getResourceList(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        // 使用 selectList 替代已废弃的 selectBatchIds 方法
        LambdaQueryWrapper<ResourcePoolDO> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.in(ResourcePoolDO::getId, ids);
        List<ResourcePoolDO> resources = resourcePoolMapper.selectList(queryWrapper);
        return BeanUtils.toBean(resources, ResourcePoolRespVO.class);
    }

    @Override
    public List<ResourcePoolDO> getAvailableResourcesByType(String type) {
        return resourcePoolMapper.selectAvailableByType(type);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, noRollbackFor = ServiceException.class)
    public void updateResourceStatus(Long id, String status) {
        // 校验存在（业务异常不应该导致回滚）
        ResourcePoolDO resource = validateResourceExists(id);
        resource.setStatus(status);
        resourcePoolMapper.updateById(resource);
    }

    @Override
    public Integer countResourcesByTypeAndStatus(String type, String status) {
        return resourcePoolMapper.countByTypeAndStatus(type, status);
    }

    private ResourcePoolDO validateResourceExists(Long id) {
        ResourcePoolDO resource = resourcePoolMapper.selectById(id);
        if (resource == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.RESOURCE_NOT_EXISTS);
        }
        return resource;
    }

    private void validateResourceNameUnique(Long id, String name) {
        LambdaQueryWrapper<ResourcePoolDO> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(ResourcePoolDO::getName, name);
        if (id != null) {
            queryWrapper.ne(ResourcePoolDO::getId, id);
        }
        ResourcePoolDO existing = resourcePoolMapper.selectOne(queryWrapper);
        if (existing != null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.RESOURCE_NAME_DUPLICATE);
        }
    }
}
