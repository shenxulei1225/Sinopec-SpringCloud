package cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission.vo;

import cn.cheers.x.module.dynamicbusiness.service.datapermission.DutyPostUserQueryService.DutyPostUserView;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "职务上的一个人")
@Data
public class DutyPostUserRespVO {

    private Long id;
    private Long dutyPostId;
    private Long userId;

    @Schema(description = "人员姓名；用户目录里已删除时为空")
    private String userName;

    public static DutyPostUserRespVO from(DutyPostUserView view) {
        DutyPostUserRespVO vo = new DutyPostUserRespVO();
        vo.setId(view.row().getId());
        vo.setDutyPostId(view.row().getDutyPostId());
        vo.setUserId(view.row().getUserId());
        vo.setUserName(view.userName());
        return vo;
    }
}
