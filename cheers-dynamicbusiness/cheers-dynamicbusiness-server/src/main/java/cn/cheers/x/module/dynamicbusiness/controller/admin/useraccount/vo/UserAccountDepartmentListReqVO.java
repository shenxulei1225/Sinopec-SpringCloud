package cn.cheers.x.module.dynamicbusiness.controller.admin.useraccount.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class UserAccountDepartmentListReqVO {

    @Schema(description = "部门分类编号。点一座站时传入这座站的各部门；点一个部门时只传这一个")
    private List<Long> categoryIds;
}
