package com.example.docconnetingalarm.infra.rabbitmq.dto;

import com.example.docconnetingalarm.domain.alarm.enums.AlarmType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class Message {
    private String fcmToken;
    private Long userId;
    private List<String> fcmTokenList;
    private List<Long> userIdList;
    private String message;
    private AlarmType alarmType;

    private Message(List<String> fcmTokenList, List<Long> userIdList, AlarmType alarmType) {
        this.fcmTokenList = fcmTokenList;
        this.userIdList = userIdList;
        this.alarmType = alarmType;
    }

    private Message(String fcmToken, String message, Long userId, AlarmType alarmType) {
        this.fcmToken = fcmToken;
        this.message = message;
        this.userId = userId;
        this.alarmType = alarmType;
    }

    public static Message of(List<String> fcmTokenList, List<Long> userIdList, AlarmType alarmType) {
        return new Message(fcmTokenList, userIdList, alarmType);
    }

    public static Message of(String fcmToken, String message, Long userId, AlarmType alarmType) {
        return new Message(fcmToken, message, userId, alarmType);
    }

}
