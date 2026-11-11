package cn.cheers.x.module.dynamicbusiness.dataprocess;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 安防告警挂在告警目录的分类树上。
 * 人员管理和巡检不是告警类型。
 */
class AlarmTypeCatalogSecurityTest {

    @Test
    void securityAlarmsHangUnderOneParent() {
        List<AlarmTypeCatalog.Node> nodes = AlarmTypeCatalog.nodes();
        AlarmTypeCatalog.Node parent = nodes.stream()
                .filter(node -> "alarm-security".equals(node.code()))
                .findFirst()
                .orElseThrow();
        assertEquals("安防告警", parent.name());
        assertNull(parent.parentCode());
        List<String> children = nodes.stream()
                .filter(node -> "alarm-security".equals(node.parentCode()))
                .map(AlarmTypeCatalog.Node::name)
                .toList();
        assertEquals(List.of(
                "视频监控", "周界报警", "入侵检测", "门禁", "有线电话",
                "无线通讯", "广播", "人员识别", "车辆定位", "行为识别"), children);
        assertTrue(nodes.stream().noneMatch(node -> "人员管理".equals(node.name())));
        assertTrue(nodes.stream().noneMatch(node -> "巡检".equals(node.name())));
    }
}
