package com.manteigueiro.kafka;

import com.manteigueiro.CalculatorRequestModel;
import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.MDC;

import java.util.Map;

public class MDCConsumerInterceptor implements ConsumerInterceptor<String, Object> {
    @Override
    public ConsumerRecords<String, Object> onConsume(ConsumerRecords<String, Object> records) {
        records.forEach(record -> {
            if (record.value() instanceof CalculatorRequestModel) {
                CalculatorRequestModel value = (CalculatorRequestModel) record.value();
                if (value.getMdcContext() != null) {
                    MDC.setContextMap(value.getMdcContext().getContextMap());
                }
            }
        });
        return records;
    }

    @Override
    public void onCommit(Map<TopicPartition, OffsetAndMetadata> offsets) {}

    @Override
    public void close() {}

    @Override
    public void configure(Map<String, ?> configs) {}
}
