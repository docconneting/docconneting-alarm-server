package com.example.docconnetingalarm.infra.rabbitmq.handler;

import com.example.docconnetingalarm.domain.alarm.enums.AlarmType;
import com.example.docconnetingalarm.infra.rabbitmq.dto.Message;

public interface AlarmHandler {
    AlarmType getAlarmType();
    void handle(Message message);

}
