package com.example.docconnetingalarm.infra.rabbitmq.handler;

import com.example.docconnetingalarm.domain.alarm.enums.AlarmType;
import com.example.docconnetingalarm.domain.alarm.service.AlarmService;
import com.example.docconnetingalarm.infra.rabbitmq.dto.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MedicalRequestAlarmHandler implements AlarmHandler{
    private final AlarmService alarmService;

    @Override
    public AlarmType getAlarmType() {
        return AlarmType.MEDICAL_REQUEST;
    }

    @Override
    public void handle(Message message) {
        alarmService.sendMedicalRequestMessage(message);
    }
}
