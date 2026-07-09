package cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 业务类型 Mapper
 * 
 * @author yudao
 */
@Mapper
public interface EntityTypeMapper extends BaseMapperX<EntityTypeDO> {

    /**
     * 根据编码查询业务类型
     */
    default EntityTypeDO selectByCode(String code) {
        return selectOne(new LambdaQueryWrapperX<EntityTypeDO>()
                .eq(EntityTypeDO::getCode, code)
                .eq(EntityTypeDO::getDeleted, false));
    }

    /**
     * 根据专用表名查询业务类型
     */
    default EntityTypeDO selectByDedicatedTableName(String dedicatedTableName) {
        return selectOne(new LambdaQueryWrapperX<EntityTypeDO>()
                .eq(EntityTypeDO::getDedicatedTableName, dedicatedTableName)
                .eq(EntityTypeDO::getDeleted, false));
    }

    /**
     * 查询所有启用的业务类型
     */
    default List<EntityTypeDO> selectActiveList() {
        return selectList(new LambdaQueryWrapperX<EntityTypeDO>()
                .eq(EntityTypeDO::getStatus, EntityTypeDO.STATUS_ACTIVE)
                .eq(EntityTypeDO::getDeleted, false)
                .orderByAsc(EntityTypeDO::getSort)
                .orderByDesc(EntityTypeDO::getCreateTime));
    }

    /**
     * 根据状态查询业务类型列表
     */
    default List<EntityTypeDO> selectByStatus(String status) {
        return selectList(new LambdaQueryWrapperX<EntityTypeDO>()
                .eqIfPresent(EntityTypeDO::getStatus, status)
                .eq(EntityTypeDO::getDeleted, false)
                .orderByAsc(EntityTypeDO::getSort)
                .orderByDesc(EntityTypeDO::getCreateTime));
    }

    /**
     * 分页查询业务类型
     */
    default PageResult<EntityTypeDO> selectPage(PageParam pageParam, String code, String name, String status) {
        return selectPage(pageParam, new LambdaQueryWrapperX<EntityTypeDO>()
                .likeIfPresent(EntityTypeDO::getCode, code)
                .likeIfPresent(EntityTypeDO::getName, name)
                .eqIfPresent(EntityTypeDO::getStatus, status)
                .eq(EntityTypeDO::getDeleted, false)
                .orderByAsc(EntityTypeDO::getSort)
                .orderByDesc(EntityTypeDO::getCreateTime));
    }

    /**
     * 检查编码是否存在
     */
    default boolean existsByCode(String code) {
        return selectCount(new LambdaQueryWrapperX<EntityTypeDO>()
                .eq(EntityTypeDO::getCode, code)
                .eq(EntityTypeDO::getDeleted, false)) > 0;
    }

    /**
     * 获取指定父级下的子类型数量
     */
    default Long selectCountByParentId(Long parentId) {
        return selectCount(new LambdaQueryWrapperX<EntityTypeDO>()
                .eq(EntityTypeDO::getParentId, parentId)
                .eq(EntityTypeDO::getDeleted, false));
    }
    
    /**
     * 检查名称是否存在
     */
    default boolean existsByName(String name) {
        return selectCount(new LambdaQueryWrapperX<EntityTypeDO>()
                .eq(EntityTypeDO::getName, name)
                .eq(EntityTypeDO::getDeleted, false)) > 0;
    }

    /**
     * 检查编码是否存在（排除指定ID）
     */
    default boolean existsByCodeExcludeId(String code, Long excludeId) {
        return selectCount(new LambdaQueryWrapperX<EntityTypeDO>()
                .eq(EntityTypeDO::getCode, code)
                .ne(EntityTypeDO::getId, excludeId)
                .eq(EntityTypeDO::getDeleted, false)) > 0;
    }

    /**
     * 查询所有未删除的业务类型
     */
    default List<EntityTypeDO> selectAllList() {
        return selectList(new LambdaQueryWrapperX<EntityTypeDO>()
                .eq(EntityTypeDO::getDeleted, false)
                .orderByAsc(EntityTypeDO::getSort)
                .orderByDesc(EntityTypeDO::getCreateTime));
    }



}
