package com.xlf.utility.utility;

import com.xlf.utility.models.entity.dto.SnowflakeInfoDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@code SnowflakeUtilTest} 是对 {@code SnowflakeUtil} 类的单元测试类。
 *
 * @author XiaoLFeng
 * @version 1.1.8
 * @since 1.1.8
 */
class SnowflakeUtilTest {

    @Test
    void testGenerateId_Basic() {
        long id1 = SnowflakeUtil.generateId();
        long id2 = SnowflakeUtil.generateId();

        System.out.println("Generated ID 1: " + id1);
        System.out.println("Generated ID 2: " + id2);

        assertTrue(id1 > 0, "ID should be positive");
        assertTrue(id2 > id1, "IDs should be increasing");
    }

    @Test
    void testParseInfo() {
        long id = SnowflakeUtil.generateId();
        SnowflakeInfoDTO info = SnowflakeUtil.parseInfo(id);

        System.out.println("Snowflake Info: " + info);

        assertNotNull(info);
        assertEquals(id, info.getId());
        assertNotNull(info.getGeneratedTime());
    }

    @Test
    void testGenerateIdString() {
        String idStr = SnowflakeUtil.generateIdString();
        System.out.println("Generated ID String: " + idStr);

        assertNotNull(idStr);
        assertFalse(idStr.isEmpty());

        long id = Long.parseLong(idStr);
        assertTrue(id > 0);
    }
}
