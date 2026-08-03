package cn.cheers.x.module.dynamicbusiness.dal.mysql.category;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryCategoryRelationDO;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 分类—分类跨种类关联 Mapper（物理表经 Tenant 路由为 *_t{tenantId}）。
 */
@Mapper
public interface CategoryCategoryRelationMapper extends BaseMapperX<CategoryCategoryRelationDO> {

    default CategoryCategoryRelationDO selectActive(Long hostCategoryId, Long memberCategoryId,
                                                    String hostCategoryTypeCode, String memberCategoryTypeCode) {
        return selectOne(new LambdaQueryWrapperX<CategoryCategoryRelationDO>()
                .eq(CategoryCategoryRelationDO::getHostCategoryId, hostCategoryId)
                .eq(CategoryCategoryRelationDO::getMemberCategoryId, memberCategoryId)
                .eq(CategoryCategoryRelationDO::getHostCategoryTypeCode, hostCategoryTypeCode)
                .eq(CategoryCategoryRelationDO::getMemberCategoryTypeCode, memberCategoryTypeCode)
                .eq(CategoryCategoryRelationDO::getDeleted, false));
    }

    default List<CategoryCategoryRelationDO> selectByHost(Long hostCategoryId, String hostCategoryTypeCode,
                                                          String memberCategoryTypeCode) {
        if (hostCategoryId == null) {
            return Collections.emptyList();
        }
        return selectByHosts(List.of(hostCategoryId), hostCategoryTypeCode, memberCategoryTypeCode);
    }

    /**
     * 按多个宿主 id 查关联（用于「点上级含子孙宿主」浏览汇总）。
     */
    default List<CategoryCategoryRelationDO> selectByHosts(Collection<Long> hostCategoryIds,
                                                           String hostCategoryTypeCode,
                                                           String memberCategoryTypeCode) {
        if (hostCategoryIds == null || hostCategoryIds.isEmpty()) {
            return Collections.emptyList();
        }
        return selectList(new LambdaQueryWrapperX<CategoryCategoryRelationDO>()
                .in(CategoryCategoryRelationDO::getHostCategoryId, hostCategoryIds)
                .eq(CategoryCategoryRelationDO::getHostCategoryTypeCode, hostCategoryTypeCode)
                .eq(CategoryCategoryRelationDO::getMemberCategoryTypeCode, memberCategoryTypeCode)
                .eq(CategoryCategoryRelationDO::getDeleted, false)
                .orderByAsc(CategoryCategoryRelationDO::getSort)
                .orderByAsc(CategoryCategoryRelationDO::getId));
    }

    default List<CategoryCategoryRelationDO> selectByMember(Long memberCategoryId, String memberCategoryTypeCode,
                                                            String hostCategoryTypeCode) {
        LambdaQueryWrapperX<CategoryCategoryRelationDO> q = new LambdaQueryWrapperX<CategoryCategoryRelationDO>()
                .eq(CategoryCategoryRelationDO::getMemberCategoryId, memberCategoryId)
                .eq(CategoryCategoryRelationDO::getMemberCategoryTypeCode, memberCategoryTypeCode)
                .eq(CategoryCategoryRelationDO::getDeleted, false)
                .orderByAsc(CategoryCategoryRelationDO::getSort)
                .orderByAsc(CategoryCategoryRelationDO::getId);
        if (hostCategoryTypeCode != null && !hostCategoryTypeCode.isBlank()) {
            q.eq(CategoryCategoryRelationDO::getHostCategoryTypeCode, hostCategoryTypeCode);
        }
        return selectList(q);
    }

    @Update("""
            UPDATE dynamic_category_category_relation
            SET deleted = FALSE,
                sort = #{sort},
                update_time = CURRENT_TIMESTAMP
            WHERE host_category_id = #{hostCategoryId}
              AND member_category_id = #{memberCategoryId}
              AND host_category_type_code = #{hostCategoryTypeCode}
              AND member_category_type_code = #{memberCategoryTypeCode}
              AND deleted = TRUE
            """)
    int restoreDeleted(@Param("hostCategoryId") Long hostCategoryId,
                       @Param("memberCategoryId") Long memberCategoryId,
                       @Param("hostCategoryTypeCode") String hostCategoryTypeCode,
                       @Param("memberCategoryTypeCode") String memberCategoryTypeCode,
                       @Param("sort") Integer sort);

