package cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Schema(description = "数据管理 - 栏间关系声明响应")
@Data
public class DmDataTabColumnRelationRespVO {

    private Long id;
    private Long layoutId;
    private String entityTypeCode;
    private String edgeId;
    private String fromColumnIdentity;
    private String toColumnIdentity;
    private String relationKind;
    private String fromTypeCode;
    private String toTypeCode;
    /** 边动作：filter=条件筛选；write=修改关联 */
    private String edgeAction;
    /** 启用的交互方式编码列表 */
    private List<String> enabledInteractions;
    /** 可选：引用字段编码列表；空则运行时按两端类型合并同目标 REF */
    private List<String> linkKeys;
    private Map<String, Object> presentation;
}
