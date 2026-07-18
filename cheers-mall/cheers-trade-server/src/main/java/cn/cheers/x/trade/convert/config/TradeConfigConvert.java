package cn.cheers.x.trade.convert.config;

import cn.cheers.x.trade.controller.admin.config.vo.TradeConfigRespVO;
import cn.cheers.x.trade.controller.admin.config.vo.TradeConfigSaveReqVO;
import cn.cheers.x.trade.controller.app.config.vo.AppTradeConfigRespVO;
import cn.cheers.x.trade.dal.dataobject.config.TradeConfigDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 交易中心配置 Convert
 *
 * @author owen
 */
@Mapper
public interface TradeConfigConvert {

    TradeConfigConvert INSTANCE = Mappers.getMapper(TradeConfigConvert.class);

    TradeConfigDO convert(TradeConfigSaveReqVO bean);

    TradeConfigRespVO convert(TradeConfigDO bean);

    AppTradeConfigRespVO convert02(TradeConfigDO tradeConfig);
}
