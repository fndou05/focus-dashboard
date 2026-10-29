package com.frankondou.focus.service;

import com.frankondou.focus.dto.FreeTimeResponse;
import java.util.UUID;

public interface FreeTimeService {
    FreeTimeResponse getFreeTime(UUID userId);
}
