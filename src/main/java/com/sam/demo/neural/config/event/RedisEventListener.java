package com.sam.demo.neural.config.event;

import com.sam.demo.neural.extension.Extension;

/**
 * The Redis Event Notify.
 *
 * @author lry
 **/
@Extension("redis")
public class RedisEventListener implements IEventListener {

    private EventConfig eventConfig;

    @Override
    public void initialize(EventConfig eventConfig) {
        this.eventConfig = eventConfig;
    }

    @Override
    public void onEvent(IEventType eventType, Object object) {

    }

    @Override
    public void destroy() {

    }

}
