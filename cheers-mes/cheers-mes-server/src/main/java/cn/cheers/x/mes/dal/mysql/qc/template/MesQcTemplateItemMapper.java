package cn.cheers.x.mes.dal.mysql.qc.template;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.mes.controller.admin.qc.template.vo.item.MesQcTemplateItemPageReqVO;
import cn.cheers.x.mes.dal.dataobject.qc.template.MesQcTemplateItemDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MES 质检方案-产品关联 Mapper
 *
 * 
 */
@Mapper
public interface MesQcTemplateItemMapper extends BaseMapperX<MesQcTemplateItemDO> {

    default PageResult<MesQcTemplateItemDO> selectPage(MesQcTemplateItemPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesQcTemplateItemDO>()
                .eqIfPresent(MesQcTemplateItemDO::getTemplateId, reqVO.getTemplateId())
                .orderByAsc(MesQcTemplateItemDO::getId));
    }

    default List<MesQcTemplateItemDO> selectListByTemplateId(Long templateId) {
        return selectList(MesQcTemplateItemDO::getTemplateId, templateId);
    }

    default List<MesQcTemplateItemDO> selectListByItemId(Long itemId) {
        return selectList(MesQcTemplateItemDO::getItemId, itemId);
    }

    default MesQcTemplateItemDO selectByTemplateIdAndItemId(Long templateId, Long itemId) {
        return selectOne(new LambdaQueryWrapperX<MesQcTemplateItemDO>()
                .eq(MesQcTemplateItemDO::getTemplateId, templateId)
                .eq(MesQcTemplateItemDO::getItemId, itemId));
    }

    default void deleteByTemplateId(Long templateId) {
        delete(new LambdaQueryWrapperX<MesQcTemplateItemDO>()
                .eq(MesQcTemplateItemDO::getTemplateId, templateId));
    }

}
