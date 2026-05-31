package cn.cheers.x.module.dynamicbusiness.dal.mysql.model;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import org.apache.ibatis.annotations.Mapper;
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
        default List<ModelDO> selectByBusinessTypeCode(String businessTypeCode) {
                return selectList(new LambdaQueryWrapperX<ModelDO>()
                        .eq(ModelDO::getBusinessTypeCode, businessTypeCode)
                        .eq(ModelDO::getStatus, 1)
                        .orderByAsc(ModelDO::getSort)
                        .orderByDesc(ModelDO::getCreateTime));
        }

        /**
         * 根据ID和业务类型编码查询模型
         */
        default ModelDO selectByIdAndBusinessTypeCode(Long id, String businessTypeCode) {
                return selectOne(new LambdaQueryWrapperX<ModelDO>()
                        .eq(ModelDO::getId, id)
                        .eq(ModelDO::getBusinessTypeCode, businessTypeCode));
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
        default ModelDO selectByNameAndBusinessTypeCode(String name, String businessTypeCode) {
                return selectOne(new LambdaQueryWrapperX<ModelDO>()
                        .eq(ModelDO::getName, name)
                        .eq(ModelDO::getBusinessTypeCode, businessTypeCode));
        }

        /**
         * 搜索模型（按名称、描述）- 单业务类型（businessTypeCode 必填）
         */
        default List<ModelDO> searchLikeInBusinessType(String keyword, String businessTypeCode) {
                return selectList(new LambdaQueryWrapperX<ModelDO>()
                        .eq(ModelDO::getBusinessTypeCode, businessTypeCode)
                        .and(StringUtils.isNotBlank(keyword), q -> q.like(ModelDO::getName, keyword)
                                .or().like(ModelDO::getDescription, keyword))
                        .orderByAsc(ModelDO::getSort)
                        .orderByDesc(ModelDO::getCreateTime));
        }


        /**
         * 分页查询模型
         */
        default PageResult<ModelDO> selectPage(String businessTypeCode, String keyword, Integer status, Integer pageNo, Integer pageSize) {
                PageParam pageParam = new PageParam();
                pageParam.setPageNo(pageNo);
                pageParam.setPageSize(pageSize);
                return selectPage(pageParam, new LambdaQueryWrapperX<ModelDO>()
                        .eq(StringUtils.isNotBlank(businessTypeCode), ModelDO::getBusinessTypeCode, businessTypeCode)
                        .eq(status != null, ModelDO::getStatus, status)
                        .and(StringUtils.isNotBlank(keyword), q -> q.like(ModelDO::getName, keyword)
                                .or().like(ModelDO::getDescription, keyword))
                        .orderByAsc(ModelDO::getSort)
                        .orderByDesc(ModelDO::getCreateTime));
        }


        /**
         * 查询业务类型下当前最大排序值
         */
        default Integer selectMaxSortByBusinessTypeCode(String businessTypeCode) {
                ModelDO one = selectOne(new LambdaQueryWrapperX<ModelDO>()
                        .eq(ModelDO::getBusinessTypeCode, businessTypeCode)
                        .orderByDesc(ModelDO::getSort)
                        .last("LIMIT 1"));
                return one != null && one.getSort() != null ? one.getSort() : 0;
        }

        /**
         * 统计业务类型下的模型数量
         */
        default Long selectCountByBusinessTypeCode(String businessTypeCode) {
                return selectCount(new LambdaQueryWrapperX<ModelDO>()
                        .eq(ModelDO::getBusinessTypeCode, businessTypeCode));
        }
}

