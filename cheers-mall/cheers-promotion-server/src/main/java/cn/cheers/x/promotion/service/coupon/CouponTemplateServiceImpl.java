package cn.cheers.x.promotion.service.coupon;

import cn.cheers.x.framework.common.enums.CommonStatusEnum;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.product.api.category.ProductCategoryApi;
import cn.cheers.x.product.api.spu.ProductSpuApi;
import cn.cheers.x.promotion.controller.admin.coupon.vo.template.CouponTemplateCreateReqVO;
import cn.cheers.x.promotion.controller.admin.coupon.vo.template.CouponTemplatePageReqVO;
import cn.cheers.x.promotion.controller.admin.coupon.vo.template.CouponTemplateUpdateReqVO;
import cn.cheers.x.promotion.convert.coupon.CouponTemplateConvert;
import cn.cheers.x.promotion.dal.dataobject.coupon.CouponTemplateDO;
import cn.cheers.x.promotion.dal.mysql.coupon.CouponTemplateMapper;
import cn.cheers.x.promotion.enums.common.PromotionProductScopeEnum;
import cn.cheers.x.promotion.enums.coupon.CouponTakeTypeEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.promotion.enums.ErrorCodeConstants.*;

/**
 * 优惠劵模板 Service 实现类
 *
 * 
 */
@Service
@Validated
public class CouponTemplateServiceImpl implements CouponTemplateService {

    @Resource
    private CouponTemplateMapper couponTemplateMapper;

    @Resource
    private ProductCategoryApi productCategoryApi;
    @Resource
    private ProductSpuApi productSpuApi;

    @Override
    public boolean isTakeLimitCountUnlimited(Integer takeLimitCount) {
        return CouponTemplateDO.TAKE_LIMIT_COUNT_MAX.equals(takeLimitCount);
    }

    @Override
    public boolean isTotalCountUnlimited(Integer totalCount) {
        return CouponTemplateDO.TOTAL_COUNT_MAX.equals(totalCount);
    }

    @Override
    public Long createCouponTemplate(CouponTemplateCreateReqVO createReqVO) {
        // 校验商品范围
        validateProductScope(createReqVO.getProductScope(), createReqVO.getProductScopeValues());
        // 插入
        CouponTemplateDO couponTemplate = CouponTemplateConvert.INSTANCE.convert(createReqVO)
                .setStatus(CommonStatusEnum.ENABLE.getStatus());
        couponTemplateMapper.insert(couponTemplate);
        // 返回
        return couponTemplate.getId();
    }

    @Override
    public void updateCouponTemplate(CouponTemplateUpdateReqVO updateReqVO) {
        // 校验存在
        CouponTemplateDO couponTemplate = validateCouponTemplateExists(updateReqVO.getId());
        // 校验发放数量不能过小（仅在 CouponTakeTypeEnum.USER 用户领取时）
        if (CouponTakeTypeEnum.isUser(couponTemplate.getTakeType())
                && !isTotalCountUnlimited(updateReqVO.getTotalCount()) // 非不限制总发放数量
                && updateReqVO.getTotalCount() < couponTemplate.getTakeCount()) {
            throw exception(COUPON_TEMPLATE_TOTAL_COUNT_TOO_SMALL, couponTemplate.getTakeCount());
        }
        // 校验商品范围
        validateProductScope(updateReqVO.getProductScope(), updateReqVO.getProductScopeValues());

        // 更新
        CouponTemplateDO updateObj = CouponTemplateConvert.INSTANCE.convert(updateReqVO);
        couponTemplateMapper.updateById(updateObj);
    }

    @Override
    public void updateCouponTemplateStatus(Long id, Integer status) {
        // 校验存在
        validateCouponTemplateExists(id);
        // 更新
        couponTemplateMapper.updateById(new CouponTemplateDO().setId(id).setStatus(status));
    }

    @Override
    public void deleteCouponTemplate(Long id) {
        // 校验存在
        validateCouponTemplateExists(id);
        // 删除
        couponTemplateMapper.deleteById(id);
    }

    private CouponTemplateDO validateCouponTemplateExists(Long id) {
        CouponTemplateDO couponTemplate = couponTemplateMapper.selectById(id);
        if (couponTemplate == null) {
            throw exception(COUPON_TEMPLATE_NOT_EXISTS);
        }
        return couponTemplate;
    }

    private void validateProductScope(Integer productScope, List<Long> productScopeValues) {
        if (Objects.equals(PromotionProductScopeEnum.SPU.getScope(), productScope)) {
            productSpuApi.validateSpuList(productScopeValues).checkError();
        } else if (Objects.equals(PromotionProductScopeEnum.CATEGORY.getScope(), productScope)) {
            productCategoryApi.validateCategoryList(productScopeValues).checkError();
        }
    }

    @Override
    public CouponTemplateDO getCouponTemplate(Long id) {
        return couponTemplateMapper.selectById(id);
    }

    @Override
    public PageResult<CouponTemplateDO> getCouponTemplatePage(CouponTemplatePageReqVO pageReqVO) {
        return couponTemplateMapper.selectPage(pageReqVO);
    }

    @Override
    public void updateCouponTemplateTakeCount(Long id, int incrCount) {
        int updateCount = couponTemplateMapper.updateTakeCount(id, incrCount);
        if (updateCount == 0) {
            throw exception(COUPON_TEMPLATE_NOT_ENOUGH);
        }
    }

    @Override
    public List<CouponTemplateDO> getCouponTemplateListByTakeType(CouponTakeTypeEnum takeType) {
        return couponTemplateMapper.selectListByTakeType(takeType.getType());
    }

    @Override
    public List<CouponTemplateDO> getCouponTemplateList(List<Integer> canTakeTypes, Integer productScope,
                                                        Long productScopeValue, Integer count) {
        return couponTemplateMapper.selectList(canTakeTypes, productScope, productScopeValue, count);
    }

    @Override
    public List<CouponTemplateDO> getCouponTemplateList(Collection<Long> ids) {
        return couponTemplateMapper.selectByIds(ids);
    }

}
