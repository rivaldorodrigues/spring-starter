package com.github.rivaldorodrigues.springstarter.application.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
public final class DateUtility {

    private DateUtility() {
    }

    public static Date toDate(@Nullable LocalDateTime localDateTime) {

        Date result = null;

        if (localDateTime != null) {
            result = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        }

        return result;
    }
}
