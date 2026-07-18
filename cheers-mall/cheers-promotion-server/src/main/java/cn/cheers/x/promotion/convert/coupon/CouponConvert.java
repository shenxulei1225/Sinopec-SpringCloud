package cn.cheers.x.promotion.convert.coupon;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.promotion.api.coupon.dto.CouponRespDTO;
import cn.cheers.x.promotion.controller.admin.coupon.vo.coupon.CouponPageItemRespVO;
import cn.cheers.x.promotion.controller.admin.coupon.vo.coupon.CouponPageReqVO;
import cn.cheers.x.promotion.controller.app.coupon.vo.coupon.AppCouponPageReqVO;
import cn.cheers.x.promotion.dal.dataobject.coupon.CouponDO;
import cn.cheers.x.promotion.dal.dataobject.coupon.CouponTemplateDO;
import cn.cheers.x.promotion.enums.coupon.CouponStatusEnum;
import cn.cheers.x.promotion.enums.coupon.CouponTemplateValidityTypeEnum;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * 优惠劵 Convert
 *
 * 
 */
@Mapper
public interface CouponConvert {

    CouponConvert INSTANCE = Mappers.getMapper(CouponConvert.class);

    PageResult<CouponPageItemRespVO> convertPage(PageResult<CouponDO> page);

    CouponRespDTO convert(CouponDO bean);

    default CouponDO convert(CouponTemplateDO template, Long userId) {
        CouponDO coupon = new CouponDO()
                .setTemplateId(template.getId())
                .setName(template.getName())
                .setTakeType(template.getTakeType())
                .setUsePrice(template.getUsePrice())
                .setProductScope(template.getProductScope())
                .setProductScopeValues(template.getProductScopeValues())
                .setDiscountType(template.getDiscountType())
                .setDiscountPercent(template.getDiscountPercent())
                .setDiscountPrice(template.getDiscountPrice())
                .setDiscountLimitPrice(template.getDiscountLimitPrice())
                .setStatus(CouponStatusEnum.UNUSED.getStatus())
                .setUserId(userId);
        if (CouponTemplateValidityTypeEnum.DATE.getType().equals(template.getValidityType())) {
            coupon.setValidStartTime(template.getValidStartTime());
            coupon.setValidEndTime(template.getValidEndTime());
        } else if (CouponTemplateValidityTypeEnum.TERM.getType().equals(template.getValidityType())) {
            coupon.setValidStartTime(LocalDateTime.now().plusDays(template.getFixedStartTerm()));
            coupon.setValidEndTime(coupon.getValidStartTime().plusDays(template.getFixedEndTerm()));
        }
        return coupon;
    }

    CouponPageReqVO convert(AppCouponPageReqVO pageReqVO, Collection<Long> userIds);

}
