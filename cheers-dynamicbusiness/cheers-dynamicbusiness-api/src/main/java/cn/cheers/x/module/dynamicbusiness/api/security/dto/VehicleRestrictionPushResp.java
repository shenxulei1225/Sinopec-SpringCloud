package cn.cheers.x.module.dynamicbusiness.api.security.dto;

import java.util.List;

/**
 * 一次下发限制的结果。发出去的条数，以及没发出的原因。
 */
public record VehicleRestrictionPushResp(int sent, List<String> messages) {
}
