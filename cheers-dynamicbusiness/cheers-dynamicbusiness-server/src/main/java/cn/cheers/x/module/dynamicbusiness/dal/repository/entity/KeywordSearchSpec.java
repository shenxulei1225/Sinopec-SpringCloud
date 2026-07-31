package cn.cheers.x.module.dynamicbusiness.dal.repository.entity;

import java.util.List;

/**
 * 列表关键词多列搜索计划：已解析为可安全拼进 SQL 的列，及是否按 id 精确匹配。
 *
 * <p>由上层根据搜索字段编码（searchFieldCodes）与类型元数据解析；仓储只消费本结构。</p>
 */
public record KeywordSearchSpec(
        /** 对 keyword 做 LIKE/ILIKE 的核心列或物理列名（白名单校验后） */
        List<String> likeColumns,
        /** 关键词可解析为 Long 时是否 OR id = ? */
        boolean matchId,
        /** 无法下推实体表的扩展字段编码；非空时快路径不可仅靠实体表搜全范围 */
        List<String> extensionFieldCodes
) {
    public static KeywordSearchSpec nameOnly() {
        return new KeywordSearchSpec(List.of("name"), false, List.of());
    }

    public boolean canPushFullyToEntityTable() {
        return extensionFieldCodes == null || extensionFieldCodes.isEmpty();
    }

    /** 与历史「仅 name ILIKE」等价，可供仍只支持 name 的分支（如部分 EVA 排序 SQL）使用。 */
    public boolean isNameOnlyLike() {
        return !matchId
                && canPushFullyToEntityTable()
                && likeColumns != null
                && likeColumns.size() == 1
                && "name".equalsIgnoreCase(likeColumns.get(0));
    }
}
