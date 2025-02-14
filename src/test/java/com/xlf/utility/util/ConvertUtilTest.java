package com.xlf.utility.util;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ConvertUtilTest {

    @Test
    public void convertObjectToMap_ShouldConvertCorrectly() {
        // 创建一个测试对象
        TestObject testObject = new TestObject();
        testObject.setName("Test Name");
        testObject.setAge(25);
        testObject.setTimestamp(new Timestamp(System.currentTimeMillis()));

        // 使用 ConvertUtil 转换对象
        Map<String, Object> map = ConvertUtil.convertObjectToMap(testObject);

        // 验证转换结果
        assertEquals("Test Name", map.get("name"));
        assertEquals(25, map.get("age"));
        assertNotNull(map.get("timestamp"));
    }

    @Test
    public void convertObjectToMapString_ShouldConvertCorrectly() {
        // 创建一个测试对象
        TestObject testObject = new TestObject();
        testObject.setName("Test Name");
        testObject.setAge(25);
        testObject.setTimestamp(new Timestamp(System.currentTimeMillis()));

        // 使用 ConvertUtil 转换对象
        Map<String, String> map = ConvertUtil.convertObjectToMapString(testObject);

        // 验证转换结果
        assertEquals("Test Name", map.get("name"));
        assertEquals("25", map.get("age"));
        assertNotNull(map.get("timestamp"));
    }

    // 创建一个简单的测试对象
    public static class TestObject {
        private String name;
        private int age;
        private Timestamp timestamp;

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public Timestamp getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Timestamp timestamp) {
            this.timestamp = timestamp;
        }
    }
}
