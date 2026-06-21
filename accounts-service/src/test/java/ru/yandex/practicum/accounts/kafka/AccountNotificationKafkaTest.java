package ru.yandex.practicum.accounts.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import ru.yandex.practicum.accounts.client.NotificationsClient;
import ru.yandex.practicum.accounts.config.TestSecurityConfig;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"notifications.account-updated"})
@DirtiesContext
@Import(TestSecurityConfig.class)
class AccountNotificationKafkaTest {

    @Autowired
    private NotificationsClient notificationsClient;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @Test
    void notifyAccountUpdated_sendsKafkaMessage() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("test-group", "true", embeddedKafka);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        Consumer<String, String> consumer = new DefaultKafkaConsumerFactory<String, String>(consumerProps)
                .createConsumer();
        embeddedKafka.consumeFromAnEmbeddedTopic(consumer, "notifications.account-updated");

        notificationsClient.notifyAccountUpdated("ivanov", "Новое Имя");

        ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, "notifications.account-updated", Duration.ofSeconds(10));
        assertThat(record.key()).isEqualTo("ivanov");
        assertThat(record.value()).contains("ivanov");
        assertThat(record.value()).contains("Новое Имя");

        consumer.close();
    }
}
