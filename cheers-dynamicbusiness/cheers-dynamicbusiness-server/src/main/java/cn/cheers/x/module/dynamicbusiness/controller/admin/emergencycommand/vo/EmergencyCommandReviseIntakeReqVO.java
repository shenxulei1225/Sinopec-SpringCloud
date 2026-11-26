package cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 待研判时改正接报经过和照片。名称、位置和发生时间不在这里改。
 */
@Data
public class EmergencyCommandReviseIntakeReqVO {

    @Schema(description = "应急事件编号")
    @NotBlank(message = "事件编号不能为空")
    private String eventId;

    @Schema(description = "事故经过。空着表示清掉原来的经过")
    private String intakeNote;

    @Schema(description = "已上传照片的地址。空列表表示清掉照片。最多 9 张")
    private List<String> photoUrls;
}
