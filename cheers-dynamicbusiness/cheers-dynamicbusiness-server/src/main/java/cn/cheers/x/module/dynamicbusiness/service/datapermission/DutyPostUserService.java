package cn.cheers.x.module.dynamicbusiness.service.datapermission;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission.DutyPostUserDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datapermission.DutyPostUserMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 职务上的人员写入。
 * 组织架构「职务管理」把人分到职务时调用。
 * 不负责：管理单元、能看哪些数据（那是任职，见 {@link PostAssignmentService}）。
 */
@Service
public class DutyPostUserService {

    @Resource
    private DutyPostUserMapper dutyPostUserMapper;
    @Resource
    private DataScopeConditionResolver conditionResolver;

    /**
     * 给一个职务加上一批人。已经担任的人跳过。
     * 不检查管理单元。
     */
    @Transactional(rollbackFor = Exception.class)
    public List<Long> createBatch(Long dutyPostId, List<Long> userIds) {
        requireDuty(dutyPostId);
        Set<Long> ids = new LinkedHashSet<>(userIds == null ? List.of() : userIds);
        ids.remove(null);
        if (ids.isEmpty()) {
            throw new ServiceException(400, "请选择人员");
        }
        List<Long> created = new ArrayList<>();
        for (Long userId : ids) {
            if (!conditionResolver.recordExists("user_account", userId)) {
                throw new ServiceException(400, "人员不存在：" + userId);
            }
            if (dutyPostUserMapper.selectByDutyAndUser(dutyPostId, userId) != null) {
                continue;
            }
            DutyPostUserDO row = new DutyPostUserDO();
            row.setDutyPostId(dutyPostId);
            row.setUserId(userId);
            dutyPostUserMapper.insert(row);
            created.add(row.getId());
        }
        if (created.isEmpty()) {
            throw new ServiceException(400, "这些人都已经担任这个职务");
        }
        return created;
    }

    /** 从这个职务去掉一个人。 */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        DutyPostUserDO existing = id == null ? null : dutyPostUserMapper.selectById(id);
        if (existing == null) {
            throw new ServiceException(404, "这个职务上没有这个人");
        }
        dutyPostUserMapper.deleteById(id);
    }

    private void requireDuty(Long dutyPostId) {
        if (dutyPostId == null || !conditionResolver.recordExists(PostAssignmentService.DUTY_POST_CATALOG, dutyPostId)) {
            throw new ServiceException(400, "职务不存在：" + dutyPostId);
        }
    }
}
