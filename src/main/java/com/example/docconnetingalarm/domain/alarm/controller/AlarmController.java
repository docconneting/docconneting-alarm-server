package com.example.docconnetingalarm.domain.alarm.controller;

import com.example.docconnetingalarm.common.response.PageResult;
import com.example.docconnetingalarm.common.response.Response;
import com.example.docconnetingalarm.domain.alarm.dto.AlarmResponse;
import com.example.docconnetingalarm.domain.alarm.service.AlarmService;
import com.example.docconnetingalarm.domain.auth.annotation.Auth;
import com.example.docconnetingalarm.domain.auth.entity.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    @GetMapping()
    public ResponseEntity<Response<List<AlarmResponse>>> findAlarmHistories(@Auth AuthUser authUser, @PageableDefault Pageable pageable) {
        PageResult<AlarmResponse> pageResult = alarmService.findAlarms(authUser, pageable);
        return ResponseEntity.ok().body(Response.of(pageResult.getContent(), pageResult.getPageInfo()));
    }

}
