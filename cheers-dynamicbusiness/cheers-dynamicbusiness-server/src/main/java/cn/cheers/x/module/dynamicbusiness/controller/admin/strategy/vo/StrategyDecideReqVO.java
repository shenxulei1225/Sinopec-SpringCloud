package cn.cheers.x.module.dynamicbusiness.controller.admin.strategy.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 一次判定的请求。事实是条件来源里这次已经出现的记录编号。
 */
@Data
public class StrategyDecideReqVO {

    @NotBlank(message = "没有指定域")
    private String domainCode;

    private List<String> presentConditionIds;
}
