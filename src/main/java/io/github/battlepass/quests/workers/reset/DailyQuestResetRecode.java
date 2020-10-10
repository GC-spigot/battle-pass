package io.github.battlepass.quests.workers.reset;

import org.apache.commons.lang.StringUtils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DailyQuestResetRecode {
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final ZoneId timeZone;
    private ZonedDateTime whenReset;

    public DailyQuestResetRecode() {
        this.timeZone = ZoneId.of("Europe/Oslo");
        this.whenReset = this.parseTime(this.now().withSecond(0), "20:00");
    }

    public void start() {
        System.out.println(this.now() + ":" + (this.now().until(this.whenReset.plusDays(this.now().until(this.whenReset, ChronoUnit.SECONDS) < 0 ? 1 : 0), ChronoUnit.SECONDS)));
        this.executorService.scheduleAtFixedRate(() -> {
            System.out.println("Time reached");
        }, this.now().until(this.whenReset.plusDays(this.now().until(this.whenReset, ChronoUnit.SECONDS) < 0 ? 1 : 0), ChronoUnit.SECONDS), TimeUnit.DAYS.toMinutes(1), TimeUnit.SECONDS);
    }

    private ZonedDateTime now() {
        return ZonedDateTime.now().withZoneSameInstant(this.timeZone);
    }

    private ZonedDateTime parseTime(ZonedDateTime date, String time) {
        String[] timeSplit = time.split(":");
        return date.withHour(StringUtils.isNumeric(timeSplit[0]) ? Integer.parseInt(timeSplit[0]) : 0).withMinute(timeSplit.length > 1 ? StringUtils.isNumeric(timeSplit[1]) ? Integer.parseInt(timeSplit[1]) : 0 : 0);
    }
}
