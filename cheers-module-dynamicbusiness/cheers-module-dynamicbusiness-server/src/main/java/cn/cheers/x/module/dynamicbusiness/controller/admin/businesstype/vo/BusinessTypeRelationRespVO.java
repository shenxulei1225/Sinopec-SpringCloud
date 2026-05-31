package cn.cheers.x.module.dynamicbusiness.controller.admin.businesstype.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 业务类型关联响应 VO
 * 
 * <h3>用途说明</h3>
 * <p>返回业务类型关联的详细信息,包含源/目标业务类型信息及关联统计。</p>
 * 
 * <h3>应用场景</h3>
 * <ul>
 *   <li><b>接口</b>:GET /system/business-type/relation/list、GET /system/business-type/relation/get</li>
 *   <li><b>场景1</b>:管理后台 - 业务类型关联管理页面,展示已建立的关联列表</li>
 *   <li><b>场景2</b>:Model 配置页面,显示当前业务类型可关联的目标业务类型</li>
 * </ul>
 * 
 * <h3>关键字段说明</h3>
 * <ul>
 *   <li><b>sourceBusinessTypeCode/Name</b>:源业务类型信息</li>
 *   <li><b>targetBusinessTypeCode/Name</b>:目标业务类型信息</li>
 * </ul>
 * 

 * <h3>与其他 VO 的关系</h3>
 * <ul>
 *   <li>创建请求 → {@link BusinessTypeRelationCreateReqVO}</li>
 * </ul>
 * 
 * @author yudao
 */
@Schema(description = "管理后台 - BusinessType 关联 Response VO")
@Data
public class BusinessTypeRelationRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "源业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "task_management")
    private String sourceBusinessTypeCode;

    @Schema(description = "源业务类型名称", example = "任务管理")
    private String sourceBusinessTypeName;

    @Schema(description = "目标业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "production_plan")
    private String targetBusinessTypeCode;

    @Schema(description = "目标业务类型名称", example = "生产计划")
    private String targetBusinessTypeName;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
