package cn.cheers.x.module.dynamicbusiness.service.capability.system;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 系统业务能力注册定义（能力源来自 yudao system 模块，非 dynamic_entity_type）。
 */
@Getter
@Builder
public class SystemCapabilityDefinition {

    /** 业务类型编码，与 dataSource.entityTypeCode 一致，如 dept、user */
    private final String entityTypeCode;

    /** 展示名 */
    private final String entityTypeName;

    /** 列表读端点 URL */
    private final String readUrl;

    /** HTTP 方法 */
    private final String readMethod;

    /** 是否分页（pageNo/pageSize） */
    private final boolean paginated;

    /** 展示字段 fieldKey 列表 */
    private final List<SystemFieldDefinition> displayFields;

    /** 筛选项 */
    private final List<SystemFieldDefinition> filterFields;

    @Getter
    @Builder
    public static class SystemFieldDefinition {
        private final String fieldKey;
        private final String label;
        private final boolean searchable;
    }
}
