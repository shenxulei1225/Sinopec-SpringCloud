package cn.iocoder.yudao.module.emergency.service.dispatch;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.module.emergency.dal.dataobject.dispatch.ResourceDispatchDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.dispatch.ResourcePoolDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.dispatch.ResourceDispatchMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.dispatch.ResourcePoolMapper;
import cn.iocoder.yudao.module.emergency.enums.error.ErrorCodeConstants;
import cn.iocoder.yudao.module.emergency.service.resource.ResourceTypeShareRuleService;
import cn.iocoder.yudao.module.emergency.service.resource.ResourceTypeConfigService;
import cn.iocoder.yudao.module.emergency.framework.common.util.DistributedLockUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class ResourceDispatchServiceImpl implements ResourceDispatchService {

    private final ResourceDispatchMapper dispatchMapper;
    private final ResourceTypeShareRuleService shareRuleService;
    private final ResourcePoolService resourcePoolService;
    private final ResourcePoolMapper resourcePoolMapper;
    private final DistributedLockUtil distributedLockUtil;
    private final ResourceTypeConfigService resourceTypeConfigService;

    public ResourceDispatchServiceImpl(ResourceDispatchMapper dispatchMapper,
                                       ResourceTypeShareRuleService shareRuleService,
                                       ResourcePoolService resourcePoolService,
                                       ResourcePoolMapper resourcePoolMapper,
                                       DistributedLockUtil distributedLockUtil,
                                       ResourceTypeConfigService resourceTypeConfigService) {
        this.dispatchMapper = dispatchMapper;
        this.shareRuleService = shareRuleService;
        this.resourcePoolService = resourcePoolService;
        this.resourcePoolMapper = resourcePoolMapper;
        this.distributedLockUtil = distributedLockUtil;
        this.resourceTypeConfigService = resourceTypeConfigService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, noRollbackFor = ServiceException.class)
    public ResourceDispatchDO createDispatch(ResourceDispatchDO dispatch) {
        // 获取资源信息
        ResourcePoolDO resource = resourcePoolService.getResource(dispatch.getResourceId());
        if (resource == null) {
            // 业务异常（资源不存在）不应该导致事务回滚，使用 noRollbackFor = ServiceException.class
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.RESOURCE_NOT_EXISTS);
        }

        // 校验共享规则
        if (!shareRuleService.canAssignToEvent(resource.getType(), dispatch.getResourceId(), dispatch.getEventId())) {
            // 业务异常（共享规则不满足）不应该导致事务回滚，使用 noRollbackFor = ServiceException.class
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.RESOURCE_NOT_EXISTS, 
                    "资源类型 " + resource.getType() + " 不允许共享或已达到最大共享数量");
        }

        // 业务校验通过后，执行数据库操作
        dispatch.setStatus("pending");
        dispatchMapper.insert(dispatch);
        return dispatch;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, noRollbackFor = ServiceException.class)
    public void dispatch(Long dispatchId) {
        ResourceDispatchDO dispatch = getOrThrow(dispatchId);
        if (!"pending".equals(dispatch.getStatus())) {
            return;
        }
        dispatch.setStatus("dispatched");
        dispatch.setDispatchTime(LocalDateTime.now());
        // 使用 updateById 因为需要更新 dispatchTime
        dispatchMapper.updateById(dispatch);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, noRollbackFor = ServiceException.class)
    public void markInUse(Long dispatchId) {
        ResourceDispatchDO dispatch = getOrThrow(dispatchId);
        if (!"dispatched".equals(dispatch.getStatus())) {
            return;
        }
        // 使用 updateStatusById 优化：在数据库层面更新状态
        dispatchMapper.updateStatusById(dispatchId, "in_use");
    }

    @Override
    @Transactional(rollbackFor = Exception.class, noRollbackFor = ServiceException.class)
    public void recover(Long dispatchId, String reason) {
        ResourceDispatchDO dispatch = getOrThrow(dispatchId);
        if ("recovered".equals(dispatch.getStatus())) {
            return;
        }
        // 使用 updateStatusById 优化：在数据库层面自动设置 recovered_time
        dispatchMapper.updateStatusById(dispatchId, "recovered");
    }

    @Override
    public ResourceDispatchDO get(Long id) {
        return dispatchMapper.selectById(id);
    }

    /**
     * 分配资源（使用分布式锁防止并发冲突）
     * 
     * @param resourceId 资源ID
     * @param eventId 事件ID
     * @param responseId 响应ID（可选，预警阶段时为null）
     * @param stage 调度阶段（预警阶段/响应阶段）
     * @return 资源调度记录
     */
    @Transactional(rollbackFor = Exception.class)
    public ResourceDispatchDO dispatchResource(Long resourceId, Long eventId, Long responseId, String stage) {
        String lockKey = "lock:resource:dispatch:" + resourceId;
        
        return distributedLockUtil.executeWithLock(lockKey, 5, 10, () -> {
            // 检查资源是否存在且可用
            ResourcePoolDO resource = resourcePoolMapper.selectById(resourceId);
            if (resource == null) {
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.RESOURCE_NOT_EXISTS);
            }
            if (!"available".equals(resource.getStatus())) {
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.RESOURCE_NOT_AVAILABLE);
            }
            
            // BR-003和BR-009：根据资源类型的共享规则校验资源是否可分配
            // 如果资源类型不允许多事件共享，则必须校验资源未被其他事件占用
            String resourceType = resource.getType();
            boolean allowMultiEventShare = resourceTypeConfigService.isAllowMultiEventShare(resourceType);
            
            if (!allowMultiEventShare) {
                // 资源类型不允许多事件共享，检查资源是否已被其他事件占用
                ResourceDispatchDO existing = dispatchMapper.selectOne(
                    new LambdaQueryWrapper<ResourceDispatchDO>()
                        .eq(ResourceDispatchDO::getResourceId, resourceId)
                        .in(ResourceDispatchDO::getStatus, "pending", "dispatched", "in_use") // 检查所有非回收状态
                        .ne(ResourceDispatchDO::getEventId, eventId)
                        .eq(ResourceDispatchDO::getDeleted, false)
                );
                if (existing != null) {
                    throw ServiceExceptionUtil.exception(ErrorCodeConstants.RESOURCE_ALREADY_DISPATCHED);
                }
            }
            // 如果资源类型允许多事件共享，则允许分配给多个事件
            
            // 创建资源调度记录
            ResourceDispatchDO dispatch = ResourceDispatchDO.builder()
                    .resourceId(resourceId)
                    .eventId(eventId)
                    .responseId(responseId)
                    .stage(stage)
                    .status("pending")
                    .build();
            dispatchMapper.insert(dispatch);
            
            // 更新资源状态为已分配
            resource.setStatus("in_use");
            resourcePoolMapper.updateById(resource);
            
            return dispatch;
        });
    }

    private ResourceDispatchDO getOrThrow(Long id) {
        ResourceDispatchDO dispatch = dispatchMapper.selectById(id);
        if (dispatch == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.DISPATCH_NOT_EXISTS);
        }
        return dispatch;
    }
}

