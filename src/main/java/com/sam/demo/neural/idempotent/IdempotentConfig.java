package com.sam.demo.neural.idempotent;

import lombok.*;
import com.sam.demo.neural.config.GlobalConfig;
import com.sam.demo.neural.config.RuleConfig;

/**
 * The Idempotent Config.
 *
 * @author lry
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class IdempotentConfig extends RuleConfig {

    private static final long serialVersionUID = 4076904823256002967L;

    /**
     * The model of limiter
     */
    private GlobalConfig.Model model = GlobalConfig.Model.STAND_ALONE;

    // === concurrent limiter


}
