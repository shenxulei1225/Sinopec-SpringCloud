package cn.cheers.x.module.dynamicbusiness.dal.mysql.dynamictable;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.dynamictable.DynamicTableColumnDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 动态表字段配置 Mapper
 * 
 * @author 基础服务模块
 */
@Mapper
public interface DynamicTableColumnMapper extends BaseMapperX<DynamicTableColumnDO> {
    
    default List<DynamicTableColumnDO> selectByDynamicTableId(Long dynamicTableId) {
        return selectList(new LambdaQueryWrapper<DynamicTableColumnDO>()
                .eq(DynamicTableColumnDO::getDynamicTableId, dynamicTableId)
                .eq(DynamicTableColumnDO::getStatus, 1)
                .orderByAsc(DynamicTableColumnDO::getSortOrder));
    }
    
    default DynamicTableColumnDO selectByDynamicTableIdAndFieldId(Long dynamicTableId, Long fieldId) {
        return selectOne(new LambdaQueryWrapper<DynamicTableColumnDO>()
                .eq(DynamicTableColumnDO::getDynamicTableId, dynamicTableId)
                .eq(DynamicTableColumnDO::getFieldId, fieldId));
    }
    
    default void deleteByDynamicTableId(Long dynamicTableId) {
        delete(new LambdaQueryWrapper<DynamicTableColumnDO>()
                .eq(DynamicTableColumnDO::getDynamicTableId, dynamicTableId));
    }
}
