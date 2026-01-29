package com.xlf.utility.utility;

import com.xlf.utility.models.dto.GeneSnowflakeInfoDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@code GeneSnowflakeUtilTest} 是对 {@code GeneSnowflakeUtil} 类的单元测试类。
 *
 * @author XiaoLFeng
 * @version 1.1.8
 * @since 1.1.8
 */
class GeneSnowflakeUtilTest {

    @Test
    void testGenerateId_WithBusinessIdentifier() {
        long userId = GeneSnowflakeUtil.generateId("user");
        long orderId = GeneSnowflakeUtil.generateId("order");
        long merchantId = GeneSnowflakeUtil.generateId("merchant");

        System.out.println("User ID: " + userId);
        System.out.println("Order ID: " + orderId);
        System.out.println("Merchant ID: " + merchantId);

        assertTrue(userId > 0);
        assertTrue(orderId > 0);
        assertTrue(merchantId > 0);

        // 不同业务标识生成不同基因
        int userGene = GeneSnowflakeUtil.parseGene(userId);
        int orderGene = GeneSnowflakeUtil.parseGene(orderId);
        int merchantGene = GeneSnowflakeUtil.parseGene(merchantId);

        System.out.println("User gene: " + userGene);
        System.out.println("Order gene: " + orderGene);
        System.out.println("Merchant gene: " + merchantGene);

        assertNotEquals(userGene, orderGene);
        assertNotEquals(orderGene, merchantGene);
        assertNotEquals(userGene, merchantGene);
    }

    @Test
    void testVerifyGene() {
        long userId = GeneSnowflakeUtil.generateId("user");

        assertTrue(GeneSnowflakeUtil.verifyGene(userId, "user"),
                "User ID should match 'user' identifier");
        assertFalse(GeneSnowflakeUtil.verifyGene(userId, "order"),
                "User ID should not match 'order' identifier");
    }

    @Test
    void testCalculateGene() {
        int gene1 = GeneSnowflakeUtil.calculateGene("user");
        int gene2 = GeneSnowflakeUtil.calculateGene("user");
        int gene3 = GeneSnowflakeUtil.calculateGene("order");

        System.out.println("Gene for 'user': " + gene1);
        System.out.println("Gene for 'order': " + gene3);

        assertEquals(gene1, gene2, "Same identifier should produce same gene");
        assertNotEquals(gene1, gene3, "Different identifiers should produce different genes");
    }

    @Test
    void testParseInfo() {
        long userId = GeneSnowflakeUtil.generateId("user");
        GeneSnowflakeInfoDTO info = GeneSnowflakeUtil.parseInfo(userId);

        System.out.println("GeneSnowflake Info: " + info);

        assertNotNull(info);
        assertEquals(userId, info.getId());
        assertNotNull(info.getGeneratedTime());
        assertTrue(info.getGene() >= 0 && info.getGene() <= 255);
    }

    @Test
    void testGetBitStructure() {
        String structure = GeneSnowflakeUtil.getBitStructure();
        System.out.println(structure);

        assertNotNull(structure);
        assertFalse(structure.isEmpty());
    }
}
