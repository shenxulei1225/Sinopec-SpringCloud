package cn.iocoder.yudao.module.inspection.inspection_content.controller.admin.library.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 型号（含检查项）VO。
 *
 * <p>用于方式一：分步获取的第一步。</p>
 */
@Schema(description = "管理后台 - 型号（含检查项）VO")
@Data
public class ModelWithItemsVO {

    @Schema(description = "对象模型/型号")
    private String objectModel;

    @Schema(description = "该型号下的设备数量")
    private Integer deviceCount;

    @Schema(description = "该型号对应的检查项列表")
    private List<LibraryItemVO> items;
}
