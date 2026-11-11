package cn.cheers.x.module.dynamicbusiness.dataprocess;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 安防在数据管理左侧是一组目录。
 * 物防使用设备账，技防使用告警账。
 */
class SecurityCatalogEnsureTest {

    @Test
    void catalogsAppearUnderSecurityGroupAndReuseExistingBooks() {
        List<SecurityCatalogEnsure.Catalog> catalogs = SecurityCatalogEnsure.catalogs();
        assertEquals("安防管理", SecurityCatalogEnsure.GROUP_NAME);
        assertEquals(List.of(
                "人员管理", "巡检", "有线电话", "无线通讯", "视频监控", "周界报警",
                "入侵检测", "门禁", "广播", "安全资源位置", "人员识别", "车辆定位", "行为识别"),
                catalogs.stream().map(SecurityCatalogEnsure.Catalog::name).toList());
        assertTrue(catalogs.stream()
                .filter(item -> List.of("有线电话", "无线通讯", "视频监控", "周界报警", "入侵检测", "门禁", "广播", "安全资源位置")
                        .contains(item.name()))
                .allMatch(item -> "equipment".equals(item.baseCode())));
        assertTrue(catalogs.stream()
                .filter(item -> List.of("人员识别", "车辆定位", "行为识别").contains(item.name()))
                .allMatch(item -> "alarm".equals(item.baseCode())));
        assertEquals("user_account", catalog(catalogs, "人员管理").baseCode());
        assertEquals("task", catalog(catalogs, "巡检").baseCode());
    }

    private static SecurityCatalogEnsure.Catalog catalog(List<SecurityCatalogEnsure.Catalog> catalogs, String name) {
        return catalogs.stream().filter(item -> name.equals(item.name())).findFirst().orElseThrow();
    }
}
