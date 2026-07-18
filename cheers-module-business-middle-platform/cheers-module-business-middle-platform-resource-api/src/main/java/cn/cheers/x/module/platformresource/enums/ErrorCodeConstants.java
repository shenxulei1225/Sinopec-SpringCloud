package cn.cheers.x.module.platformresource.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * platform-resource 错误码（1-004-050-000 段）
 */
public interface ErrorCodeConstants {

    ErrorCode COMPONENT_NOT_EXISTS = new ErrorCode(1_004_050_000, "组件不存在");
    ErrorCode COMPONENT_CODE_DUPLICATE = new ErrorCode(1_004_050_001, "组件编码已存在");
    ErrorCode COMPONENT_PROPS_NOT_EXISTS = new ErrorCode(1_004_050_002, "组件 Props 不存在");
    ErrorCode COMPONENT_PROPS_NOT_TEMPLATE = new ErrorCode(1_004_050_003, "propsId 必须指向模板");
    ErrorCode COMPONENT_PROPS_SAVE_PROPS_REQUIRED = new ErrorCode(1_004_050_004, "模板保存需提供 props");
    ErrorCode COMPONENT_PROPS_SAVE_OVERRIDE_REQUIRED = new ErrorCode(1_004_050_005, "实例保存需提供 propsOverride");
    ErrorCode COMPONENT_PROPS_INSTANCE_MISSING_TEMPLATE = new ErrorCode(1_004_050_006, "实例缺少 templateId");
    ErrorCode COMPONENT_PROPS_TEMPLATE_HAS_INSTANCES = new ErrorCode(1_004_050_007, "模板仍有关联实例，无法删除");
    ErrorCode COMPONENT_PROPS_DATA_SOURCE_REQUIRED = new ErrorCode(1_004_050_008, "模板须选择数据来源 dataSource");
    ErrorCode COMPONENT_PROPS_CONTRACT_NOT_FOUND = new ErrorCode(1_004_050_009, "数据能力契约不存在");
    ErrorCode COMPONENT_PROPS_UNSUPPORTED_COMPONENT = new ErrorCode(1_004_050_010, "不支持的组件类型");
    ErrorCode COMPONENT_PROPS_GENERATE_PREVIEW_ONLY = new ErrorCode(1_004_050_011, "预览模式不可落库");

    // ─── 视图配置（1_004_050_020 段） ──────────────────────────────────────────
    ErrorCode VIEW_CONFIG_NOT_EXISTS            = new ErrorCode(1_004_050_020, "视图配置不存在");
    ErrorCode VIEW_CONFIG_NOT_TEMPLATE         = new ErrorCode(1_004_050_021, "viewId 必须指向视图模板");
    ErrorCode VIEW_CONFIG_SAVE_JSON_REQUIRED   = new ErrorCode(1_004_050_022, "模板保存需提供 configJson");
    ErrorCode VIEW_CONFIG_SAVE_OVERRIDE_REQUIRED = new ErrorCode(1_004_050_023, "实例保存需提供 configOverride");
    ErrorCode VIEW_CONFIG_TEMPLATE_HAS_INSTANCES = new ErrorCode(1_004_050_024, "视图模板仍有关联实例，无法删除");
    ErrorCode VIEW_CONFIG_CODE_DUPLICATE       = new ErrorCode(1_004_050_025, "视图编码已存在：{}");

}
