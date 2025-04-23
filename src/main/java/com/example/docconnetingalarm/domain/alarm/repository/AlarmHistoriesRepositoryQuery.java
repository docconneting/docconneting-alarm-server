package com.example.docconnetingalarm.domain.alarm.repository;

import com.example.docconnetingalarm.domain.alarm.entity.AlarmHistories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AlarmHistoriesRepositoryQuery {

    Page<AlarmHistories> findAlarmHistories(Long userId, Pageable pageable);
}
