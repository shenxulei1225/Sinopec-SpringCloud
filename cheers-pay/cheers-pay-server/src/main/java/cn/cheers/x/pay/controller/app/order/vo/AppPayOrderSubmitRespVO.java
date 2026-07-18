package cn.cheers.x.pay.controller.app.order.vo;

import cn.cheers.x.pay.controller.admin.order.vo.PayOrderSubmitRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户 APP - 支付订单提交 Response VO")
@Data
public class AppPayOrderSubmitRespVO extends PayOrderSubmitRespVO {

}
