package cn.cheers.x.module.dynamicbusiness.controller.admin.useraccount.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserAccountDepartmentListRespVO {

    @Schema(description = "这些部门上、已经在用户目录里的人")
    private List<UserAccountRespVO> people = new ArrayList<>();

    @Schema(description = "挂在部门上、但还不是用户目录记录的人数。页面只提示这个数，不把他们显示成用户")
    private int legacyCount;
}
