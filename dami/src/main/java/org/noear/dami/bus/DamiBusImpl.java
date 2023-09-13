package org.noear.dami.bus;

import org.noear.dami.bus.impl.AcceptorCallback;
import org.noear.dami.bus.impl.AcceptorResponse;
import org.noear.dami.bus.impl.PayloadImpl;
import org.noear.dami.bus.impl.TopicRouterImpl;
import org.noear.dami.exception.DamiException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 大米总线实现
 *
 * @author noear
 * @since 1.0
 */
public class DamiBusImpl<C, R> implements DamiBus<C, R> {
    /**
     * 路由器
     */
    private final TopicRouter<C, R> router = new TopicRouterImpl<>();

    /**
     * 超时：默认3s
     */
    private long timeout = 3000;

    /**
     * 获取超时
     */
    @Override
    public long getTimeout() {
        return timeout;
    }

    /**
     * 设置超时
     */
    @Override
    public void setTimeout(final long timeout) {
        this.timeout = timeout;
    }

    /**
     * 拦截
     *
     * @param index       顺序位
     * @param interceptor 拦截器
     */
    @Override
    public void intercept(int index, Interceptor interceptor) {
        router.addInterceptor(index, interceptor);
    }

    /**
     * 发送（不需要答复）
     *
     * @param topic   主题
     * @param content 内容
     */
    @Override
    public void send(final String topic, final C content) {
        router.handle(new PayloadImpl<>(topic, content, null));
    }

    /**
     * 发送并等待响应
     *
     * @param topic   主题
     * @param content 内容
     */
    @Override
    public R sendAndResponse(final String topic, final C content) {
        CompletableFuture<R> future = new CompletableFuture<>();
        PayloadImpl<C,R> payload = new PayloadImpl<>(topic, content, new AcceptorResponse<>(future));
        router.handle(payload);

        try {
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (Throwable e) {
            throw new DamiException(e);
        }
    }

    /**
     * 发送并等待回调
     *
     * @param topic    主题
     * @param content  内容
     * @param callback 回调函数
     */
    @Override
    public void sendAndCallback(final String topic, final C content, final Consumer<R> callback) {
        PayloadImpl<C,R> payload = new PayloadImpl<>(topic, content, new AcceptorCallback<>(callback));

        router.handle(payload);
    }

    /**
     * 监听
     *
     * @param topic    主题
     * @param index    顺序位
     * @param listener 监听
     */
    @Override
    public void listen(final String topic, final int index, final TopicListener<Payload<C, R>> listener) {
        router.add(topic, index, listener);
    }

    /**
     * 取消监听
     *
     * @param topic    主题
     * @param listener 监听
     */
    @Override
    public void unlisten(final String topic, final TopicListener<Payload<C, R>> listener) {
        router.remove(topic, listener);
    }
}