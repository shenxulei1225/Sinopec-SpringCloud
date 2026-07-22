package cn.iocoder.yudao.module.emergency.service.organization;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emergency.controller.admin.organization.vo.abrole.*;
import cn.iocoder.yudao.module.emergency.convert.organization.ABRoleConvert;
import cn.iocoder.yudao.module.emergency.dal.dataobject.organization.EmergencyABRoleDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.organization.EmergencyABRoleMapper;
import cn.iocoder.yudao.module.emergency.enums.error.ErrorCodeConstants;
import cn.iocoder.yudao.module.emergency.service.event.EventNotificationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 应急A/B角管理 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Slf4j
public class EmergencyABRoleServiceImpl implements EmergencyABRoleService {

    @Resource
    private EmergencyABRoleMapper abRoleMapper;

    @Resource
    private EventNotificationService notificationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createABRole(ABRoleCreateReqVO createReqVO) {
        // 验证A角和B角用户不能相同
        if (createReqVO.getAUserId().equals(createReqVO.getBUserId())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.AB_ROLE_USER_SAME);
        }

        // 验证角色编号唯一性
        validateRoleCodeUnique(null, createReqVO.getRoleCode());

        // 插入
        EmergencyABRoleDO abRole = ABRoleConvert.INSTANCE.convert(createReqVO);
        if (abRole.getCurrentActive() == null) {
            abRole.setCurrentActive("A"); // 默认A角激活
        }
        if (abRole.getIsEnabled() == null) {
            abRole.setIsEnabled(true);
        }
        abRoleMapper.insert(abRole);

        // 返回
        return abRole.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateABRole(ABRoleUpdateReqVO updateReqVO) {
        // 校验存在
        EmergencyABRoleDO abRole = validateABRoleExists(updateReqVO.getId());

        // 验证A角和B角用户不能相同
        Long aUserId = updateReqVO.getAUserId() != null ? updateReqVO.getAUserId() : abRole.getAUserId();
        Long bUserId = updateReqVO.getBUserId() != null ? updateReqVO.getBUserId() : abRole.getBUserId();
        if (aUserId.equals(bUserId)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.AB_ROLE_USER_SAME);
        }

        // 验证角色编号唯一性
        String roleCode = updateReqVO.getRoleCode() != null ? updateReqVO.getRoleCode() : abRole.getRoleCode();
        validateRoleCodeUnique(updateReqVO.getId(), roleCode);

        // 更新
        EmergencyABRoleDO updateObj = ABRoleConvert.INSTANCE.convert(updateReqVO);
        abRoleMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteABRole(Long id) {
        // 校验存在
        validateABRoleExists(id);

        // 删除
        abRoleMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void switchABRole(String roleCode, String reason) {
        // 查找A/B角配置
        EmergencyABRoleDO abRole = abRoleMapper.selectOne(
                new LambdaQueryWrapper<EmergencyABRoleDO>()
                        .eq(EmergencyABRoleDO::getRoleCode, roleCode)
                        .eq(EmergencyABRoleDO::getDeleted, false));
        if (abRole == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.AB_ROLE_NOT_EXISTS);
        }

        if (!abRole.getIsEnabled()) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.AB_ROLE_NOT_EXISTS, "A/B角配置已禁用");
        }

        // 切换A/B角（BR-ORG-004：A角因故不在时，B角顺序递补）
        String newActive = "A".equals(abRole.getCurrentActive()) ? "B" : "A";
        EmergencyABRoleDO updateObj = new EmergencyABRoleDO();
        updateObj.setId(abRole.getId());
        updateObj.setCurrentActive(newActive);
        abRoleMapper.updateById(updateObj);

        // 发送通知（BR-ORG-004：切换时自动发送通知给A角和B角，确保信息传递）
        // 注意：通知功能需要根据实际的通知服务接口实现
        // 这里暂时记录日志，实际实现中应该调用通知服务发送通知给A角和B角
        log.info("A/B角切换：roleCode={}, 从{}切换到{}, 原因={}", roleCode, abRole.getCurrentActive(), newActive, reason != null ? reason : "系统自动切换");
        // TODO: 实际实现中应调用通知服务发送通知给A角和B角用户

        log.info("A/B角切换成功：roleCode={}, 从{}切换到{}, 原因={}", roleCode, abRole.getCurrentActive(), newActive, reason);
    }

    private EmergencyABRoleDO validateABRoleExists(Long id) {
        EmergencyABRoleDO abRole = abRoleMapper.selectById(id);
        if (abRole == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.AB_ROLE_NOT_EXISTS);
        }
        return abRole;
    }

    private void validateRoleCodeUnique(Long id, String roleCode) {
        EmergencyABRoleDO existing = abRoleMapper.selectOne(
                new LambdaQueryWrapper<EmergencyABRoleDO>()
                        .eq(EmergencyABRoleDO::getRoleCode, roleCode)
                        .eq(EmergencyABRoleDO::getDeleted, false));
        if (existing != null && (id == null || !existing.getId().equals(id))) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.AB_ROLE_CODE_DUPLICATE);
        }
    }

    @Override
    public ABRoleRespVO getABRole(Long id) {
        EmergencyABRoleDO abRole = abRoleMapper.selectById(id);
        return ABRoleConvert.INSTANCE.convert(abRole);
    }

    @Override
    public PageResult<ABRoleRespVO> getABRolePage(ABRolePageReqVO pageReqVO) {
        PageResult<EmergencyABRoleDO> pageResult = abRoleMapper.selectPage(pageReqVO);
        return ABRoleConvert.INSTANCE.convertPage(pageResult);
    }

    @Override
    public List<ABRoleRespVO> getABRolesByDepartmentId(Long departmentId) {
        List<EmergencyABRoleDO> abRoles = abRoleMapper.selectList(
                new LambdaQueryWrapper<EmergencyABRoleDO>()
                        .eq(EmergencyABRoleDO::getDepartmentId, departmentId)
                        .eq(EmergencyABRoleDO::getDeleted, false)
                        .orderByAsc(EmergencyABRoleDO::getId));
        return ABRoleConvert.INSTANCE.convertList(abRoles);
    }
}

