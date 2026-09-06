package cn.cheers.x.module.dynamicbusiness.service.entity;

import org.springframework.util.StringUtils;

/**
 * 实体列表「能不能在实体表上直接分页」的进门规则（纯判断，不发 SQL）。
 *
 * <p>管什么：筛是否可全部下推到实体表、关键词是否可表内搜、排序列是否已落到实体表、
 * 有无划分成员 → 能否 {@code WHERE + ORDER BY + LIMIT}；以及用哪一列排序。</p>
 *
 * <p>不管什么：场景 scene、分类/型号连线、具体 SQL、场站字段 ensure、扩展字段索引怎么查。</p>
 *
 * <p>禁止：再把 {@code sort} 当成不准表内分页的开关；{@code sort} 只出现在「排序列解析结果」里。
 * 列表禁止按扩展字段排序，故本门不提供 EVA 序路径。</p>
 */
public final class EntityTableDirectPagingGate {

    private EntityTableDirectPagingGate() {
    }

    /**
     * @param filtersPushable              筛全部可下推（或无筛）；false = 有不可下推筛
     * @param keywordPushableToEntityTable 无关键词，或关键词可整段下推到实体表
     * @param hasOrderByColumn             请求是否带了排序列
     * @param dbOrderColumn                已解析的实体表排序列（含 sort/name/id）；解析不到为 null
     * @param hasScopeMembership           是否带划分成员收窄
     */
    public static Decision decide(
            boolean filtersPushable,
            boolean keywordPushableToEntityTable,
            boolean hasOrderByColumn,
            String dbOrderColumn,
            boolean hasScopeMembership
    ) {
        if (!filtersPushable || !keywordPushableToEntityTable) {
            return Decision.notEligible();
        }
        boolean hasDbOrder = StringUtils.hasText(dbOrderColumn);
        // 有划分时：无实体序也可表内分页，ORDER BY 回退 id
        boolean orderOk = !hasOrderByColumn || hasDbOrder || hasScopeMembership;
        if (!orderOk) {
            return Decision.notEligible();
        }
        String effectiveDbOrder = hasDbOrder ? dbOrderColumn.trim() : "id";
        return new Decision(true, effectiveDbOrder);
    }

    /**
     * @param canPageOnEntityTable   true → 调用方走实体表直分页
     * @param effectiveDbOrderColumn ORDER BY 列（至少为 id）
     */
    public record Decision(
            boolean canPageOnEntityTable,
            String effectiveDbOrderColumn
    ) {
        static Decision notEligible() {
            return new Decision(false, null);
        }
    }
}
