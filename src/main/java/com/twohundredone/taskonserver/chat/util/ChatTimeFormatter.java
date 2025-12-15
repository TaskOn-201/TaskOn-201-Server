package com.twohundredone.taskonserver.chat.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatTimeFormatter {

    private static final DateTimeFormatter SENT_TIME_FMT = DateTimeFormatter.ofPattern("yyMMdd'T'HHmmss");

    public static String toSentTime(LocalDateTime time) {
        return time.format(SENT_TIME_FMT);
    }

    // 24시간 이내: "N분 전", 이후: sentTime 포맷
    public static String toDisplayTime(LocalDateTime time) {
        LocalDateTime now = LocalDateTime.now();
        Duration d = Duration.between(time, now);

        if (d.toHours() >= 24) return toSentTime(time);

        long minutes = d.toMinutes();
        if (minutes < 1) return "방금 전";
        if (minutes < 60) return minutes + "분 전";

        long hours = d.toHours();
        return hours + "시간 전";
    }
}
