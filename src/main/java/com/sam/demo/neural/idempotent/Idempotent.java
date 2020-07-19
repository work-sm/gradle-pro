package com.sam.demo.neural.idempotent;

import lombok.extern.slf4j.Slf4j;
import com.sam.demo.neural.AbstractNeural;
import com.sam.demo.neural.NeuralContext;
import com.sam.demo.neural.OriginalCall;
import com.sam.demo.neural.common.utils.StreamUtils;
import com.sam.demo.neural.config.store.RedisStore;
import com.sam.demo.neural.extension.Extension;

import java.util.ArrayList;
import java.util.List;

/**
 * Idempotent
 *
 * @author lry
 */
@Slf4j
@Extension(IdempotentGlobalConfig.IDENTITY)
public class Idempotent extends AbstractNeural<IdempotentConfig, IdempotentGlobalConfig> {

    private static String IDEMPOTENT_SCRIPT = StreamUtils.loadScript("/script/idempotent.lua");

    @Override
    public Object wrapperCall(NeuralContext neuralContext, String identity, OriginalCall originalCall) throws Throwable {
        List<Object> keys = new ArrayList<>();
        keys.add(neuralContext.getId());
        // might contain
        List<Object> result = RedisStore.INSTANCE.eval(IDEMPOTENT_SCRIPT, globalConfig.getTimeout(), keys);
        if (result == null || result.size() != 1) {
            return super.wrapperCall(neuralContext, identity, originalCall);
        } else {
            if ((Boolean) result.get(0)) {
                throw new RuntimeException();
            }

            return super.wrapperCall(neuralContext, identity, originalCall);
        }
    }

}
