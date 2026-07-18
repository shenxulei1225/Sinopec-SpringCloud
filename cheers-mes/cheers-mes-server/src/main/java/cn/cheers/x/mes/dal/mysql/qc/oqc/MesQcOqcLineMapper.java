package cn.cheers.x.mes.dal.mysql.qc.oqc;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.mes.controller.admin.qc.oqc.vo.line.MesQcOqcLinePageReqVO;
import cn.cheers.x.mes.dal.dataobject.qc.oqc.MesQcOqcLineDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MES 出货检验单行 Mapper
 *
 * 
 */
@Mapper
public interface MesQcOqcLineMapper extends BaseMapperX<MesQcOqcLineDO> {

    default PageResult<MesQcOqcLineDO> selectPage(MesQcOqcLinePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesQcOqcLineDO>()
                .eqIfPresent(MesQcOqcLineDO::getOqcId, reqVO.getOqcId())
                .orderByAsc(MesQcOqcLineDO::getId));
    }

    default List<MesQcOqcLineDO> selectListByOqcId(Long oqcId) {
        return selectList(MesQcOqcLineDO::getOqcId, oqcId);
    }

    default void deleteByOqcId(Long oqcId) {
        delete(new LambdaQueryWrapperX<MesQcOqcLineDO>()
                .eq(MesQcOqcLineDO::getOqcId, oqcId));
    }

    default Long selectCountByUnitMeasureId(Long unitMeasureId) {
        return selectCount(MesQcOqcLineDO::getUnitMeasureId, unitMeasureId);
    }

}
