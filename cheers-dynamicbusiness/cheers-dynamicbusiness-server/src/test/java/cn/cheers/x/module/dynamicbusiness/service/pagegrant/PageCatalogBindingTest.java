package cn.cheers.x.module.dynamicbusiness.service.pagegrant;

import cn.cheers.x.module.dynamicbusiness.service.cataloggrant.CatalogRoleGrantItem;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class PageCatalogBindingTest {

    @Test
    void inspectionMaintainDoesNotGrantBaseTaskOrSchedule() {
        Set<String> codes = codesOf(derive("inspection-task", "view", "maintain"));
        assertThat(codes).contains("task_patrol", "task_record_patrol");
        assertThat(codes).doesNotContain("task", "schedule");
        assertThat(operations("task_patrol", derive("inspection-task", "view", "maintain")))
                .contains("query", "create", "update", "delete");
    }

    @Test
    void alarmPagesGrantTheirOwnCatalogs() {
        assertThat(codesOf(derive("security-alarm", "view"))).containsExactly("alarm");
        List<CatalogRoleGrantItem> linkage = derive("alarm-linkage", "view");
        assertThat(codesOf(linkage)).containsExactly("linkage_alarm");
        assertThat(operations("linkage_alarm", linkage)).containsExactly("query");
        assertThat(codesOf(linkage)).doesNotContain("linkage");
    }

    @Test
    void analysisDoesNotGrantLinkage() {
        assertThat(codesOf(derive("analysis-job", "view", "maintain")))
                .containsExactly("anomaly");
    }

    @Test
    void organizationSplitsDepartmentAndPersonWrites() {
        List<CatalogRoleGrantItem> grants = derive("organization", "view", "maintain-dept");
        assertThat(operations("organization", grants)).containsExactly("query");
        assertThat(operations("department", grants)).contains("query", "create", "update", "delete");
        assertThat(operations("user_account", grants)).containsExactly("query");
        assertThat(codesOf(grants)).doesNotContain("duty_post");
    }

    @Test
    void pageWithoutCatalogProducesNothing() {
        assertThat(derive("live-monitor", "view", "command")).isEmpty();
    }

    private static List<CatalogRoleGrantItem> derive(String pageCode, String... actions) {
        return PageCatalogBinding.derive(List.of(new PageRoleGrantItem(pageCode, List.of(actions))));
    }

    private static Set<String> codesOf(List<CatalogRoleGrantItem> grants) {
        return grants.stream().map(CatalogRoleGrantItem::registryCode).collect(Collectors.toSet());
    }

    private static List<String> operations(String code, List<CatalogRoleGrantItem> grants) {
        return grants.stream()
                .filter(item -> code.equals(item.registryCode()) && "entity".equals(item.subject()))
                .findFirst()
                .orElseThrow()
                .operations();
    }
}
