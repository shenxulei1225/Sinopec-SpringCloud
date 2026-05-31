package cn.cheers.x.module.dynamicbusiness.dal.mysql.businesstype;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.businesstype.BusinessTypeDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 业务类型 Mapper
 * 
 * @author yudao
 */
@Mapper
public interface BusinessTypeMapper extends BaseMapperX<BusinessTypeDO> {

    /**
     * 根据编码查询业务类型
     */
    default BusinessTypeDO selectByCode(String code) {
        return selectOne(new LambdaQueryWrapperX<BusinessTypeDO>()
                .eq(BusinessTypeDO::getCode, code)
                .eq(BusinessTypeDO::getDeleted, false));
    }

    /**
     * 根据专用表名查询业务类型
     */
    default BusinessTypeDO selectByDedicatedTableName(String dedicatedTableName) {
        return selectOne(new LambdaQueryWrapperX<BusinessTypeDO>()
                .eq(BusinessTypeDO::getDedicatedTableName, dedicatedTableName)
                .eq(BusinessTypeDO::getDeleted, false));
    }

    /**
     * 查询所有启用的业务类型
     */
    default List<BusinessTypeDO> selectActiveList() {
        return selectList(new LambdaQueryWrapperX<BusinessTypeDO>()
                .eq(BusinessTypeDO::getStatus, BusinessTypeDO.STATUS_ACTIVE)
                .eq(BusinessTypeDO::getDeleted, false)
                .orderByAsc(BusinessTypeDO::getSort)
                .orderByDesc(BusinessTypeDO::getCreateTime));
    }

    /**
     * 根据状态查询业务类型列表
     */
    default List<BusinessTypeDO> selectByStatus(String status) {
        return selectList(new LambdaQueryWrapperX<BusinessTypeDO>()
                .eqIfPresent(BusinessTypeDO::getStatus, status)
                .eq(BusinessTypeDO::getDeleted, false)
                .orderByAsc(BusinessTypeDO::getSort)
                .orderByDesc(BusinessTypeDO::getCreateTime));
    }

    /**
     * 分页查询业务类型
     */
    default PageResult<BusinessTypeDO> selectPage(PageParam pageParam, String code, String name, String status) {
        return selectPage(pageParam, new LambdaQueryWrapperX<BusinessTypeDO>()
                .likeIfPresent(BusinessTypeDO::getCode, code)
                .likeIfPresent(BusinessTypeDO::getName, name)
                .eqIfPresent(BusinessTypeDO::getStatus, status)
                .eq(BusinessTypeDO::getDeleted, false)
                .orderByAsc(BusinessTypeDO::getSort)
                .orderByDesc(BusinessTypeDO::getCreateTime));
    }

    /**
     * 检查编码是否存在
     */
    default boolean existsByCode(String code) {
        return selectCount(new LambdaQueryWrapperX<BusinessTypeDO>()
                .eq(BusinessTypeDO::getCode, code)
                .eq(BusinessTypeDO::getDeleted, false)) > 0;
    }

    /**
     * 获取指定父级下的子类型数量
     */
    default Long selectCountByParentId(Long parentId) {
        return selectCount(new LambdaQueryWrapperX<BusinessTypeDO>()
                .eq(BusinessTypeDO::getParentId, parentId)
                .eq(BusinessTypeDO::getDeleted, false));
    }
    
    /**
     * 检查名称是否存在
     */
    default boolean existsByName(String name) {
        return selectCount(new LambdaQueryWrapperX<BusinessTypeDO>()
                .eq(BusinessTypeDO::getName, name)
                .eq(BusinessTypeDO::getDeleted, false)) > 0;
    }

    /**
     * 检查编码是否存在（排除指定ID）
     */
    default boolean existsByCodeExcludeId(String code, Long excludeId) {
        return selectCount(new LambdaQueryWrapperX<BusinessTypeDO>()
                .eq(BusinessTypeDO::getCode, code)
                .ne(BusinessTypeDO::getId, excludeId)
                .eq(BusinessTypeDO::getDeleted, false)) > 0;
    }

    /**
     * 查询所有未删除的业务类型
     */
    default List<BusinessTypeDO> selectAllList() {
        return selectList(new LambdaQueryWrapperX<BusinessTypeDO>()
                .eq(BusinessTypeDO::getDeleted, false)
                .orderByAsc(BusinessTypeDO::getSort)
                .orderByDesc(BusinessTypeDO::getCreateTime));
    }



}
