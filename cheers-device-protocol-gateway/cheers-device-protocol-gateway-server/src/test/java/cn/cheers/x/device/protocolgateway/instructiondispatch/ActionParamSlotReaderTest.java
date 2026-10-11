package cn.cheers.x.device.protocolgateway.instructiondispatch;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ActionParamSlotReaderTest {

    @Test
    void searchlightSelectionFillsPsdkIndex() {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("selected_searchlight", "99-0-2");
        ActionParamSlotReader.writeSpeakerIndexFromSelectedSpeaker(params);
        assertEquals(2, params.get("psdk_index"));
    }

    @Test
    void spokenTextFillsMd5() {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("speaker_tts_text", "注意安全");
        ActionParamSlotReader.writeSpeakerTtsMd5(params);
        assertEquals("335458c97189d2457dc48477cd9de7dd", params.get("speaker_tts_md5"));
    }

    @Test
    void blankTextDoesNotInventMd5() {
        Map<String, Object> params = new LinkedHashMap<>();
        ActionParamSlotReader.writeSpeakerTtsMd5(params);
        assertFalse(params.containsKey("speaker_tts_md5"));
    }
}
