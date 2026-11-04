package cn.cheers.x.module.dynamicbusiness.service.event;

/**
 * 消息通道送达后，消费方自己声明能处理哪些事件。
 *
 * <p>生产方不持有这个接口，也不按它去调用联动。</p>
 */
public interface PlatformEventHandler {

    boolean supports(String eventKind);

    void handle(PlatformEventMessage message);
}
