package cn.cheers.x.inspection.inspection_content.controller.admin.library.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 完整树形结构 VO。
 *
 * <p>用于方式二：一次性获取。</p>
 */
@Schema(description = "管理后台 - 完整树形结构 VO")
@Data
public class FullTreeVO {

    @Schema(description = "型号列表")
    private List<ModelNodeVO> models;

    @Data
    @Schema(description = "型号节点")
    public static class ModelNodeVO {

        @Schema(description = "对象模型/型号")
        private String objectModel;

        @Schema(description = "该型号下的设备数量")
        private Integer deviceCount;

        @Schema(description = "该型号对应的检查项列表")
        private List<LibraryItemVO> items;

        @Schema(description = "该型号下的设备列表")
        private List<LibraryObjectVO> devices;
    }
}
