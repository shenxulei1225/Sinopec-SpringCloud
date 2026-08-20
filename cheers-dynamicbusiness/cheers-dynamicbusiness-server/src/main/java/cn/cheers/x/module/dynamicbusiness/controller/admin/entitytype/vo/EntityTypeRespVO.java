package cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 业务类型响应 VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EntityTypeRespVO extends EntityTypeBaseVO {

    @Schema(description = "业务类型编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "业务类型级别", example = "USER")
    private String typeLevel;

    @Schema(description = "该目录「数据」页签引用的工作台布局实例 id")
    private Long dataLayoutId;

    @Schema(description = "子业务类型列表")
    private List<EntityTypeRespVO> children;

}
