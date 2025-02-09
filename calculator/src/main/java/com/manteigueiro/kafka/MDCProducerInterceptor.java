package com.manteigueiro.kafka;

import com.manteigueiro.CalculatorRequestModel;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.MDC;
import java.util.Map;

public class MDCProducerInterceptor implements ProducerInterceptor<String, Object> {
    @Override
    public ProducerRecord<String, Object> onSend(ProducerRecord<String, Object> record) {
        if (record.value() instanceof CalculatorRequestModel) {
            CalculatorRequestModel value = (CalculatorRequestModel) record.value();
            value.setMdcContext(new MDCContextModel(MDC.getCopyOfContextMap()));
        }
        return record;
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {}

    @Override
    public void close() {}

    @Override
    public void configure(Map<String, ?> configs) {}
}

