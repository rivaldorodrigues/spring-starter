package com.github.rivaldorodrigues.springstarter.infrastructure;

import java.time.LocalDateTime;
import java.util.Date;

public interface SystemClock {
    Date currentDate();
    LocalDateTime currentLocalDateTime();
}
