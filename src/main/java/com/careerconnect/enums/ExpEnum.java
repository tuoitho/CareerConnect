package com.careerconnect.enums;

import lombok.Getter;

@Getter
public enum ExpEnum {
    ENTRY_LEVEL("Entry Level"),
    MID_LEVEL("Mid Level"),
    SENIOR_LEVEL("Senior Level"),
    EXECUTIVE("Executive"),
    NO_EXPERIENCE("No Experience"),
    INTERN("Intern"),
    FRESHER("Fresher");

    private final String value;

    ExpEnum(String value) {
        this.value = value;
    }

}
