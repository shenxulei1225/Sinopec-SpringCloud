package cn.cheers.x.module.dynamicbusiness.service.alarm;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AlarmRecordAccessTest {

    @Test
    void emptyRequestKeepsNothing() {
        assertTrue(AlarmRecordAccess.keepRequested(List.of(), Set.of(1L)).isEmpty());
        assertTrue(AlarmRecordAccess.keepRequested(null, Set.of(1L)).isEmpty());
    }

    @Test
    void dropsCategoryOutsideTheAllowedSet() {
        assertEquals(List.of(2L), AlarmRecordAccess.keepRequested(List.of(2L, 9L), Set.of(2L, 3L)));
    }
}
