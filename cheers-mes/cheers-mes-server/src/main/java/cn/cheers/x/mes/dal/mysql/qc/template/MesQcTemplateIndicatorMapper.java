package cn.cheers.x.mes.dal.mysql.qc.template;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.mes.controller.admin.qc.template.vo.indicator.MesQcTemplateIndicatorPageReqVO;
import cn.cheers.x.mes.dal.dataobject.qc.template.MesQcTemplateIndicatorDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MES 质检方案-检测指标项 Mapper
 *
 * 
 */
@Mapper
public interface MesQcTemplateIndicatorMapper extends BaseMapperX<MesQcTemplateIndicatorDO> {

    default PageResult<MesQcTemplateIndicatorDO> selectPage(MesQcTemplateIndicatorPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesQcTemplateIndicatorDO>()
                .eqIfPresent(MesQcTemplateIndicatorDO::getTemplateId, reqVO.getTemplateId())
                .orderByAsc(MesQcTemplateIndicatorDO::getId));
    }

    default List<MesQcTemplateIndicatorDO> selectListByTemplateId(Long templateId) {
        return selectList(MesQcTemplateIndicatorDO::getTemplateId, templateId);
    }

    default Long selectCountByUnitMeasureId(Long unitMeasureId) {
        return selectCount(MesQcTemplateIndicatorDO::getUnitMeasureId, unitMeasureId);
    }

    default void deleteByTemplateId(Long templateId) {
        delete(new LambdaQueryWrapperX<MesQcTemplateIndicatorDO>()
                .eq(MesQcTemplateIndicatorDO::getTemplateId, templateId));
    }

}
