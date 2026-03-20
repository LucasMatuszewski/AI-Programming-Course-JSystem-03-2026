package com.silkycoders1.jsystemssilkycodders1.config;

import com.agui.core.event.BaseEvent;
import com.agui.json.ObjectMapperFactory;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.silkycoders1.jsystemssilkycodders1.agui.sdk.serialization.AgUiEventSerializationMixin;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OpenRouterProperties.class)
public class ApplicationConfig {

    @Bean
    public ObjectMapper objectMapper() {
        var factory = JsonFactory.builder()
                .enable(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION)
                .build();
        var result = new ObjectMapper(factory);
        ObjectMapperFactory.addMixins(result);
        result.addMixIn(BaseEvent.class, AgUiEventSerializationMixin.class);
        return result;
    }
}
