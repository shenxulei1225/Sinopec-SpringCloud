package cn.cheers.x.module.platform.orchestration.service;

import cn.cheers.x.module.platform.orchestration.api.dto.OrchestrationRunRequest;
import cn.cheers.x.module.platform.orchestration.api.dto.OrchestrationRunResponse;
import cn.cheers.x.module.platform.orchestration.enums.OrchestrationRefs;
import cn.cheers.x.module.platform.orchestration.phase.PhaseContext;
import cn.cheers.x.module.platform.orchestration.phase.PhaseHandler;
import cn.cheers.x.module.platform.orchestration.phase.PhaseHandlerRegistry;
import cn.cheers.x.module.platform.orchestration.template.OrchestrationTemplateRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmergencyStartResponseOrchestrationTest {

    private OrchestrationRunner runner;
    private final AtomicInteger validateCalls = new AtomicInteger();
    private final AtomicInteger expandCalls = new AtomicInteger();
    private final AtomicInteger persistCalls = new AtomicInteger();

    @BeforeEach
    void setUp() {
        validateCalls.set(0);
        expandCalls.set(0);
        persistCalls.set(0);

        List<PhaseHandler> handlers = new ArrayList<>();
        handlers.add(new PhaseHandler() {
            @Override
            public String handlerId() {
                return "emergency.start_response.validate";
            }

            @Override
            public void execute(PhaseContext context) {
                validateCalls.incrementAndGet();
            }
        });
        handlers.add(new PhaseHandler() {
            @Override
            public String handlerId() {
                return "emergency.start_response.expand";
            }

            @Override
            public void execute(PhaseContext context) {
                expandCalls.incrementAndGet();
                Map<String, Object> result = new HashMap<>();
                result.put("responseNo", "RESP-TEST");
                context.putAttr("expandResult", result);
            }
        });
        handlers.add(new PhaseHandler() {
            @Override
            public String handlerId() {
                return "emergency.start_response.persist";
            }

            @Override
            public void execute(PhaseContext context) {
                persistCalls.incrementAndGet();
            }
        });

        OrchestrationRunner r = new OrchestrationRunner();
        try {
            var templateField = OrchestrationRunner.class.getDeclaredField("templateRegistry");
            templateField.setAccessible(true);
            templateField.set(r, new OrchestrationTemplateRegistry());
            var handlerField = OrchestrationRunner.class.getDeclaredField("phaseHandlerRegistry");
            handlerField.setAccessible(true);
            handlerField.set(r, new PhaseHandlerRegistry(handlers));
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
        this.runner = r;
    }

    @Test
    void run_emergencyStartResponse_executesValidateExpandPersistInOrder() {
        OrchestrationRunResponse resp = runner.run(OrchestrationRunRequest.builder()
                .orchestrationRef(OrchestrationRefs.EMERGENCY_START_RESPONSE_V1)
                .scope("emergency")
                .payload(Map.of("eventId", 1L, "planId", 2L, "responseLevel", "III"))
                .build());

        assertEquals(1, validateCalls.get());
        assertEquals(1, expandCalls.get());
        assertEquals(1, persistCalls.get());
        assertEquals("COMPLETED", resp.getStatus());
        assertEquals("RESP-TEST", resp.getResult().get("responseNo"));
    }

    @Test
    void run_unknownTemplate_throws() {
        assertThrows(Exception.class, () -> runner.run(OrchestrationRunRequest.builder()
                .orchestrationRef("orch.unknown")
                .build()));
    }
}
