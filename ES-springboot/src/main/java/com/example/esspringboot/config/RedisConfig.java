package com.example.esspringboot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    /**
             ┌─────────────────────────────────────────────────────────────────┐
             │                    Spring Boot 启动                              │
             │  读取 application.properties 中的 Redis 配置                       │
             └───────────────────────┬─────────────────────────────────────────┘
             ↓
             ┌─────────────────────────────────────────────────────────────────┐
             │  自动创建 RedisConnectionFactory 实现类                            │
             │  (默认是 LettuceConnectionFactory)                               │
             └───────────────────────┬─────────────────────────────────────────┘
             ↓
             ┌─────────────────────────────────────────────────────────────────┐
             │  注入到我们的 redisTemplate() 方法中                                │
             │  template.setConnectionFactory(factory);                        │
             └───────────────────────┬─────────────────────────────────────────┘
             ↓
             ┌─────────────────────────────────────────────────────────────────┐
             │  RedisTemplate 就能通过它获取 Redis 连接，执行各种操作                 │
             │  (set/get/hash/incr/decr...)                                    │
             └─────────────────────────────────────────────────────────────────┘
     方法	                                       作用	                            Redis命令

     set(key, value)	                         设置键值对	                        SET key value
     get(key)	                                获取值	                            GET key
     setIfAbsent(key, value, timeout, unit)	    不存在时才设置（分布式锁核心）	        SET key value NX EX
     getAndSet(key, value)	                    获取旧值并设置新值	                    GETSET key value
     increment(key)	                            原子递增	                            INCR key
     decrement(key)	                            原子递减	                            DECR key
     append(key, value)	                        追加字符串	                        APPEND key value
     getRange(key, start, end)	                获取子字符串	                        GETRANGE key start end
     size(key)	                                获取字符串长度	                        STRLEN key
     multiSet(map)	                            批量设置	                            MSET key1 value1 key2 value2
     multiGet(keys)	                            批量获取	                            MGET key1 key2

     opsForValue() 就是专门用来操作 String 类型的入口方法 比如opsForValue().set("name", "张三")
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {//泛型<String, Object>表示key为String，value为Object
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);//设置Redis连接工厂，用于获取Redis连接


        // 创建 ObjectMapper 并注册 Java 8 时间模块   我们需要注册 JavaTimeModule 来支持 LocalDateTime 等时间类型
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());  // 支持 LocalDateTime
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);  // 格式化为 ISO 字符串而非时间戳


        //======这是字符串序列化器============================
        //Redis内部存储的是字节数组（byte[]）
        //如果直接把Java字符串存进去，可能会出现编码问题（如中文乱码）
        //StringRedisSerializer 会把字符串转换成标准的UTF-8字节数组
        //Redis的Key必须用字符串序列化器，否则会报错
        StringRedisSerializer stringSerializer = new StringRedisSerializer(); // 创建StringRedisSerializer实例，用于将字符串键序列化为UTF-8字节数组
        template.setKeySerializer(stringSerializer); // 设置Redis key的序列化器为StringRedisSerializer，确保key可读且无乱码
        template.setHashKeySerializer(stringSerializer); // 设置Redis hash结构中的field的序列化器为StringRedisSerializer


        //======这是JSON序列化器（支持对象存储）===========================
        // 把Java对象转换成JSON字符串，再存储到Redis中
        //                                                                                     传递objectMapper参数，确保使用我们注册的时间模块
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper); // 创建GenericJackson2JsonRedisSerializer实例，用于将Java对象序列化为JSON格式
        template.setValueSerializer(jsonSerializer); // 设置Redis value的序列化器为JSON序列化器，支持存储任意对象
        template.setHashValueSerializer(jsonSerializer); // 设置Redis hash结构中的value的序列化器为JSON序列化器

        template.afterPropertiesSet(); // 调用初始化方法，检查并确保所有必要属性已设置（如序列化器、连接工厂等）

        return template;
    }
}