package cn.cheers.x.pay.api.refund;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.cheers.x.pay.api.refund.dto.PayRefundCreateReqDTO;
import cn.cheers.x.pay.api.refund.dto.PayRefundRespDTO;
import cn.cheers.x.pay.convert.refund.PayRefundConvert;
import cn.cheers.x.pay.dal.dataobject.refund.PayRefundDO;
import cn.cheers.x.pay.service.refund.PayRefundService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class PayRefundApiImpl implements PayRefundApi {

    @Resource
    private PayRefundService payRefundService;

    @Override
    public CommonResult<Long> createRefund(PayRefundCreateReqDTO reqDTO) {
        return success(payRefundService.createRefund(reqDTO));
    }

    @Override
    public CommonResult<PayRefundRespDTO> getRefund(Long id) {
        PayRefundDO refund = payRefundService.getRefund(id);
        return success(BeanUtils.toBean(refund, PayRefundRespDTO.class));
    }

}
