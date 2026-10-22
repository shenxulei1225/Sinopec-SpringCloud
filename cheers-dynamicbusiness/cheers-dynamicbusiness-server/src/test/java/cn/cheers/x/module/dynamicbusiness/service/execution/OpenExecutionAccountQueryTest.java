package cn.cheers.x.module.dynamicbusiness.service.execution;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpenExecutionAccountQueryTest {

    @Test
    void sql_readsDropdownFieldsOnly() {
        String sql = OpenExecutionAccountQuery.sql("ent_task_excution_record_t1");
        assertTrue(sql.contains("custom_fields->>'execution_status'"));
        assertTrue(sql.contains("logicalDeviceId' = ?"));
        assertTrue(sql.contains("taskRecordIngestUrl"));
        assertTrue(sql.contains("piece->>'templateId'"));
        assertTrue(sql.contains("IN ('pending', 'in_progress')"));
        assertFalse(sql.contains("SELECT custom_fields"));
        assertFalse(sql.contains("packagesJson"));
    }
}
