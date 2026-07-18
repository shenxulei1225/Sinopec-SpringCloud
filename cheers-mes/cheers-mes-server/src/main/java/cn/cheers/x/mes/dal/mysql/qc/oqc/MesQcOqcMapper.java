package cn.cheers.x.mes.dal.mysql.qc.oqc;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.mes.controller.admin.qc.oqc.vo.MesQcOqcPageReqVO;
import cn.cheers.x.mes.dal.dataobject.qc.oqc.MesQcOqcDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 出货检验单（OQC） Mapper
 *
 * 
 */
@Mapper
public interface MesQcOqcMapper extends BaseMapperX<MesQcOqcDO> {

    default PageResult<MesQcOqcDO> selectPage(MesQcOqcPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesQcOqcDO>()
                .likeIfPresent(MesQcOqcDO::getCode, reqVO.getCode())
                .eqIfPresent(MesQcOqcDO::getClientId, reqVO.getClientId())
                .likeIfPresent(MesQcOqcDO::getBatchCode, reqVO.getBatchCode())
                .eqIfPresent(MesQcOqcDO::getItemId, reqVO.getItemId())
                .eqIfPresent(MesQcOqcDO::getCheckResult, reqVO.getCheckResult())
                .orderByDesc(MesQcOqcDO::getId));
    }

    default MesQcOqcDO selectByCode(String code) {
        return selectOne(MesQcOqcDO::getCode, code);
    }

}
