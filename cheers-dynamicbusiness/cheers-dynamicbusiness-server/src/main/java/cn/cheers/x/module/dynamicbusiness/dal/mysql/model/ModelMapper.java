package cn.cheers.x.module.dynamicbusiness.dal.mysql.model;

import cn.cheers.x.framework.common.pojo.PageParam;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.framework.entitytype.EntityTypeScopeContext;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 业务模型 Mapper
 *
 * @author yudao
 */
@Mapper
public interface ModelMapper extends BaseMapperX<ModelDO> {

        /**
         * 根据业务类型编码查询模型列表
         */
        default List<ModelDO> selectByEntityTypeCode(String entityTypeCode) {
                return selectList(new LambdaQueryWrapperX<ModelDO>()
                        .eq(ModelDO::getEntityTypeCode, entityTypeCode)
                        .eq(ModelDO::getStatus, 1)
                        .orderByAsc(ModelDO::getSort)
                        .orderByDesc(ModelDO::getCreateTime));
        }

        /**
         * 根据ID和业务类型编码查询模型
         */
        default ModelDO selectByIdAndEntityTypeCode(Long id, String entityTypeCode) {
                return selectOne(new LambdaQueryWrapperX<ModelDO>()
                        .eq(ModelDO::getId, id)
                        .eq(ModelDO::getEntityTypeCode, entityTypeCode));
        }

        /**
         * 根据编码查询模型
         */
        default ModelDO selectByCode(String code) {
                return selectOne(new LambdaQueryWrapperX<ModelDO>()
                        .eq(ModelDO::getCode, code));
        }

        /**
         * 根据名称 + 业务类型查询模型（同一租户、同业务类型内唯一）
         */
        default ModelDO selectByNameAndEntityTypeCode(String name, String entityTypeCode) {
                return selectOne(new LambdaQueryWrapperX<ModelDO>()
                        .eq(ModelDO::getName, name)
                        .eq(ModelDO::getEntityTypeCode, entityTypeCode));
        }

        /**
         * 搜索模型（按名称、描述）- 单业务类型（entityTypeCode 必填）
         */
        default List<ModelDO> searchLikeInEntityType(String keyword, String entityTypeCode) {
                return selectList(new LambdaQueryWrapperX<ModelDO>()
                        .eq(ModelDO::getEntityTypeCode, entityTypeCode)
                        .and(StringUtils.isNotBlank(keyword), q -> q.like(ModelDO::getName, keyword)
                                .or().like(ModelDO::getDescription, keyword))
                        .orderByAsc(ModelDO::getSort)
                        .orderByDesc(ModelDO::getCreateTime));
        }


        /**
         * 分页查询模型
         */
        default PageResult<ModelDO> selectPage(String entityTypeCode, String domain, String keyword, Integer status, Integer pageNo, Integer pageSize) {
                PageParam pageParam = new PageParam();
                pageParam.setPageNo(pageNo);
                pageParam.setPageSize(pageSize);
                // 「未划域」分组在服务端过滤，避免前端拉全量后本地筛选导致分页失真
                boolean unassignedDomain = EntityTypeScopeContext.isNoneDomainFilter(domain);
                boolean namedDomain = StringUtils.isNotBlank(domain) && !unassignedDomain;
                return selectPage(pageParam, new LambdaQueryWrapperX<ModelDO>()
                        .eq(StringUtils.isNotBlank(entityTypeCode), ModelDO::getEntityTypeCode, entityTypeCode)
                        .eq(namedDomain, ModelDO::getDomain, domain != null ? domain.trim() : null)
                        .and(unassignedDomain, q -> q.isNull(ModelDO::getDomain).or().eq(ModelDO::getDomain, ""))
                        .eq(status != null, ModelDO::getStatus, status)
                        .and(StringUtils.isNotBlank(keyword), q -> q.like(ModelDO::getName, keyword)
                                .or().like(ModelDO::getDescription, keyword))
                        .orderByAsc(ModelDO::getSort)
                        .orderByDesc(ModelDO::getCreateTime));
        }


        /**
         * 查询业务类型下当前最大排序值
         */
        default Integer selectMaxSortByEntityTypeCode(String entityTypeCode) {
                ModelDO one = selectOne(new LambdaQueryWrapperX<ModelDO>()
                        .eq(ModelDO::getEntityTypeCode, entityTypeCode)
                        .orderByDesc(ModelDO::getSort)
                        .last("LIMIT 1"));
                return one != null && one.getSort() != null ? one.getSort() : 0;
        }

        /**
         * 统计业务类型下的模型数量
         */
        default Long selectCountByEntityTypeCode(String entityTypeCode) {
                return selectCount(new LambdaQueryWrapperX<ModelDO>()
                        .eq(ModelDO::getEntityTypeCode, entityTypeCode));
        }

        /**
         * 按 ID 查询型号（含已软删），仅用于判定「不存在 / 已删除」。
         * <p>变更模型业务禁止对已删型号做迁移；须用原生 SQL，因 {@code @TableLogic} 会挡住 Wrapper。</p>
         */
        @Select("SELECT * FROM dynamic_model WHERE id = #{id}")
        ModelDO selectByIdIncludingDeleted(@Param("id") Long id);
}

