package com.springboot.wooden.service;

import com.springboot.wooden.dto.WeeklyHistoryDto;

public interface WeeklyHistoryService {

    WeeklyHistoryDto getWeeklyHistory(Long itemNo, int weeksBack);
}
