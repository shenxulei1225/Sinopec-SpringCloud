package cn.cheers.x.module.platform.orchestration.phase;

/**
 * 编排阶段处理器 SPI。
 */
public interface PhaseHandler {

    String handlerId();

    void execute(PhaseContext context);
}
