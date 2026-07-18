package cn.cheers.x.module.dynamicbusiness.dal.mysql.field;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.framework.common.pojo.PageParam;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FieldMapper extends BaseMapperX<FieldDO> {

    default FieldDO selectByName(String name, Long tenantId) {
        return selectOne(new LambdaQueryWrapperX<FieldDO>()
                .eq(FieldDO::getName, name)
                .eq(FieldDO::getTenantId, tenantId)
                .eq(FieldDO::getDeleted, false));
    }

    /**
     * 根据 code 查询字段（code 全局唯一，不需要 tenantId）
     */
    default FieldDO selectByCode(String code) {
        return selectOne(new LambdaQueryWrapperX<FieldDO>()
                .eq(FieldDO::getCode, code)
                .eq(FieldDO::getDeleted, false));
    }

    default List<FieldDO> search(String keyword, String type, String source, Integer status) {
        LambdaQueryWrapperX<FieldDO> wrapper = new LambdaQueryWrapperX<>();
        // 关键字搜索：name 或 description 包含关键字（OR 条件）
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(FieldDO::getName, keyword)
                    .or()
                    .like(FieldDO::getDescription, keyword));
        }
        wrapper.eqIfPresent(FieldDO::getType, type)
                .eqIfPresent(FieldDO::getSource, source)
                .eqIfPresent(FieldDO::getStatus, status)
                .eq(FieldDO::getDeleted, false)
                .orderByAsc(FieldDO::getCreateTime)
                .orderByAsc(FieldDO::getId);
        return selectList(wrapper);
    }

    default PageResult<FieldDO> selectPage(PageParam pageParam, String keyword, String type, String source, Integer status) {
        return selectPage(pageParam, new LambdaQueryWrapperX<FieldDO>()
                .likeIfPresent(FieldDO::getName, keyword)
                .likeIfPresent(FieldDO::getDescription, keyword)
                .eqIfPresent(FieldDO::getType, type)
                .eqIfPresent(FieldDO::getSource, source)
                .eqIfPresent(FieldDO::getStatus, status)
                .eq(FieldDO::getDeleted, false)
                .orderByAsc(FieldDO::getCreateTime)
                .orderByAsc(FieldDO::getId));
    }

    default Long selectCountByUnit(String unitCode, Long tenantId) {
        return selectCount(new LambdaQueryWrapperX<FieldDO>()
                .eq(FieldDO::getUnit, unitCode)
                .eq(FieldDO::getTenantId, tenantId)
                .eq(FieldDO::getDeleted, false));
    }
}

