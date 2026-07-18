package cn.cheers.x.inspection.inspection_content.dal.mysql.collection;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.inspection.inspection_content.dal.dataobject.collection.InspectionObjectCollectionDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 巡检对象集合 Mapper。
 */
@Mapper
public interface InspectionObjectCollectionMapper extends BaseMapperX<InspectionObjectCollectionDO> {

    /**
     * 根据集合编码查询。
     */
    default InspectionObjectCollectionDO selectByCollectionCode(String collectionCode) {
        return selectOne(InspectionObjectCollectionDO::getCollectionCode, collectionCode);
    }

    /**
     * 根据状态查询列表。
     */
    default java.util.List<InspectionObjectCollectionDO> selectListByStatus(String status) {
        return selectList(new LambdaQueryWrapperX<InspectionObjectCollectionDO>()
                .eqIfPresent(InspectionObjectCollectionDO::getStatus, status)
                .orderByDesc(InspectionObjectCollectionDO::getCreateTime));
    }
}
