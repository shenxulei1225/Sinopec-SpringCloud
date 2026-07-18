package cn.cheers.x.mes.dal.mysql.dv.machinery;

import cn.hutool.core.collection.CollUtil;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.mes.controller.admin.dv.machinery.vo.MesDvMachineryPageReqVO;
import cn.cheers.x.mes.dal.dataobject.dv.machinery.MesDvMachineryDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 设备台账 Mapper
 *
 * 
 */
@Mapper
public interface MesDvMachineryMapper extends BaseMapperX<MesDvMachineryDO> {

    default PageResult<MesDvMachineryDO> selectPage(MesDvMachineryPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesDvMachineryDO>()
                .likeIfPresent(MesDvMachineryDO::getCode, reqVO.getCode())
                .likeIfPresent(MesDvMachineryDO::getName, reqVO.getName())
                .likeIfPresent(MesDvMachineryDO::getBrand, reqVO.getBrand())
                .eqIfPresent(MesDvMachineryDO::getMachineryTypeId, reqVO.getMachineryTypeId())
                .inIfPresent(MesDvMachineryDO::getMachineryTypeId, reqVO.getMachineryTypeIds())
                .eqIfPresent(MesDvMachineryDO::getWorkshopId, reqVO.getWorkshopId())
                .eqIfPresent(MesDvMachineryDO::getStatus, reqVO.getStatus())
                .orderByDesc(MesDvMachineryDO::getId));
    }

    default MesDvMachineryDO selectByCode(String code) {
        return selectOne(MesDvMachineryDO::getCode, code);
    }

    default Long selectCountByMachineryTypeId(Long machineryTypeId) {
        return selectCount(MesDvMachineryDO::getMachineryTypeId, machineryTypeId);
    }

}
