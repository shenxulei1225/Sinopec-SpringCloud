package cn.iocoder.yudao.module.emergency.dal.mysql.organization;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.emergency.controller.admin.organization.vo.abrole.ABRolePageReqVO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.organization.EmergencyABRoleDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 应急A/B角管理 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface EmergencyABRoleMapper extends BaseMapperX<EmergencyABRoleDO> {

    default PageResult<EmergencyABRoleDO> selectPage(ABRolePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<EmergencyABRoleDO>()
                .likeIfPresent(EmergencyABRoleDO::getRoleCode, reqVO.getRoleCode())
                .eqIfPresent(EmergencyABRoleDO::getDepartmentId, reqVO.getDepartmentId())
                .eqIfPresent(EmergencyABRoleDO::getAUserId, reqVO.getAUserId())
                .eqIfPresent(EmergencyABRoleDO::getBUserId, reqVO.getBUserId())
                .eqIfPresent(EmergencyABRoleDO::getCurrentActive, reqVO.getCurrentActive())
                .eqIfPresent(EmergencyABRoleDO::getIsEnabled, reqVO.getIsEnabled())
                .orderByDesc(EmergencyABRoleDO::getId));
    }
}
