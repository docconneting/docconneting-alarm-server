package com.example.docconnetingalarm.domain.alarm.service;

import com.example.docconnetingalarm.common.response.PageInfo;
import com.example.docconnetingalarm.common.response.PageResult;
import com.example.docconnetingalarm.domain.alarm.dto.AlarmResponse;
import com.example.docconnetingalarm.domain.alarm.entity.AlarmHistories;
import com.example.docconnetingalarm.domain.alarm.enums.AlarmType;
import com.example.docconnetingalarm.domain.alarm.repository.AlarmHistoriesBulkRepository;
import com.example.docconnetingalarm.domain.alarm.repository.AlarmHistoriesRepository;
import com.example.docconnetingalarm.domain.auth.entity.AuthUser;
import com.example.docconnetingalarm.infra.rabbitmq.dto.FcmInfo;
import com.example.docconnetingalarm.infra.rabbitmq.dto.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmSenderService alarmSenderService;
    private final AlarmHistoriesRepository alarmHistoriesRepository;
    private final AlarmHistoriesBulkRepository alarmHistoriesBulkRepository;

    /*
     * 알람 목록 조회
     */
    @Transactional(readOnly = true)
    public PageResult<AlarmResponse> findAlarms(AuthUser authUser, Pageable pageable) {
        Page<AlarmHistories> result = alarmHistoriesRepository.findAlarmHistories(authUser.getId(), pageable);
        List<AlarmResponse> alarms = AlarmResponse.toAlarmResponse(result.getContent());

        PageInfo pageInfo = PageInfo.builder()
                .pageNum(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalElement(result.getTotalElements())
                .totalPage(result.getTotalPages())
                .build();

        return new PageResult<>(alarms, pageInfo);
    }

    /*
     * 사용자가 유료 게시물을 올렸을 떄 해당 전공에 해당되는 의사들에게 알람 전송
     */
    @Transactional
    public void sendPostUploadCompletedMessage(Message message) {
        List<String> fcmTokenList = message.getFcmInfos().stream()
                                    .map(FcmInfo::getFcmToken)
                                    .toList();

        List<Long> userIdList = message.getFcmInfos().stream()
                                    .map(FcmInfo::getUserId)
                                    .toList();

        AlarmType alarmType = message.getAlarmType();
        String alarmMessage = message.getMessage();


        List<List<String>> fcmTokenBatches = new ArrayList<>();
        for (int i = 0; i < fcmTokenList.size(); i += 100) {
            fcmTokenBatches.add(fcmTokenList.subList(i, Math.min(fcmTokenList.size(), i + 100)));
        }
        for (List<String> fcmTokenBatche : fcmTokenBatches) {
            alarmSenderService.sendMulticastAlarm(fcmTokenBatche, alarmMessage);
        }
        alarmHistoriesBulkRepository.batchUpdate(userIdList, alarmType, alarmMessage);
    }

    /*
     * 게시물에 의사가 댓글을 달았을 때 게시물 작성자에게 알람 전송
     */
    @Transactional
    public void sendCommentCompletedMessage(Message message) {
        String fcmToken = message.getFcmInfos().get(0).getFcmToken();
        Long userId = message.getFcmInfos().get(0).getUserId();
        AlarmType alarmType = message.getAlarmType();
        String alarmMessage = message.getMessage();

        alarmSenderService.sendAlarm(fcmToken, alarmMessage);
        saveAlarmHistories(alarmMessage, userId, alarmType);
    }

    /*
     * 채팅 진료 결제가 완료 됐을 때 해당 의사에게 알람 전송
     */
    @Transactional
    public void sendMedicalRequestMessage(Message message) {
        String fcmToken = message.getFcmInfos().get(0).getFcmToken();
        Long userId = message.getFcmInfos().get(0).getUserId();
        AlarmType alarmType = message.getAlarmType();
        String alarmMessage = message.getMessage();

        alarmSenderService.sendAlarm(fcmToken, alarmMessage);
        saveAlarmHistories(alarmMessage, userId, alarmType);
    }

    /*
     * 단건 알람 히스토리 저장
     */
    private void saveAlarmHistories(String content, Long id, AlarmType alarmType) {
        AlarmHistories alarmHistories = AlarmHistories.of(content, id, alarmType);
        alarmHistoriesRepository.save(alarmHistories);
    }
}
