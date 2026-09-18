package cn.cheers.x.module.dynamicbusiness.service.sop;

import cn.cheers.x.module.dynamicbusiness.service.entity.version.EntityVersionCommandService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * SOP 发布命令实现。
 *
 * <p>发布与保存分离：</p>
 * <ul>
 *   <li>保存：允许草稿内容反复编辑；</li>
 *   <li>发布：必须显式指定版本号，再把该版本设为 PUBLISHED。</li>
 * </ul>
 */
@Service
public class FlowPublishCommandServiceImpl implements FlowPublishCommandService {
    @Resource
    private EntityVersionCommandService entityVersionCommandService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishByVersion(long currentFlowId, int versionNo) {
        entityVersionCommandService.publishByVersion(FlowFieldCodes.ENTITY_TYPE_CODE, currentFlowId, versionNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveToVersion(long currentFlowId, int versionNo) {
        entityVersionCommandService.saveToVersion(FlowFieldCodes.ENTITY_TYPE_CODE, currentFlowId, versionNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unpublish(long currentFlowId) {
        entityVersionCommandService.unpublish(FlowFieldCodes.ENTITY_TYPE_CODE, currentFlowId);
    }
}

