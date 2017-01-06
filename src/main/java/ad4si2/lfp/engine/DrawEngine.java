package ad4si2.lfp.engine;

import ad4si2.lfp.data.entities.account.Player;
import ad4si2.lfp.data.entities.forecast.Meeting;
import ad4si2.lfp.data.entities.tour.Tour;
import ad4si2.lfp.data.entities.tournament.Tournament;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.List;

@Service
public class DrawEngine {

    /**
     * Метод для генерации списка встреч между игроками в турнире
     *
     * @param t       турнир
     * @param players игроки
     * @param tours   туры
     * @return список встреч во всех турах
     */
    public List<Meeting> drawPlayers(@Nonnull final Tournament t, @Nonnull final List<Player> players, @Nonnull final List<Tour> tours) {
        return null;
    }

}
