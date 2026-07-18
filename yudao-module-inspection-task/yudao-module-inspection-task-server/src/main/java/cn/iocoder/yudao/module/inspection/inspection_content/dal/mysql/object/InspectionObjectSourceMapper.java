package cn.iocoder.yudao.module.inspection.inspection_content.dal.mysql.object;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.inspection.inspection_content.dal.dataobject.object.InspectionObjectSourceDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 巡检对象来源 Mapper。
 */
@Mapper
public interface InspectionObjectSourceMapper extends BaseMapperX<InspectionObjectSourceDO> {

    /**
     * 根据来源编码查询。
     */
    default InspectionObjectSourceDO selectBySourceCode(String sourceCode) {
        return selectOne(InspectionObjectSourceDO::getSourceCode, sourceCode);
    }

    /**
     * 根据状态查询列表。
     */
    default List<InspectionObjectSourceDO> selectListByStatus(String status) {
        return selectList(new LambdaQueryWrapperX<InspectionObjectSourceDO>()
                .eqIfPresent(InspectionObjectSourceDO::getStatus, status)
                .orderByAsc(InspectionObjectSourceDO::getSortNo));
    }
}
