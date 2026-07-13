package cn.cheers.x.module.dynamicbusiness.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * System 错误码枚举类
 *
 * system 系统，使用 1-002-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== AUTH 模块 1-002-000-000 ==========
    ErrorCode AUTH_LOGIN_BAD_CREDENTIALS = new ErrorCode(1_002_000_000, "登录失败，账号密码不正确");
    ErrorCode AUTH_LOGIN_USER_DISABLED = new ErrorCode(1_002_000_001, "登录失败，账号被禁用");
    ErrorCode AUTH_LOGIN_CAPTCHA_CODE_ERROR = new ErrorCode(1_002_000_004, "验证码不正确，原因：{}");
    ErrorCode AUTH_THIRD_LOGIN_NOT_BIND = new ErrorCode(1_002_000_005, "未绑定账号，需要进行绑定");
    ErrorCode AUTH_MOBILE_NOT_EXISTS = new ErrorCode(1_002_000_007, "手机号不存在");
    ErrorCode AUTH_REGISTER_CAPTCHA_CODE_ERROR = new ErrorCode(1_002_000_008, "验证码不正确，原因：{}");

    // ========== 菜单模块 1-002-001-000 ==========
    ErrorCode MENU_NAME_DUPLICATE = new ErrorCode(1_002_001_000, "已经存在该名字的菜单");
    ErrorCode MENU_PARENT_NOT_EXISTS = new ErrorCode(1_002_001_001, "父菜单不存在");
    ErrorCode MENU_PARENT_ERROR = new ErrorCode(1_002_001_002, "不能设置自己为父菜单");
    ErrorCode MENU_NOT_EXISTS = new ErrorCode(1_002_001_003, "菜单不存在");
    ErrorCode MENU_EXISTS_CHILDREN = new ErrorCode(1_002_001_004, "存在子菜单，无法删除");
    ErrorCode MENU_PARENT_NOT_DIR_OR_MENU = new ErrorCode(1_002_001_005, "父菜单的类型必须是目录或者菜单");
    ErrorCode MENU_COMPONENT_NAME_DUPLICATE = new ErrorCode(1_002_001_006, "已经存在该组件名的菜单");
    ErrorCode MENU_PATH_DUPLICATE = new ErrorCode(1_002_001_007, "已经存在该路径的菜单: {}");

    // ========== 角色模块 1-002-002-000 ==========
    ErrorCode ROLE_NOT_EXISTS = new ErrorCode(1_002_002_000, "角色不存在");
    ErrorCode ROLE_NAME_DUPLICATE = new ErrorCode(1_002_002_001, "已经存在名为【{}】的角色");
    ErrorCode ROLE_CODE_DUPLICATE = new ErrorCode(1_002_002_002, "已经存在标识为【{}】的角色");
    ErrorCode ROLE_CAN_NOT_UPDATE_SYSTEM_TYPE_ROLE = new ErrorCode(1_002_002_003, "不能操作类型为系统内置的角色");
    ErrorCode ROLE_IS_DISABLE = new ErrorCode(1_002_002_004, "名字为【{}】的角色已被禁用");
    ErrorCode ROLE_ADMIN_CODE_ERROR = new ErrorCode(1_002_002_005, "标识【{}】不能使用");

    // ========== 用户模块 1-002-003-000 ==========
    ErrorCode USER_USERNAME_EXISTS = new ErrorCode(1_002_003_000, "用户账号已经存在");
    ErrorCode USER_MOBILE_EXISTS = new ErrorCode(1_002_003_001, "手机号已经存在");
    ErrorCode USER_EMAIL_EXISTS = new ErrorCode(1_002_003_002, "邮箱已经存在");
    ErrorCode USER_NOT_EXISTS = new ErrorCode(1_002_003_003, "用户不存在");
    ErrorCode USER_IMPORT_LIST_IS_EMPTY = new ErrorCode(1_002_003_004, "导入用户数据不能为空！");
    ErrorCode USER_PASSWORD_FAILED = new ErrorCode(1_002_003_005, "用户密码校验失败");
    ErrorCode USER_IS_DISABLE = new ErrorCode(1_002_003_006, "名字为【{}】的用户已被禁用");
    ErrorCode USER_COUNT_MAX = new ErrorCode(1_002_003_008, "创建用户失败，原因：超过租户最大租户配额({})！");
    ErrorCode USER_IMPORT_INIT_PASSWORD = new ErrorCode(1_002_003_009, "初始密码不能为空");
    ErrorCode USER_MOBILE_NOT_EXISTS = new ErrorCode(1_002_003_010, "该手机号尚未注册");
    ErrorCode USER_REGISTER_DISABLED = new ErrorCode(1_002_003_011, "注册功能已关闭");

    // ========== 部门模块 1-002-004-000 ==========
    ErrorCode DEPT_NAME_DUPLICATE = new ErrorCode(1_002_004_000, "已经存在该名字的部门");
    ErrorCode DEPT_PARENT_NOT_EXITS = new ErrorCode(1_002_004_001, "父级部门不存在");
    ErrorCode DEPT_NOT_FOUND = new ErrorCode(1_002_004_002, "当前部门不存在");
    ErrorCode DEPT_EXITS_CHILDREN = new ErrorCode(1_002_004_003, "存在子部门，无法删除");
    ErrorCode DEPT_PARENT_ERROR = new ErrorCode(1_002_004_004, "不能设置自己为父部门");
    ErrorCode DEPT_NOT_ENABLE = new ErrorCode(1_002_004_006, "部门({})不处于开启状态，不允许选择");
    ErrorCode DEPT_PARENT_IS_CHILD = new ErrorCode(1_002_004_007, "不能设置自己的子部门为父部门");

    // ========== 岗位模块 1-002-005-000 ==========
    ErrorCode POST_NOT_FOUND = new ErrorCode(1_002_005_000, "当前岗位不存在");
    ErrorCode POST_NOT_ENABLE = new ErrorCode(1_002_005_001, "岗位({}) 不处于开启状态，不允许选择");
    ErrorCode POST_NAME_DUPLICATE = new ErrorCode(1_002_005_002, "已经存在该名字的岗位");
    ErrorCode POST_CODE_DUPLICATE = new ErrorCode(1_002_005_003, "已经存在该标识的岗位");

    // ========== 字典类型 1-002-006-000 ==========
    ErrorCode DICT_TYPE_NOT_EXISTS = new ErrorCode(1_002_006_001, "当前字典类型不存在");
    ErrorCode DICT_TYPE_NOT_ENABLE = new ErrorCode(1_002_006_002, "字典类型不处于开启状态，不允许选择");
    ErrorCode DICT_TYPE_NAME_DUPLICATE = new ErrorCode(1_002_006_003, "已经存在该名字的字典类型");
    ErrorCode DICT_TYPE_TYPE_DUPLICATE = new ErrorCode(1_002_006_004, "已经存在该类型的字典类型");
    ErrorCode DICT_TYPE_HAS_CHILDREN = new ErrorCode(1_002_006_005, "无法删除，该字典类型还有字典数据");

    // ========== 字典数据 1-002-007-000 ==========
    ErrorCode DICT_DATA_NOT_EXISTS = new ErrorCode(1_002_007_001, "当前字典数据不存在");
    ErrorCode DICT_DATA_NOT_ENABLE = new ErrorCode(1_002_007_002, "字典数据({})不处于开启状态，不允许选择");
    ErrorCode DICT_DATA_VALUE_DUPLICATE = new ErrorCode(1_002_007_003, "已经存在该值的字典数据");

    // ========== 通知公告 1-002-008-000 ==========
    ErrorCode NOTICE_NOT_FOUND = new ErrorCode(1_002_008_001, "当前通知公告不存在");

    // ========== 短信渠道 1-002-011-000 ==========
    ErrorCode SMS_CHANNEL_NOT_EXISTS = new ErrorCode(1_002_011_000, "短信渠道不存在");
    ErrorCode SMS_CHANNEL_DISABLE = new ErrorCode(1_002_011_001, "短信渠道不处于开启状态，不允许选择");
    ErrorCode SMS_CHANNEL_HAS_CHILDREN = new ErrorCode(1_002_011_002, "无法删除，该短信渠道还有短信模板");

    // ========== 短信模板 1-002-012-000 ==========
    ErrorCode SMS_TEMPLATE_NOT_EXISTS = new ErrorCode(1_002_012_000, "短信模板不存在");
    ErrorCode SMS_TEMPLATE_CODE_DUPLICATE = new ErrorCode(1_002_012_001, "已经存在编码为【{}】的短信模板");
    ErrorCode SMS_TEMPLATE_API_ERROR = new ErrorCode(1_002_012_002, "短信 API 模板调用失败，原因是：{}");
    ErrorCode SMS_TEMPLATE_API_AUDIT_CHECKING = new ErrorCode(1_002_012_003, "短信 API 模版无法使用，原因：审批中");
    ErrorCode SMS_TEMPLATE_API_AUDIT_FAIL = new ErrorCode(1_002_012_004, "短信 API 模版无法使用，原因：审批不通过，{}");
    ErrorCode SMS_TEMPLATE_API_NOT_FOUND = new ErrorCode(1_002_012_005, "短信 API 模版无法使用，原因：模版不存在");

    // ========== 短信发送 1-002-013-000 ==========
    ErrorCode SMS_SEND_MOBILE_NOT_EXISTS = new ErrorCode(1_002_013_000, "手机号不存在");
    ErrorCode SMS_SEND_MOBILE_TEMPLATE_PARAM_MISS = new ErrorCode(1_002_013_001, "模板参数({})缺失");
    ErrorCode SMS_SEND_TEMPLATE_NOT_EXISTS = new ErrorCode(1_002_013_002, "短信模板不存在");

    // ========== 短信验证码 1-002-014-000 ==========
    ErrorCode SMS_CODE_NOT_FOUND = new ErrorCode(1_002_014_000, "验证码不存在");
    ErrorCode SMS_CODE_EXPIRED = new ErrorCode(1_002_014_001, "验证码已过期");
    ErrorCode SMS_CODE_USED = new ErrorCode(1_002_014_002, "验证码已使用");
    ErrorCode SMS_CODE_EXCEED_SEND_MAXIMUM_QUANTITY_PER_DAY = new ErrorCode(1_002_014_004, "超过每日短信发送数量");
    ErrorCode SMS_CODE_SEND_TOO_FAST = new ErrorCode(1_002_014_005, "短信发送过于频繁");

    // ========== 租户信息 1-002-015-000 ==========
    ErrorCode TENANT_NOT_EXISTS = new ErrorCode(1_002_015_000, "租户不存在");
    ErrorCode TENANT_DISABLE = new ErrorCode(1_002_015_001, "名字为【{}】的租户已被禁用");
    ErrorCode TENANT_EXPIRE = new ErrorCode(1_002_015_002, "名字为【{}】的租户已过期");
    ErrorCode TENANT_CAN_NOT_UPDATE_SYSTEM = new ErrorCode(1_002_015_003, "系统租户不能进行修改、删除等操作！");
    ErrorCode TENANT_NAME_DUPLICATE = new ErrorCode(1_002_015_004, "名字为【{}】的租户已存在");
    ErrorCode TENANT_WEBSITE_DUPLICATE = new ErrorCode(1_002_015_005, "域名为【{}】的租户已存在");

    // ========== 租户套餐 1-002-016-000 ==========
    ErrorCode TENANT_PACKAGE_NOT_EXISTS = new ErrorCode(1_002_016_000, "租户套餐不存在");
    ErrorCode TENANT_PACKAGE_USED = new ErrorCode(1_002_016_001, "租户正在使用该套餐，请给租户重新设置套餐后再尝试删除");
    ErrorCode TENANT_PACKAGE_DISABLE = new ErrorCode(1_002_016_002, "名字为【{}】的租户套餐已被禁用");
    ErrorCode TENANT_PACKAGE_NAME_DUPLICATE = new ErrorCode(1_002_016_003, "已经存在该名字的租户套餐");

    // ========== 社交用户 1-002-018-000 ==========
    ErrorCode SOCIAL_USER_AUTH_FAILURE = new ErrorCode(1_002_018_000, "社交授权失败，原因是：{}");
    ErrorCode SOCIAL_USER_NOT_FOUND = new ErrorCode(1_002_018_001, "社交授权失败，找不到对应的用户");

    ErrorCode SOCIAL_CLIENT_WEIXIN_MINI_APP_PHONE_CODE_ERROR = new ErrorCode(1_002_018_200, "获得手机号失败");
    ErrorCode SOCIAL_CLIENT_WEIXIN_MINI_APP_QRCODE_ERROR = new ErrorCode(1_002_018_201, "获得小程序码失败");
    ErrorCode SOCIAL_CLIENT_WEIXIN_MINI_APP_SUBSCRIBE_TEMPLATE_ERROR = new ErrorCode(1_002_018_202, "获得小程序订阅消息模版失败");
    ErrorCode SOCIAL_CLIENT_WEIXIN_MINI_APP_SUBSCRIBE_MESSAGE_ERROR = new ErrorCode(1_002_018_203, "发送小程序订阅消息失败");
    ErrorCode SOCIAL_CLIENT_WEIXIN_MINI_APP_ORDER_UPLOAD_SHIPPING_INFO_ERROR = new ErrorCode(1_002_018_204, "上传微信小程序发货信息失败");
    ErrorCode SOCIAL_CLIENT_WEIXIN_MINI_APP_ORDER_NOTIFY_CONFIRM_RECEIVE_ERROR = new ErrorCode(1_002_018_205, "上传微信小程序订单收货信息失败");
    ErrorCode SOCIAL_CLIENT_NOT_EXISTS = new ErrorCode(1_002_018_210, "社交客户端不存在");
    ErrorCode SOCIAL_CLIENT_UNIQUE = new ErrorCode(1_002_018_211, "社交客户端已存在配置");

    // ========== OAuth2 客户端 1-002-020-000 =========
    ErrorCode OAUTH2_CLIENT_NOT_EXISTS = new ErrorCode(1_002_020_000, "OAuth2 客户端不存在");
    ErrorCode OAUTH2_CLIENT_EXISTS = new ErrorCode(1_002_020_001, "OAuth2 客户端编号已存在");
    ErrorCode OAUTH2_CLIENT_DISABLE = new ErrorCode(1_002_020_002, "OAuth2 客户端已禁用");
    ErrorCode OAUTH2_CLIENT_AUTHORIZED_GRANT_TYPE_NOT_EXISTS = new ErrorCode(1_002_020_003, "不支持该授权类型");
    ErrorCode OAUTH2_CLIENT_SCOPE_OVER = new ErrorCode(1_002_020_004, "授权范围过大");
    ErrorCode OAUTH2_CLIENT_REDIRECT_URI_NOT_MATCH = new ErrorCode(1_002_020_005, "无效 redirect_uri: {}");
    ErrorCode OAUTH2_CLIENT_CLIENT_SECRET_ERROR = new ErrorCode(1_002_020_006, "无效 client_secret: {}");

    // ========== OAuth2 授权 1-002-021-000 =========
    ErrorCode OAUTH2_GRANT_CLIENT_ID_MISMATCH = new ErrorCode(1_002_021_000, "client_id 不匹配");
    ErrorCode OAUTH2_GRANT_REDIRECT_URI_MISMATCH = new ErrorCode(1_002_021_001, "redirect_uri 不匹配");
    ErrorCode OAUTH2_GRANT_STATE_MISMATCH = new ErrorCode(1_002_021_002, "state 不匹配");

    // ========== OAuth2 授权 1-002-022-000 =========
    ErrorCode OAUTH2_CODE_NOT_EXISTS = new ErrorCode(1_002_022_000, "code 不存在");
    ErrorCode OAUTH2_CODE_EXPIRE = new ErrorCode(1_002_022_001, "code 已过期");

    // ========== 邮箱账号 1-002-023-000 ==========
    ErrorCode MAIL_ACCOUNT_NOT_EXISTS = new ErrorCode(1_002_023_000, "邮箱账号不存在");
    ErrorCode MAIL_ACCOUNT_RELATE_TEMPLATE_EXISTS = new ErrorCode(1_002_023_001, "无法删除，该邮箱账号还有邮件模板");

    // ========== 邮件模版 1-002-024-000 ==========
    ErrorCode MAIL_TEMPLATE_NOT_EXISTS = new ErrorCode(1_002_024_000, "邮件模版不存在");
    ErrorCode MAIL_TEMPLATE_CODE_EXISTS = new ErrorCode(1_002_024_001, "邮件模版 code({}) 已存在");

    // ========== 邮件发送 1-002-025-000 ==========
    ErrorCode MAIL_SEND_TEMPLATE_PARAM_MISS = new ErrorCode(1_002_025_000, "模板参数({})缺失");
    ErrorCode MAIL_SEND_MAIL_NOT_EXISTS = new ErrorCode(1_002_025_001, "邮箱不存在");

    // ========== 站内信模版 1-002-026-000 ==========
    ErrorCode NOTIFY_TEMPLATE_NOT_EXISTS = new ErrorCode(1_002_026_000, "站内信模版不存在");
    ErrorCode NOTIFY_TEMPLATE_CODE_DUPLICATE = new ErrorCode(1_002_026_001, "已经存在编码为【{}】的站内信模板");

    // ========== 站内信模版 1-002-027-000 ==========

    // ========== 站内信发送 1-002-028-000 ==========
    ErrorCode NOTIFY_SEND_TEMPLATE_PARAM_MISS = new ErrorCode(1_002_028_000, "模板参数({})缺失");

    // ========== 实体权限 1-002-029-000 ==========
    ErrorCode ENTITY_PERMISSION_NOT_EXISTS = new ErrorCode(1_002_029_000, "实体权限不存在");
    ErrorCode ENTITY_PERMISSION_EXISTS = new ErrorCode(1_002_029_001, "实体权限已存在");
    ErrorCode ENTITY_ACCESS_DENIED = new ErrorCode(1_002_029_002, "没有访问该实体的权限");
    ErrorCode ENTITY_OPERATION_DENIED = new ErrorCode(1_002_029_003, "没有执行该操作的权限");
    ErrorCode ENTITY_FIELD_ACCESS_DENIED = new ErrorCode(1_002_029_004, "没有访问该字段的权限");
    ErrorCode CATEGORY_ACCESS_DENIED = new ErrorCode(1_002_029_005, "没有访问该分类的权限");

    // ========== 分类类型管理 1-002-032-000 ==========
    ErrorCode CATEGORY_TYPE_NOT_EXISTS = new ErrorCode(1_002_032_000, "分类类型不存在");
    ErrorCode CATEGORY_TYPE_CODE_EXISTS = new ErrorCode(1_002_032_001, "分类类型编码已存在");
    ErrorCode CATEGORY_TYPE_CANNOT_DELETE = new ErrorCode(1_002_032_002, "该分类类型下有分类数据，无法删除");

    // ========== 模型字段分配 1-002-030-000 ==========
    ErrorCode MODEL_FIELD_ASSIGNMENT_NOT_EXISTS = new ErrorCode(1_002_030_000, "字段分配不存在");
    ErrorCode MODEL_FIELD_BASE_FIELD_CANNOT_UNASSIGN = new ErrorCode(1_002_030_001, "固定列字段不允许修改");
    ErrorCode MODEL_FIELD_BASE_FIELD_CANNOT_UPDATE = new ErrorCode(1_002_030_002, "固定列字段不允许修改业务规则");

    // ========== 固定列字段验证 1-002-031-000 ==========
    ErrorCode BASE_FIELD_VALIDATION_FAILED = new ErrorCode(1_002_031_000, "固定列字段验证失败：{}");
    ErrorCode BASE_FIELD_REQUIRED = new ErrorCode(1_002_031_001, "固定列字段 {} 不能为空");
    ErrorCode BASE_FIELD_FORMAT_ERROR = new ErrorCode(1_002_031_002, "固定列字段 {} 格式错误：{}");
    ErrorCode BASE_FIELD_LIBRARY_ID_REQUIRED = new ErrorCode(1_002_031_003, "新增固定列字段必须从字段库选择，请提供 libraryFieldId");
    ErrorCode BASE_FIELD_LIBRARY_NOT_FOUND = new ErrorCode(1_002_031_004, "字段库中不存在该字段");
    ErrorCode BASE_FIELD_LIBRARY_ENTITY_MISMATCH = new ErrorCode(1_002_031_005, "所选字段库条目不属于当前业务类型：{}");
    ErrorCode BASE_FIELD_LIBRARY_METADATA_READONLY = new ErrorCode(1_002_031_006, "固定列字段的编码、类型以字段库为准，不可手动修改；显示名称可按业务类型设置别名");

    // ========== 关联字段库 1-002-032-000 ==========
    ErrorCode RELATION_FIELD_NOT_EXISTS = new ErrorCode(1_002_032_000, "关联字段不存在");
    ErrorCode RELATION_FIELD_CODE_DUPLICATE = new ErrorCode(1_002_032_001, "关联字段编码已存在");
    ErrorCode RELATION_FIELD_SYSTEM_CANNOT_DELETE = new ErrorCode(1_002_032_002, "系统预置字段不可删除");
    ErrorCode RELATION_FIELD_IN_USE = new ErrorCode(1_002_032_003, "关联字段正在使用中，无法删除");
    ErrorCode RELATION_FIELD_TARGET_NOT_EXISTS = new ErrorCode(1_002_032_004, "关联目标不存在，请先创建目标业务类型/Model");
    ErrorCode RELATION_FIELD_TARGET_NOT_ENABLED = new ErrorCode(1_002_032_005, "请先在 Model 关联设置中启用该业务类型");
    ErrorCode RELATION_FIELD_CONSTRAINT_TYPE_REQUIRED = new ErrorCode(1_002_032_006, "关联字段必须选择引用约束类型");
    ErrorCode RELATION_FIELD_CONSTRAINT_TYPE_NOT_ALLOWED = new ErrorCode(1_002_032_007, "当前关联目标不允许使用该引用约束类型");

    // ========== 引用约束库 1-002-033-000 ==========
    ErrorCode REF_CONSTRAINT_LIBRARY_NOT_EXISTS = new ErrorCode(1_002_033_000, "引用约束库记录不存在");
    ErrorCode REF_CONSTRAINT_TYPE_NOT_CONFIGURED = new ErrorCode(1_002_033_001, "引用约束类型未在约束库中配置或未启用：{}");

    // ========== 计算字段 1-002-034-000 ==========
    ErrorCode COMPUTED_FIELD_NOT_EXISTS = new ErrorCode(1_002_034_000, "计算字段不存在");
    ErrorCode COMPUTED_FIELD_CODE_DUPLICATE = new ErrorCode(1_002_034_001, "计算字段编码已存在");
    ErrorCode COMPUTED_FIELD_FORMULA_SYNTAX_ERROR = new ErrorCode(1_002_034_002, "公式语法错误：{}");
    ErrorCode COMPUTED_FIELD_REFERENCE_NOT_EXISTS = new ErrorCode(1_002_034_003, "公式引用的字段不存在：{}");
    ErrorCode COMPUTED_FIELD_CIRCULAR_DEPENDENCY = new ErrorCode(1_002_034_004, "计算字段存在循环依赖：{}");
    ErrorCode COMPUTED_FIELD_AGGREGATE_CONFIG_ERROR = new ErrorCode(1_002_034_005, "聚合统计配置错误：{}");
    ErrorCode COMPUTED_FIELD_COMPUTE_ERROR = new ErrorCode(1_002_034_006, "计算字段执行错误：{}");

    // ========== Model 管理 1-002-035-000 ==========
    ErrorCode MODEL_NOT_EXISTS = new ErrorCode(1_002_035_000, "模型不存在");
    ErrorCode MODEL_NAME_DUPLICATE = new ErrorCode(1_002_035_001, "模型名称已存在：{}");
    ErrorCode MODEL_CODE_DUPLICATE = new ErrorCode(1_002_035_002, "模型编码已存在：{}");
    ErrorCode MODEL_HAS_ENTITIES = new ErrorCode(1_002_035_003, "模型存在关联的实体，禁止删除（数量：{}）");
    ErrorCode MODEL_NAME_FIELD_NOT_EXISTS = new ErrorCode(1_002_035_004, "指定的名称字段不存在：{}");
    ErrorCode MODEL_RELATION_NOT_DECLARED = new ErrorCode(1_002_035_005, "请先在模型上启用对 [{}] 的关联");
    ErrorCode MODEL_RELATION_FIELD_ALREADY_USED = new ErrorCode(1_002_035_006, "该关联字段已被选用");

    // ========== 关联展示 1-002-036-000 ==========
    ErrorCode RELATION_DISPLAY_TARGET_NOT_EXISTS = new ErrorCode(1_002_036_000, "关联目标实体不存在");
    ErrorCode RELATION_DISPLAY_FIELD_NOT_EXISTS = new ErrorCode(1_002_036_001, "展示字段不存在：{}");

    // ========== Model 关联管理 1-002-037-000 ==========
    ErrorCode MODEL_RELATION_NOT_EXISTS = new ErrorCode(1_002_037_000, "Model 关联不存在");
    ErrorCode MODEL_RELATION_ALREADY_EXISTS = new ErrorCode(1_002_037_001, "Model 关联已存在：{} -> {}");
    ErrorCode MODEL_RELATION_SELF_REFERENCE = new ErrorCode(1_002_037_002, "不能创建自引用关联");
    ErrorCode MODEL_RELATION_FIELD_CREATE_FAILED = new ErrorCode(1_002_037_003, "创建关联字段失败：{}");

    // ========== 字段模板 1-002-038-000 ==========
    ErrorCode TEMPLATE_NOT_EXISTS = new ErrorCode(1_002_038_000, "字段模板不存在");
    ErrorCode TEMPLATE_NAME_DUPLICATE = new ErrorCode(1_002_038_001, "模板名称已存在：{}");
    ErrorCode TEMPLATE_CODE_DUPLICATE = new ErrorCode(1_002_038_002, "模板编码已存在：{}");
    ErrorCode TEMPLATE_SYSTEM_CANNOT_DELETE = new ErrorCode(1_002_038_003, "系统预设模板不允许删除");
    ErrorCode TEMPLATE_FIELD_ASSIGNMENT_NOT_EXISTS = new ErrorCode(1_002_038_004, "模板字段分配不存在");
    ErrorCode TEMPLATE_FIELD_ALREADY_ASSIGNED = new ErrorCode(1_002_038_005, "字段已分配到该模板");

    // ========== EntityType 关联 1-002-039-000 ==========
    ErrorCode BUSINESS_TYPE_RELATION_NOT_EXISTS = new ErrorCode(1_002_039_000, "业务类型关联不存在");
    ErrorCode BUSINESS_TYPE_RELATION_ALREADY_EXISTS = new ErrorCode(1_002_039_001, "业务类型 {} 与 {} 的关联已存在");
    ErrorCode BUSINESS_TYPE_RELATION_SELF_REFERENCE = new ErrorCode(1_002_039_002, "不能创建自关联");
    ErrorCode BUSINESS_TYPE_NOT_EXISTS_FOR_RELATION = new ErrorCode(1_002_039_003, "业务类型不存在：{}");
    ErrorCode BUSINESS_TYPE_RELATION_EXPAND_FAILED = new ErrorCode(1_002_039_004, "关联展开失败：{}");

    // ========== 双向关联查询 1-002-044-000 ==========
    ErrorCode BIDIRECTIONAL_ENTITY_NOT_EXISTS = new ErrorCode(1_002_044_000, "实体不存在：{}");
    ErrorCode BIDIRECTIONAL_MODEL_NOT_EXISTS = new ErrorCode(1_002_044_001, "Model 不存在：{}");
    ErrorCode BIDIRECTIONAL_FIELD_NOT_ENTITY_REF = new ErrorCode(1_002_044_002, "字段 {} 不是关联字段类型");
    ErrorCode BIDIRECTIONAL_AGGREGATE_TYPE_NOT_SUPPORTED = new ErrorCode(1_002_044_003, "不支持的聚合类型：{}");
    ErrorCode BIDIRECTIONAL_CACHE_WARMUP_FAILED = new ErrorCode(1_002_044_004, "缓存预热失败：{}");

    // ========== 页面配置 1-002-045-000 ==========
    ErrorCode PAGE_CONFIG_NOT_EXISTS = new ErrorCode(1_002_045_000, "页面配置不存在");
    ErrorCode PAGE_CONFIG_MENU_ID_DUPLICATE = new ErrorCode(1_002_045_001, "该菜单已存在页面配置");
    ErrorCode PAGE_CONFIG_PAGE_NAME_EMPTY = new ErrorCode(1_002_045_002, "页面名称不能为空");
    ErrorCode PAGE_CONFIG_PAGE_NAME_TOO_LONG = new ErrorCode(1_002_045_003, "页面名称不能超过50个字符");
    ErrorCode PAGE_CONFIG_BUSINESS_TYPE_EMPTY = new ErrorCode(1_002_045_004, "业务类型不能为空");
    ErrorCode PAGE_CONFIG_TAB_LIST_EMPTY = new ErrorCode(1_002_045_005, "至少需要配置1个Tab");
    ErrorCode PAGE_CONFIG_TAB_NAME_EMPTY = new ErrorCode(1_002_045_006, "Tab名称不能为空");
    ErrorCode PAGE_CONFIG_TAB_NAME_TOO_LONG = new ErrorCode(1_002_045_007, "Tab名称不能超过20个字符");
    ErrorCode PAGE_CONFIG_TAB_NAME_DUPLICATE = new ErrorCode(1_002_045_008, "Tab名称重复: {}");
    ErrorCode PAGE_CONFIG_FIELD_LIST_EMPTY = new ErrorCode(1_002_045_009, "如果配置字段列表，至少需要选择1个字段");
    ErrorCode PAGE_CONFIG_PAGE_CODE_EXISTS = new ErrorCode(1_002_045_010, "页面代码已存在: {}");

    // ========== 业务管理Portal 1-002-046-000 ==========
    ErrorCode BUSINESS_PORTAL_CODE_EXISTS = new ErrorCode(1_002_046_001, "业务类型代码已存在: {}");
    ErrorCode BUSINESS_PORTAL_NAME_EXISTS = new ErrorCode(1_002_046_002, "业务名称已存在: {}");
    ErrorCode BUSINESS_PORTAL_NOT_EXISTS = new ErrorCode(1_002_046_003, "业务类型不存在");
    ErrorCode BUSINESS_PORTAL_CODE_INVALID = new ErrorCode(1_002_046_004, "业务类型代码格式不正确");
    ErrorCode BUSINESS_PORTAL_NAME_INVALID = new ErrorCode(1_002_046_005, "业务名称格式不正确");

    // ========== 分类实体链接 1-002-047-000 ==========
    ErrorCode CATEGORY_ENTITY_LINK_NOT_FOUND = new ErrorCode(1_002_047_000, "分类实体链接不存在");
    ErrorCode ENTITY_NOT_EXISTS = new ErrorCode(1_002_047_001, "实体不存在");
    ErrorCode ASSOCIATION_OPERATION_FAILED = new ErrorCode(1_002_047_002, "关联操作失败：{}");

    // ========== 引用 Provider 1-002-048-000 ==========
    ErrorCode REF_PROVIDER_NOT_EXISTS = new ErrorCode(1_002_048_000, "引用 Provider 不存在或未启用");
    ErrorCode REF_PROVIDER_CODE_EXISTS = new ErrorCode(1_002_048_001, "引用 Provider 编码已存在：{}");
    ErrorCode REF_PROVIDER_NOT_SUPPORTED = new ErrorCode(1_002_048_002, "暂不支持的引用 Provider：{}");
    ErrorCode REF_PROVIDER_FIELD_CODE_REQUIRED = new ErrorCode(1_002_048_003, "字段编码不能为空");
    ErrorCode REF_PROVIDER_FIELD_NOT_EXISTS = new ErrorCode(1_002_048_004, "字段不存在：{}");
    ErrorCode REF_PROVIDER_FIELD_MISSING_PROVIDER = new ErrorCode(1_002_048_005, "字段未配置 providerCode：{}");

}
