package cn.cheers.x.module.dynamicbusiness.dal.mysql.capability;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.capability.BusinessCapabilityDO;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 业务能力全集 Mapper。
 *
 * <p>说明：</p>
 * <ul>
 *   <li>面向 business_capability 表；</li>
 *   <li>所有查询以 entityTypeCode 为业务索引；</li>
 *   <li>逻辑删除由 MP 框架处理，这里不额外拼 deleted 条件，避免重复和歧义。</li>
 * </ul>
 */
@Mapper
public interface BusinessCapabilityMapper extends BaseMapperX<BusinessCapabilityDO> {

    /**
     * 按业务类型编码查询单条能力全集。
     *
     * @param entityTypeCode 业务类型编码（必填）
     * @return 对应能力全集；不存在返回 null
     */
    default BusinessCapabilityDO selectByEntityTypeCode(String entityTypeCode) {
        return selectOne(new LambdaQueryWrapperX<BusinessCapabilityDO>()
                .eq(BusinessCapabilityDO::getEntityTypeCode, entityTypeCode));
    }

    /**
     * 查询全部能力全集（按业务类型编码升序）。
     *
     * <p>用于能力列表页、后台核对等场景。</p>
     */
    default List<BusinessCapabilityDO> selectAllOrderByCode() {
        return selectAllOrderByCode(null);
    }

    /**
     * 按业务分类可选过滤（dynamic / system）。
     */
    default List<BusinessCapabilityDO> selectAllOrderByCode(String businessCategory) {
        LambdaQueryWrapperX<BusinessCapabilityDO> query = new LambdaQueryWrapperX<BusinessCapabilityDO>()
                .orderByAsc(BusinessCapabilityDO::getEntityTypeCode);
        if (StringUtils.hasText(businessCategory)) {
            query.eq(BusinessCapabilityDO::getBusinessCategory, businessCategory.trim());
        }
        return selectList(query);
    }
}
