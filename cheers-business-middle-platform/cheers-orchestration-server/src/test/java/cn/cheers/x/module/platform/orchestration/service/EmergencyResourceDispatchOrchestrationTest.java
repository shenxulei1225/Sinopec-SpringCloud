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

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.module.platform.orchestration.enums.ErrorCodeConstants.ORCHESTRATION_PAYLOAD_INVALID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmergencyResourceDispatchOrchestrationTest {

    private OrchestrationRunner runner;
    private final AtomicInteger validateCalls = new AtomicInteger();
    private final AtomicInteger expandCalls = new AtomicInteger();
    private final AtomicInteger solveCalls = new AtomicInteger();
    private final AtomicInteger persistCalls = new AtomicInteger();

    @BeforeEach
    void setUp() {
        validateCalls.set(0);
        expandCalls.set(0);
        solveCalls.set(0);
        persistCalls.set(0);

        List<PhaseHandler> handlers = new ArrayList<>();
        handlers.add(new PhaseHandler() {
            @Override
            public String handlerId() {
                return "emergency.resource_dispatch.validate";
            }

            @Override
            public void execute(PhaseContext context) {
                validateCalls.incrementAndGet();
                Map<String, Object> payload = context.getOrchestrationRunRequest().getPayload();
                if (payload == null || payload.get("eventId") == null || payload.get("resourceId") == null) {
                    throw exception(ORCHESTRATION_PAYLOAD_INVALID);
                }
            }
        });
        handlers.add(new PhaseHandler() {
            @Override
            public String handlerId() {
                return "emergency.resource_dispatch.expand";
            }

            @Override
            public void execute(PhaseContext context) {
                expandCalls.incrementAndGet();
                Map<String, Object> result = new HashMap<>();
                result.put("eventId", 1L);
                result.put("resourceId", 2L);
                result.put("solveSkipped", false);
                context.putAttr("expandResult", result);
            }
        });
        handlers.add(new PhaseHandler() {
            @Override
            public String handlerId() {
                return "emergency.resource_dispatch.solve";
            }

            @Override
            public void execute(PhaseContext context) {
                solveCalls.incrementAndGet();
                Map<String, Object> result = context.getAttr("expandResult");
                if (result == null) {
                    result = new HashMap<>();
                }
                result.put("solveSkipped", true);
                context.putAttr("expandResult", result);
            }
        });
        handlers.add(new PhaseHandler() {
            @Override
            public String handlerId() {
                return "emergency.resource_dispatch.persist";
            }

            @Override
            public void execute(PhaseContext context) {
                persistCalls.incrementAndGet();
                Map<String, Object> result = context.getAttr("expandResult");
                if (result == null) {
                    result = new HashMap<>();
                }
                result.put("dispatchId", 99L);
                context.putAttr("expandResult", result);
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
    void run_resourceDispatch_executesValidateExpandSolvePersist() {
        OrchestrationRunResponse resp = runner.run(OrchestrationRunRequest.builder()
                .orchestrationRef(OrchestrationRefs.EMERGENCY_RESOURCE_DISPATCH_V1)
                .scope("emergency")
                .payload(Map.of("eventId", 1L, "resourceId", 2L))
                .build());

        assertEquals(1, validateCalls.get());
        assertEquals(1, expandCalls.get());
        assertEquals(1, solveCalls.get());
        assertEquals(1, persistCalls.get());
        assertEquals("COMPLETED", resp.getStatus());
        assertEquals(99L, ((Number) resp.getResult().get("dispatchId")).longValue());
        assertTrue(Boolean.TRUE.equals(resp.getResult().get("solveSkipped")));
    }

    @Test
    void run_resourceDispatch_missingResourceId_fails() {
        assertThrows(Exception.class, () -> runner.run(OrchestrationRunRequest.builder()
                .orchestrationRef(OrchestrationRefs.EMERGENCY_RESOURCE_DISPATCH_V1)
                .payload(Map.of("eventId", 1L))
                .build()));
        assertEquals(1, validateCalls.get());
        assertEquals(0, expandCalls.get());
    }
}
