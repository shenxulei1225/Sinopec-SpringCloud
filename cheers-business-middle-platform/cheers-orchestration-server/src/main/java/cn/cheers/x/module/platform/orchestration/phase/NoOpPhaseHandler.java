package cn.cheers.x.module.platform.orchestration.phase;

import org.springframework.stereotype.Component;

/**
 * 空实现阶段处理器，供模板占位或测试使用。
 */
@Component
public class NoOpPhaseHandler implements PhaseHandler {

    public static final String HANDLER_ID = "platform.noop";

    @Override
    public String handlerId() {
        return HANDLER_ID;
    }

    @Override
    public void execute(PhaseContext context) {
        // no-op
    }
}
