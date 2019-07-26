package com.xeathen.springbucks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Map;

@Slf4j
//@EnableTransactionManagement
@SpringBootApplication
public class SpringBucksApplication implements ApplicationRunner {

    @Autowired
    private JedisPool jedisPool; //自动注入JedisPool实例

    @Autowired
    private JedisPoolConfig jedisPoolConfig; //自动注入JedisPoolConfig实例

    public static void main(String[] args) {
        SpringApplication.run(SpringBucksApplication.class, args);
    }


    @Bean
    @ConfigurationProperties(prefix = "redis") //读取redis配置
    public JedisPoolConfig jedisPoolConfig() {
        return new JedisPoolConfig();
    }

    @Bean(destroyMethod = "close") //Pool关闭时调用close方法
    public JedisPool jedisPool(@Value("${redis.host}") String host) {
        return new JedisPool(jedisPoolConfig(), host);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info(jedisPoolConfig.toString()); //打印配置信息

        try (Jedis jedis = jedisPool.getResource()) { //在try的{}方法块运行结束时会将jedis自动关闭
            //利用jedis插入哈希表数据
            jedis.hset("spring-menu",
                    "porsche",
                    "100w");
            jedis.hset("spring-menu",
                    "toyota",
                    "20w");
            Map<String, String> menu = jedis.hgetAll("spring-menu"); //取数据
            log.info("Menu:{} ", menu);
            String price = jedis.hget("spring-menu", "porsche");
            log.info("audi - {}", price);

        }
    }
}
