package cn.cheers.x.module.dynamicbusiness.dal.mysql.relation;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.relation.RelationFieldLibraryDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 关联字段库 Mapper
 * 
 * @author yudao
 */
@Mapper
public interface RelationFieldLibraryMapper extends BaseMapperX<RelationFieldLibraryDO> {

    /**
     * 根据字段编码查询
     * 
     * @param fieldCode 字段编码
     * @return 关联字段
     */
    default RelationFieldLibraryDO selectByFieldCode(String fieldCode) {
        return selectOne(new LambdaQueryWrapperX<RelationFieldLibraryDO>()
                .eq(RelationFieldLibraryDO::getFieldCode, fieldCode));
    }

    /**
     * 根据关联业务类型查询字段列表
     *
     * @param refBusinessType 关联业务类型编码
     * @return 字段列表
     */
    default List<RelationFieldLibraryDO> selectByRefBusinessType(String refBusinessType) {
        return selectList(new LambdaQueryWrapperX<RelationFieldLibraryDO>()
                .eq(RelationFieldLibraryDO::getRefBusinessType, refBusinessType)
                .orderByDesc(RelationFieldLibraryDO::getUsageCount)
                .orderByAsc(RelationFieldLibraryDO::getFieldName));
    }

    /**
     * 查询所有字段列表
     * 
     * @return 字段列表
     */
    default List<RelationFieldLibraryDO> selectAll() {
        return selectList(new LambdaQueryWrapperX<RelationFieldLibraryDO>()
                .orderByDesc(RelationFieldLibraryDO::getUsageCount)
                .orderByAsc(RelationFieldLibraryDO::getFieldName));
    }

    /**
     * 查询系统预置字段列表
     * 
     * @return 系统预置字段列表
     */
    default List<RelationFieldLibraryDO> selectSystemFields() {
        return selectList(new LambdaQueryWrapperX<RelationFieldLibraryDO>()
                .eq(RelationFieldLibraryDO::getIsSystem, true)
                .orderByAsc(RelationFieldLibraryDO::getFieldName));
    }

    /**
     * 分页查询
     * 
     * @param targetBusinessType 目标业务类型（可选）
     * @param keyword 关键字（可选，搜索名称和编码）
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    default PageResult<RelationFieldLibraryDO> selectPage(String refBusinessType, String keyword,
                                                           Integer pageNo, Integer pageSize) {
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(pageNo);
        pageParam.setPageSize(pageSize);
        return selectPage(pageParam, new LambdaQueryWrapperX<RelationFieldLibraryDO>()
                .eqIfPresent(RelationFieldLibraryDO::getRefBusinessType, refBusinessType)
                .and(StringUtils.isNotBlank(keyword), q -> q
                        .like(RelationFieldLibraryDO::getFieldName, keyword)
                        .or().like(RelationFieldLibraryDO::getFieldCode, keyword)
                        .or().like(RelationFieldLibraryDO::getDescription, keyword))
                .orderByDesc(RelationFieldLibraryDO::getUsageCount)
                .orderByAsc(RelationFieldLibraryDO::getFieldName));
    }

    /**
     * 检查字段编码是否存在
     * 
     * @param fieldCode 字段编码
     * @param excludeId 排除的ID（用于更新时排除自身）
     * @return 是否存在
     */
    default boolean existsByFieldCode(String fieldCode, Long excludeId) {
        return selectCount(new LambdaQueryWrapperX<RelationFieldLibraryDO>()
                .eq(RelationFieldLibraryDO::getFieldCode, fieldCode)
                .neIfPresent(RelationFieldLibraryDO::getId, excludeId)) > 0;
    }

    /**
     * 增加使用次数
     * 
     * @param id 字段ID
     */
    default void incrementUsageCount(Long id) {
        RelationFieldLibraryDO field = selectById(id);
        if (field != null) {
            field.setUsageCount(field.getUsageCount() == null ? 1 : field.getUsageCount() + 1);
            updateById(field);
        }
    }

    /**
     * 减少使用次数
     * 
     * @param id 字段ID
     */
    default void decrementUsageCount(Long id) {
        RelationFieldLibraryDO field = selectById(id);
        if (field != null && field.getUsageCount() != null && field.getUsageCount() > 0) {
            field.setUsageCount(field.getUsageCount() - 1);
            updateById(field);
        }
    }
}
