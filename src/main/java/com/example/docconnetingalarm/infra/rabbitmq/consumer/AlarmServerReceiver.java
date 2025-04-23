package com.example.docconnetingalarm.infra.rabbitmq.consumer;

import com.example.docconnetingalarm.infra.rabbitmq.dto.Message;
import com.example.docconnetingalarm.infra.rabbitmq.handler.AlarmHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AlarmServerReceiver {

    private final List<AlarmHandler> handlers;
    private static final String QUEUE = "alarm-queue";

    @RabbitListener(queues = QUEUE)
    public void receive(Message message) {
        for (AlarmHandler handler : handlers) {
            if (handler.getAlarmType().equals(message.getAlarmType())) {
                handler.handle(message);
                break;
            }
        }
    }
}
