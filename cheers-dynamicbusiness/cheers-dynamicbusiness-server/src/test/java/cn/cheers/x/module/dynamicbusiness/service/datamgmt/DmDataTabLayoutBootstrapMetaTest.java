package cn.cheers.x.module.dynamicbusiness.service.datamgmt;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DmDataTabLayoutBootstrapMetaTest {

    @Test
    void detailColumnUsesTypeDisplayName() {
        assertEquals("事件主题", DmDataTabLayoutBootstrapMeta.detailInstanceLabel("事件主题", "event_topic"));
        assertEquals("事件主题", DmDataTabLayoutBootstrapMeta.detailInstanceLabel("  事件主题  ", "event_topic"));
    }

    @Test
    void detailColumnUsesTypeCodeWhenDisplayNameMissing() {
        assertEquals("event_topic", DmDataTabLayoutBootstrapMeta.detailInstanceLabel("  ", "event_topic"));
        assertEquals("event_topic", DmDataTabLayoutBootstrapMeta.detailInstanceLabel(null, "event_topic"));
    }
}
