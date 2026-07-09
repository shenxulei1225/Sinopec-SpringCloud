package cn.cheers.x.module.dynamicbusiness.dal.mysql.dynamictable;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.dynamictable.DynamicTableDO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 动态表配置 Mapper
 * 
 * <p>动态表是按 entityTypeCode 创建和管理的，一个 EntityType 对应一个动态表。
 * 多个 Model 可以共享同一个 EntityType 的动态表。</p>
 * 
 * <p>查询动态表的推荐方式：
 * <ul>
 *   <li>已知 entityTypeCode：直接使用 {@link #selectByEntityTypeCode(String)}</li>
 *   <li>已知 modelId：通过 DynamicTableService.getDynamicTableByModelId(modelId)，
 *       内部会先查询 Model 获取 entityTypeCode，再查询动态表</li>
 * </ul>
 * </p>
 * 
 * @author 基础服务模块
 */
@Mapper
public interface DynamicTableMapper extends BaseMapperX<DynamicTableDO> {
    
    /**
     * 根据业务类型编码查询动态表列表
     * 
     * <p>这是查询动态表的主要方法。一个 EntityType 通常只有一个动态表。</p>
     * 
     * @param entityTypeCode 业务类型编码
     * @return 动态表列表
     */
    default List<DynamicTableDO> selectByEntityTypeCode(String entityTypeCode) {
        return selectList(DynamicTableDO::getEntityTypeCode, entityTypeCode);
    }
    
    /**
     * 根据表名查询动态表
     * 
     * @param tableName 物理表名
     * @return 动态表配置，未找到时返回 null
     */
    default DynamicTableDO selectByTableName(String tableName) {
        return selectOne(DynamicTableDO::getTableName, tableName);
    }
    
    /**
     * 查询所有活跃的动态表
     * 
     * @return 活跃状态的动态表列表
     */
    default List<DynamicTableDO> selectAllActive() {
        return selectList(DynamicTableDO::getStatus, 1);
    }
}
