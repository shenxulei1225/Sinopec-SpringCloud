package cn.cheers.x.module.dynamicbusiness.service.sop.dto;

import lombok.Data;

import java.util.List;

/**
 * 实例步骤差量：当前最小可用为整列表 replaceSteps。
 */
@Data
public class SopStepOverride {

    private List<SopStepTemplateRef> replaceSteps;
}
