package cn.cheers.x.module.dynamicbusiness.dataprocess;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AlarmRecordWriterNamedRefTest {

    @Test
    void namedEntityRefWritesIdAndName() {
        Map<String, Object> ref = AlarmRecordWriter.namedEntityRef("equipment", 9L, "1号水泵");
        assertEquals("equipment", ref.get("entityTypeCode"));
        assertEquals(9L, ref.get("id"));
        assertEquals("1号水泵", ref.get("name"));
    }

    @Test
    void namedEntityRefRejectsBlankName() {
        assertThrows(Exception.class, () -> AlarmRecordWriter.namedEntityRef("equipment", 9L, "  "));
    }
}
