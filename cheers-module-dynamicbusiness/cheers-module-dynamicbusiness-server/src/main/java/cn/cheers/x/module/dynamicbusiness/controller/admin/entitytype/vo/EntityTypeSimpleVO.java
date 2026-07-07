package cn.cheers.x.module.dynamicbusiness.controller.admin.businesstype.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 业务类型简单 VO
 * 
 * <h3>用途说明</h3>
 * <p>业务类型的精简版本，仅包含核心字段，用于下拉选择等轻量级场景。</p>
 * 
 * <h3>应用场景</h3>
 * <ul>
 *   <li><b>接口</b>：GET /system/business-type/simple-list</li>
 *   <li><b>场景1</b>：Model 创建表单 - 业务类型下拉选择框</li>
 *   <li><b>场景2</b>：实体筛选条件 - 按业务类型筛选的下拉框</li>
 *   <li><b>场景3</b>：关联配置 - 选择目标业务类型的下拉框</li>
 * </ul>
 * 
 * <h3>设计说明</h3>
 * <p>相比 {@link BusinessTypeRespVO}，本 VO 只包含 code、name、description 三个字段，
 * 减少数据传输量，提升下拉框加载性能。</p>
 * 
 * <h3>与其他 VO 的关系</h3>
 * <ul>
 *   <li>完整版本 → {@link BusinessTypeRespVO}</li>
 * </ul>
 * 
 * @author yudao
 */
@Schema(description = "管理后台 - BusinessType 简单 VO")
@Data
public class BusinessTypeSimpleVO {

    @Schema(description = "业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "task_management")
    private String code;

    @Schema(description = "业务类型名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "任务管理")
    private String name;

    @Schema(description = "业务类型描述", example = "用于管理各类任务")
    private String description;
}
