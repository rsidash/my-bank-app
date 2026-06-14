package ru.yandex.practicum.mybankfront.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "transfer.service")
public class TransferServiceProperties {
    private String url;
}
