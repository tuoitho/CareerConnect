package com.careerconnect.enums;

import lombok.Getter;

@Getter
public enum JobTypeEnum {
    FULL_TIME("Full Time"),
    PART_TIME("Part Time"),
    CONTRACT("Contract"),
    INTERNSHIP("Internship"),
    TEMPORARY("Temporary"),
    VOLUNTEER("Volunteer"),
    FREELANCE("Freelance");

    private final String value;

    JobTypeEnum(String value) {
        this.value = value;
    }

}
