package org.noear.dami.impl;

import org.noear.dami.TopicListener;
import org.noear.dami.TopicRouter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 主题路由器
 *
 * @author noear
 * @since 1.0
 */
public final class TopicRouterImpl<C, R> implements TopicRouter<C, R> {

    //===========
    private final Map<String, TopicListenPipeline<Payload<C, R>>> pipelineMap = new LinkedHashMap<>();

    /**
     * 添加监听
     */
    @Override
    public synchronized void add(final String topic, final int index, final TopicListener<Payload<C, R>> listener) {
        assertTopic(topic);

        final TopicListenPipeline<Payload<C, R>> pipeline = pipelineMap.computeIfAbsent(topic, t -> new TopicListenPipeline<>());

        pipeline.add(index, listener);
    }

    /**
     * 移除监听
     */
    @Override
    public synchronized void remove(final String topic, final TopicListener<Payload<C, R>> listener) {
        assertTopic(topic);

        final TopicListenPipeline<Payload<C, R>> pipeline = pipelineMap.get(topic);

        if (pipeline != null) {
            pipeline.remove(listener);
        }
    }


    /**
     * 接收事件并路由
     */
    @Override
    public void handle(final Payload<C, R> payload) {
        assertTopic(payload.getTopic());

        final TopicListenPipeline<Payload<C, R>> pipeline = pipelineMap.get(payload.getTopic());

        if (pipeline != null) {
            try {
                pipeline.onEvent(payload);
            } catch (Throwable e) {
                throw new IllegalStateException(e);
            }
        }
    }


}
