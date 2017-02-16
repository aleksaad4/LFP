package ad4si2.lfp.scheduled;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class MatchScheduledService {

    /**
     * Задача, которая переводит матчи в состояние 'Идёт' по наступлению дня матча
     */
    @Scheduled(cron = "0 0/30 * * * *")
    void startMatches() {
        // находим все матчи, у которых дата начала 'СЕГОДНЯ' и статус 'NOT_STARTED' и переводим их в статус 'PROGRESS'
    }

    /**
     * Задача, которая переводит матчи в состояние 'Завершен' по наступлению дня, следующего за днём матча
     */
    @Scheduled(cron = "0 0/30 * * * *")
    void finishMatches() {
        // находим все матчи, у которых дата начала 'ВЧЕРА' и статус не 'PROGRESS' и переводим их в статус 'FINISH'
    }

}
