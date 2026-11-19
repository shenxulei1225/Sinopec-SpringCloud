package cn.cheers.x.module.dynamicbusiness.dataprocess;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

    @Test
    void updateKeepsTheDeclaredPlaceAlreadyOnTheAlarm() {
        AlarmOwnershipCopy.Hop campus = new AlarmOwnershipCopy.Hop("campus_id", "校区", "campus", true);
        Map<String, Object> base = new LinkedHashMap<>();
        AlarmOwnershipCopy.writeRefs(base, AlarmOwnershipCopy.fromStored(Map.of("campus_id", 5L), List.of(campus), "这条告警没有填写"));
        Map<?, ?> ref = (Map<?, ?>) base.get("campus_id");
        assertEquals("campus", ref.get("entityTypeCode"));
        assertEquals(5L, ref.get("id"));
    }

    @Test
    void updateDoesNotCopyAFieldTheCatalogDidNotDeclare() {
        AlarmOwnershipCopy.Hop campus = new AlarmOwnershipCopy.Hop("campus_id", "校区", "campus", false);
        Map<String, Object> base = new LinkedHashMap<>();
        AlarmOwnershipCopy.writeRefs(base,
                AlarmOwnershipCopy.fromStored(Map.of("facility_id", 1L), List.of(campus), "这条告警没有填写"));
        assertFalse(base.containsKey("facility_id"));
        assertFalse(base.containsKey("campus_id"));
    }

    @Test
    void requiredPlaceMissingOnTheDeviceIsRefused() {
        AlarmOwnershipCopy.Hop campus = new AlarmOwnershipCopy.Hop("campus_id", "校区", "campus", true);
        assertThrows(Exception.class, () -> AlarmOwnershipCopy.fromDevice(Map.of(), Map.of(), List.of(campus)));
    }
}
