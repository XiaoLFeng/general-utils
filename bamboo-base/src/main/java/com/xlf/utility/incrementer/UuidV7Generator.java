package com.xlf.utility.incrementer;

import com.xlf.utility.utility.RandomUtil;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.github.f4b6a3.uuid.UuidCreator;

/**
 * UUID v7 生成器
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@SuppressWarnings("unused")
public class UuidV7Generator implements IdentifierGenerator {
    @Override
    public Number nextId(Object entity) {
        return Integer.parseInt(RandomUtil.createRandomInt(6));
    }

    @Override
    public String nextUUID(Object entity) {
        return UuidCreator.getTimeOrderedEpoch().toString();
    }
}
