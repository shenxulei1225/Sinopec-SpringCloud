package cn.cheers.x.module.dynamicbusiness.service.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 实体表直分页进门：筛/词/序职责边界（不发 SQL）。
 */
class EntityTableDirectPagingGateTest {

    @Test
    void facilityFilterAndSort_canPageOnEntityTable() {
        EntityTableDirectPagingGate.Decision d = EntityTableDirectPagingGate.decide(
                true,
                true,
                true,
                "sort",
                false);
        assertTrue(d.canPageOnEntityTable());
        assertEquals("sort", d.effectiveDbOrderColumn());
    }

    @Test
    void unpushableFilters_notEligible() {
        EntityTableDirectPagingGate.Decision d = EntityTableDirectPagingGate.decide(
                false,
                true,
                true,
                "sort",
                false);
        assertFalse(d.canPageOnEntityTable());
    }

    @Test
    void scopeWithoutDbOrder_fallsBackToId() {
        EntityTableDirectPagingGate.Decision d = EntityTableDirectPagingGate.decide(
                true,
                true,
                true,
                null,
                true);
        assertTrue(d.canPageOnEntityTable());
        assertEquals("id", d.effectiveDbOrderColumn());
    }

    @Test
    void extensionFieldOrderWithoutDbColumn_notEligible() {
        // 列表禁止扩展字段序：无实体表列、无划分 → 不能直分页
        EntityTableDirectPagingGate.Decision d = EntityTableDirectPagingGate.decide(
                true,
                true,
                true,
                null,
                false);
        assertFalse(d.canPageOnEntityTable());
    }
}
