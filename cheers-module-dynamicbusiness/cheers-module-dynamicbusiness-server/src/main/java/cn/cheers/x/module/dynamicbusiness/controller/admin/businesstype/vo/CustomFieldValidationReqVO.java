package cn.cheers.x.module.dynamicbusiness.controller.admin.businesstype.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 自定义字段验证请求 VO
 * 
 * <h3>用途说明</h3>
 * <p>用于验证实体的自定义字段数据是否符合 Model 定义的字段规则。</p>
 * 
 * <h3>应用场景</h3>
 * <ul>
 *   <li><b>接口</b>：POST /system/business-type/validate-custom-fields</li>
 *   <li><b>场景1</b>：实体创建/更新前，验证自定义字段数据的合法性</li>
 *   <li><b>场景2</b>：批量导入数据时，预校验自定义字段格式</li>
 *   <li><b>场景3</b>：表单提交前的前端预校验（可选）</li>
 * </ul>
 * 
 * <h3>验证规则</h3>
 * <ul>
 *   <li><b>必填校验</b>：检查 required=true 的字段是否有值</li>
 *   <li><b>类型校验</b>：检查值是否符合字段定义的数据类型（TEXT、NUMBER、DATE 等）</li>
 *   <li><b>格式校验</b>：根据 typeConfig 中的配置进行格式校验（如数字精度、枚举值范围）</li>
 *   <li><b>长度校验</b>：检查文本字段是否超过最大长度限制</li>
 * </ul>
 * 
 * <h3>customFieldsJson 格式</h3>
 * <pre>
 * {
 *   "fieldId1": "文本值",
 *   "fieldId2": 100,
 *   "fieldId3": "2024-01-01",
 *   "fieldId4": true
 * }
 * </pre>
 * 
 * <h3>与其他组件的关系</h3>
 * <ul>
 *   <li>验证服务 → CustomFieldValidationService</li>
 *   <li>字段定义 → ModelFieldAssignmentDO</li>
 * </ul>
 */
@Schema(description = "管理后台 - 自定义字段验证请求")
@Data
public class CustomFieldValidationReqVO {

    @Schema(description = "模型ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "模型ID不能为空")
    private Long modelId;

    @Schema(description = "自定义字段数据（JSON格式，键为字段ID，值为字段值）", 
            requiredMode = Schema.RequiredMode.REQUIRED, 
            example = "{\"1\": \"value1\", \"2\": 100}")
    @NotNull(message = "自定义字段数据不能为空")
    private String customFieldsJson;
}
