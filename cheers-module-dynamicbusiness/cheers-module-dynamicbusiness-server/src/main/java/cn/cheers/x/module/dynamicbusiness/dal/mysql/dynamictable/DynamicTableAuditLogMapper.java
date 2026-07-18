package cn.cheers.x.module.dynamicbusiness.dal.mysql.dynamictable;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.dynamictable.DynamicTableAuditLogDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 动态表审计日志 Mapper
 * 
 * @author 基础服务模块
 */
@Mapper
public interface DynamicTableAuditLogMapper extends BaseMapperX<DynamicTableAuditLogDO> {
    
    default List<DynamicTableAuditLogDO> selectByDynamicTableId(Long dynamicTableId) {
        return selectList(new LambdaQueryWrapper<DynamicTableAuditLogDO>()
                .eq(DynamicTableAuditLogDO::getDynamicTableId, dynamicTableId)
                .orderByDesc(DynamicTableAuditLogDO::getOperationTime));
    }
}
