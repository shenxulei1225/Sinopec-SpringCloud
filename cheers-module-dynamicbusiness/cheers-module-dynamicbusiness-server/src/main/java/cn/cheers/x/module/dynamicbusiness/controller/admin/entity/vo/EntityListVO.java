package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 管理后台 - 实体列表响应 VO（轻量级）
 * 
 * <p>用于列表查询，只返回必要的字段，不包含字段定义信息，避免性能问题。</p>
 * 
 * <p>与 EntityRespVO 的区别：</p>
 * <ul>
 *   <li>不包含 fields（字段定义列表）</li>
 *   <li>不包含 fieldNameMapping（字段名称映射）</li>
 *   <li>包含 associations（各 REF 字段关联数据，列表场景按需填充）</li>
 *   <li>customFields 可选，或只包含关键字段</li>
 * </ul>
 * 
 * @author 基础服务模块
 */
@Schema(description = "管理后台 - 实体列表响应 VO（轻量级）")
@Data
public class EntityListVO {

    @Schema(description = "实体ID", example = "1001")
    private Long id;

    @Schema(description = "业务类型编码", example = "equipment")
    private String entityTypeCode;

    @Schema(description = "模型ID", example = "1")
    private Long modelId;

    @Schema(description = "名称", example = "示例实体")
    private String name;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "父实体ID", example = "1000")
    private Long parentId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "关键自定义字段（仅列表展示需要的字段，可选）")
    private Map<String, Object> keyCustomFields;

    @Schema(description = "各 REF/REFMulti 字段的关联数据块（列表按需）")
    private List<AssociationsVO> associations;
}
