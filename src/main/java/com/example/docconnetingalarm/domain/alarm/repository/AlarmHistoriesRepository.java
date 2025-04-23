package com.example.docconnetingalarm.domain.alarm.repository;

import com.example.docconnetingalarm.domain.alarm.entity.AlarmHistories;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmHistoriesRepository extends JpaRepository<AlarmHistories, Long>, AlarmHistoriesRepositoryQuery {
}
