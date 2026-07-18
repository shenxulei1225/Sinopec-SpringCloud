package cn.cheers.x.mes.dal.mysql.qc.defect;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.mes.controller.admin.qc.defect.vo.MesQcDefectPageReqVO;
import cn.cheers.x.mes.dal.dataobject.qc.defect.MesQcDefectDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MES 缺陷类型 Mapper
 *
 * 
 */
@Mapper
public interface MesQcDefectMapper extends BaseMapperX<MesQcDefectDO> {

    default PageResult<MesQcDefectDO> selectPage(MesQcDefectPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesQcDefectDO>()
                .likeIfPresent(MesQcDefectDO::getCode, reqVO.getCode())
                .likeIfPresent(MesQcDefectDO::getName, reqVO.getName())
                .eqIfPresent(MesQcDefectDO::getType, reqVO.getType())
                .eqIfPresent(MesQcDefectDO::getLevel, reqVO.getLevel())
                .orderByDesc(MesQcDefectDO::getId));
    }

    default MesQcDefectDO selectByCode(String code) {
        return selectOne(MesQcDefectDO::getCode, code);
    }

    default MesQcDefectDO selectByName(String name) {
        return selectOne(MesQcDefectDO::getName, name);
    }

    default List<MesQcDefectDO> selectList() {
        return selectList(new LambdaQueryWrapperX<MesQcDefectDO>()
                .orderByDesc(MesQcDefectDO::getId));
    }

}
