package cn.iocoder.yudao.module.inspection.inspection_content.dal.mysql.item;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.inspection.inspection_content.dal.dataobject.item.InspectionItemDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 巡检项 Mapper。
 */
@Mapper
public interface InspectionItemMapper extends BaseMapperX<InspectionItemDO> {

    /**
     * 根据巡检项编码查询。
     */
    default InspectionItemDO selectByItemCode(String itemCode) {
        return selectOne(InspectionItemDO::getItemCode, itemCode);
    }

    /**
     * 根据业务实体类型和分类ID查询。
     */
    default List<InspectionItemDO> selectListBySourceTypeAndCategoryId(String sourceType, Long categoryId) {
        return selectList(new LambdaQueryWrapperX<InspectionItemDO>()
                .eq(InspectionItemDO::getSourceType, sourceType)
                .eqIfPresent(InspectionItemDO::getCategoryId, categoryId)
                .eq(InspectionItemDO::getStatus, "enabled")
                .orderByAsc(InspectionItemDO::getId));
    }

    /**
     * 根据业务实体类型、分类ID和型号查询（精确匹配）。
     */
    default List<InspectionItemDO> selectListBySourceTypeAndCategoryIdAndObjectModel(String sourceType, Long categoryId, String objectModel) {
        return selectList(new LambdaQueryWrapperX<InspectionItemDO>()
                .eq(InspectionItemDO::getSourceType, sourceType)
                .eqIfPresent(InspectionItemDO::getCategoryId, categoryId)
                .eqIfPresent(InspectionItemDO::getObjectModel, objectModel)
                .eq(InspectionItemDO::getStatus, "enabled")
                .orderByAsc(InspectionItemDO::getId));
    }

    /**
     * 根据业务实体类型和型号查询（忽略分类ID）。
     */
    default List<InspectionItemDO> selectListBySourceTypeAndObjectModel(String sourceType, String objectModel) {
        return selectList(new LambdaQueryWrapperX<InspectionItemDO>()
                .eq(InspectionItemDO::getSourceType, sourceType)
                .eqIfPresent(InspectionItemDO::getObjectModel, objectModel)
                .eq(InspectionItemDO::getStatus, "enabled")
                .orderByAsc(InspectionItemDO::getId));
    }

    /**
     * 根据 ID 列表查询。
     */
    default List<InspectionItemDO> selectListByIds(List<Long> ids) {
        return selectList(new LambdaQueryWrapperX<InspectionItemDO>()
                .in(InspectionItemDO::getId, ids)
                .orderByAsc(InspectionItemDO::getId));
    }
}
