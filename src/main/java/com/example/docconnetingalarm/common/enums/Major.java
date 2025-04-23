package com.example.docconnetingalarm.common.enums;

import com.example.docconnetingalarm.common.exception.constant.ErrorCode;
import com.example.docconnetingalarm.common.exception.object.ClientException;

public enum Major {
    INTERNAL_MEDICINE,
    ORTHOPEDICS,
    SURGERY,
    PEDIATRICS,
    DERMATOLOGY;

    public static Major of(String category) {
        for (Major major : values()) {
            if (major.name().equals(category)) {
                return major;
            }
        }
        throw new ClientException(ErrorCode.MAJOR_NOT_FOUND);
    }
}
