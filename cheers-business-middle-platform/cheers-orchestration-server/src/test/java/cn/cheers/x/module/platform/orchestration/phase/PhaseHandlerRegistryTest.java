package cn.cheers.x.module.platform.orchestration.phase;

import cn.cheers.x.framework.common.exception.ServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("PhaseHandlerRegistry 单元测试")
class PhaseHandlerRegistryTest {

    @Test
    @DisplayName("按 handlerId 解析成功")
    void require_knownHandlerId_returnsHandler() {
        NoOpPhaseHandler handler = new NoOpPhaseHandler();
        PhaseHandlerRegistry registry = new PhaseHandlerRegistry(List.of(handler));

        assertSame(handler, registry.require("platform.noop"));
    }

    @Test
    @DisplayName("重复 handlerId 注册失败")
    void duplicateHandlerId_failsFast() {
        PhaseHandler first = stubHandler("platform.dup");
        PhaseHandler second = stubHandler("platform.dup");

        assertThrows(IllegalStateException.class,
                () -> new PhaseHandlerRegistry(List.of(first, second)));
    }

    @Test
    @DisplayName("未知 handlerId 抛错")
    void require_unknownHandlerId_throws() {
        PhaseHandlerRegistry registry = new PhaseHandlerRegistry(List.of(new NoOpPhaseHandler()));

        assertThrows(ServiceException.class, () -> registry.require("platform.missing"));
    }

    private static PhaseHandler stubHandler(String handlerId) {
        return new PhaseHandler() {
            @Override
            public String handlerId() {
                return handlerId;
            }

            @Override
            public void execute(PhaseContext context) {
                // no-op
            }
        };
    }
}
