package cn.cheers.x.promotion.convert.banner;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.promotion.controller.admin.banner.vo.BannerCreateReqVO;
import cn.cheers.x.promotion.controller.admin.banner.vo.BannerRespVO;
import cn.cheers.x.promotion.controller.admin.banner.vo.BannerUpdateReqVO;
import cn.cheers.x.promotion.controller.app.banner.vo.AppBannerRespVO;
import cn.cheers.x.promotion.dal.dataobject.banner.BannerDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface BannerConvert {

    BannerConvert INSTANCE = Mappers.getMapper(BannerConvert.class);

    List<BannerRespVO> convertList(List<BannerDO> list);

    PageResult<BannerRespVO> convertPage(PageResult<BannerDO> pageResult);

    BannerRespVO convert(BannerDO banner);

    BannerDO convert(BannerCreateReqVO createReqVO);

    BannerDO convert(BannerUpdateReqVO updateReqVO);

    List<AppBannerRespVO> convertList01(List<BannerDO> bannerList);

}
