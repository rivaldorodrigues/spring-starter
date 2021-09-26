package com.github.rivaldorodrigues.springstarter.infrastructure;

import com.github.rivaldorodrigues.springstarter.application.util.DateUtility;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Date;

@Service
public class SystemClockDefault implements SystemClock {

    private Clock clock = Clock.systemDefaultZone();

    @Override
    public Date currentDate() {
        return DateUtility.toDate(currentLocalDateTime());
    }

    @Override
    public LocalDateTime currentLocalDateTime() {
        return LocalDateTime.now(clock);
    }
}
