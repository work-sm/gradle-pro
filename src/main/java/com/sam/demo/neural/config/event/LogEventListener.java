package com.sam.demo.neural.config.event;

import com.sam.demo.neural.common.utils.SerializeUtils;
import com.sam.demo.neural.extension.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Log Event Notify.
 *
 * @author lry
 **/
@Extension("log")
public class LogEventListener implements IEventListener {

    private Logger eventLog;
    private EventConfig eventConfig;

    @Override
    public void initialize(EventConfig eventConfig) {
        this.eventConfig = eventConfig;
        // build event log
        this.eventLog = LoggerFactory.getLogger(eventConfig.getLogName());
    }

    @Override
    public void onEvent(IEventType eventType, Object object) {
        if (eventConfig.isJsonLog()) {
            eventLog.info("{}", SerializeUtils.serialize(object));
        } else {
            eventLog.info("{}", object.toString());
        }
    }

    @Override
    public void destroy() {

    }

}