    default int softDelete(Long hostCategoryId, Long memberCategoryId,
                           String hostCategoryTypeCode, String memberCategoryTypeCode) {
        return update(null, new LambdaUpdateWrapper<CategoryCategoryRelationDO>()
                .eq(CategoryCategoryRelationDO::getHostCategoryId, hostCategoryId)
                .eq(CategoryCategoryRelationDO::getMemberCategoryId, memberCategoryId)
                .eq(CategoryCategoryRelationDO::getHostCategoryTypeCode, hostCategoryTypeCode)
                .eq(CategoryCategoryRelationDO::getMemberCategoryTypeCode, memberCategoryTypeCode)
                .eq(CategoryCategoryRelationDO::getDeleted, false)
                .set(CategoryCategoryRelationDO::getDeleted, true));
    }

    default List<CategoryCategoryRelationDO> selectByHostAndMembers(Long hostCategoryId,
                                                                    Collection<Long> memberCategoryIds,
                                                                    String hostCategoryTypeCode,
                                                                    String memberCategoryTypeCode) {
        if (hostCategoryId == null || memberCategoryIds == null || memberCategoryIds.isEmpty()) {
            return Collections.emptyList();
        }
        return selectList(new LambdaQueryWrapperX<CategoryCategoryRelationDO>()
                .eq(CategoryCategoryRelationDO::getHostCategoryId, hostCategoryId)
                .in(CategoryCategoryRelationDO::getMemberCategoryId, memberCategoryIds)
                .eq(CategoryCategoryRelationDO::getHostCategoryTypeCode, hostCategoryTypeCode)
                .eq(CategoryCategoryRelationDO::getMemberCategoryTypeCode, memberCategoryTypeCode)
                .eq(CategoryCategoryRelationDO::getDeleted, false));
    }

    /**
     * 同一宿主下，批量软删多个成员关联（用于「点成员上级含子孙解绑」）。
     */
    default int softDeleteByHostAndMembers(Long hostCategoryId, Collection<Long> memberCategoryIds,
                                           String hostCategoryTypeCode, String memberCategoryTypeCode) {
        if (hostCategoryId == null || memberCategoryIds == null || memberCategoryIds.isEmpty()) {
            return 0;
        }
        return update(null, new LambdaUpdateWrapper<CategoryCategoryRelationDO>()
                .eq(CategoryCategoryRelationDO::getHostCategoryId, hostCategoryId)
                .in(CategoryCategoryRelationDO::getMemberCategoryId, memberCategoryIds)
                .eq(CategoryCategoryRelationDO::getHostCategoryTypeCode, hostCategoryTypeCode)
                .eq(CategoryCategoryRelationDO::getMemberCategoryTypeCode, memberCategoryTypeCode)
                .eq(CategoryCategoryRelationDO::getDeleted, false)
                .set(CategoryCategoryRelationDO::getDeleted, true));
    }

    default Integer maxSort(Long hostCategoryId, String hostCategoryTypeCode, String memberCategoryTypeCode) {
        List<CategoryCategoryRelationDO> rows = selectList(new LambdaQueryWrapperX<CategoryCategoryRelationDO>()
                .eq(CategoryCategoryRelationDO::getHostCategoryId, hostCategoryId)
                .eq(CategoryCategoryRelationDO::getHostCategoryTypeCode, hostCategoryTypeCode)
                .eq(CategoryCategoryRelationDO::getMemberCategoryTypeCode, memberCategoryTypeCode)
                .eq(CategoryCategoryRelationDO::getDeleted, false)
                .orderByDesc(CategoryCategoryRelationDO::getSort)
                .last("LIMIT 1"));
        if (rows == null || rows.isEmpty() || rows.get(0).getSort() == null) {
            return 0;
        }
        return rows.get(0).getSort();
    }
}
