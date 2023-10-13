package org.noear.dami.bus;

/**
 * 大米总线配置器
 *
 * @author noear
 * @since 1.0
 */
public interface DamiBusConfigurator<C, R> extends DamiBus<C, R> {
    /**
     * 配置响应超时
     *
     * @param timeout 超时（单位：毫秒）
     */
    DamiBusConfigurator<C, R> timeout(final long timeout);

    /**
     * 配置主题路由器
     */
    DamiBusConfigurator<C, R> topicRouter(TopicRouter<C, R> router);

    /**
     * 配置主题调度器
     */
    DamiBusConfigurator<C, R> topicDispatcher(TopicDispatcher<C, R> dispatcher);

    /**
     * 配置事件负载工厂
     */
    DamiBusConfigurator<C, R> payloadFactory(PayloadFactory<C, R> factory);
}
