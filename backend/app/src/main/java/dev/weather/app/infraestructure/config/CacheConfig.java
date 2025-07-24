package dev.weather.app.infraestructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.weather.app.domain.ports.out.CachePort;
import dev.weather.app.infraestructure.adapters.RedisCacheAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class CacheConfig {
    @Value("${spring.redis.host}")
    private  String host;

    @Value("${spring.redis.password}")
    private  String password;
    @Bean
    @Primary
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {

        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(19923);
        config.setUsername("default");
        config.setPassword(password);

        // Using default Lettuce client configuration without SSL
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofSeconds(5))
                .shutdownTimeout(Duration.ZERO)
                .build();

        return new LettuceConnectionFactory(config, clientConfig);
    }

    @Bean(name = "redisCachePort")
    @Primary
    public <T> CachePort<T> redisCachePort(ReactiveRedisTemplate<String, String> redisTemplate, 
                                         ObjectMapper objectMapper) {
        return new RedisCacheAdapter<>(redisTemplate, objectMapper);
    }

    @Bean(name = "reactiveRedisTemplate")
    @Primary
    public ReactiveRedisTemplate<String, String> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory factory,
            ObjectMapper objectMapper) {
        
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<String> valueSerializer = 
            new Jackson2JsonRedisSerializer<>(objectMapper, String.class);

        RedisSerializationContext.RedisSerializationContextBuilder<String, String> builder =
                RedisSerializationContext.newSerializationContext(keySerializer);

        RedisSerializationContext<String, String> context = 
                builder.value(valueSerializer).build();

        return new ReactiveRedisTemplate<>(factory, context);
    }
}
