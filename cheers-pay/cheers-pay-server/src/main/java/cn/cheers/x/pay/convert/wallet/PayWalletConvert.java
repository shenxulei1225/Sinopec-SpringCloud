package cn.cheers.x.pay.convert.wallet;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.pay.controller.admin.wallet.vo.wallet.PayWalletRespVO;
import cn.cheers.x.pay.controller.app.wallet.vo.wallet.AppPayWalletRespVO;
import cn.cheers.x.pay.dal.dataobject.wallet.PayWalletDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PayWalletConvert {

    PayWalletConvert INSTANCE = Mappers.getMapper(PayWalletConvert.class);

    AppPayWalletRespVO convert(PayWalletDO bean);

    PayWalletRespVO convert02(PayWalletDO bean);

    PageResult<PayWalletRespVO> convertPage(PageResult<PayWalletDO> page);

}
