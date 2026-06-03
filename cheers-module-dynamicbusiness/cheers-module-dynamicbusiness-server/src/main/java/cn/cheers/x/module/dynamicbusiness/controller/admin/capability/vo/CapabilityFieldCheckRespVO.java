package cn.cheers.x.module.dynamicbusiness.controller.admin.capability.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "字段异步校验结果")
public class CapabilityFieldCheckRespVO {

    @Schema(description = "是否可用（唯一性校验通过）")
    private Boolean available;

    @Schema(description = "不可用时的提示")
    private String message;

    public static CapabilityFieldCheckRespVO ok() {
        CapabilityFieldCheckRespVO vo = new CapabilityFieldCheckRespVO();
        vo.setAvailable(true);
        return vo;
    }

    public static CapabilityFieldCheckRespVO fail(String message) {
        CapabilityFieldCheckRespVO vo = new CapabilityFieldCheckRespVO();
        vo.setAvailable(false);
        vo.setMessage(message);
        return vo;
    }
}
