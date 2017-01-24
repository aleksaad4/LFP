package ad4si2.lfp.web.controllers.admin.tour;

import ad4si2.lfp.data.entities.account.Account;
import ad4si2.lfp.data.entities.football.Match;
import ad4si2.lfp.data.entities.forecast.Meeting;
import ad4si2.lfp.data.entities.tour.TourStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TourDTO {

    private long id;

    @Nonnull
    private String name;

    @Nullable
    private Account account;

    private long tournamentId;

    @Nullable
    private TourStatus status;

    @Nullable
    private List<Meeting> meetingList;

    @Nonnull
    private List<Match> matchList = new ArrayList<>();

    @Nullable
    private Date openDate;

    @Nullable
    private Date startDate;

    @Nullable
    private Date finishDate;

    public TourDTO(final long id, @Nonnull final String name, @Nullable final Account account,
                   final long tournamentId, @Nonnull final TourStatus status,
                   @Nullable final Date openDate, @Nullable final Date startDate, @Nullable final Date finishDate) {
        this.id = id;
        this.name = name;
        this.account = account;
        this.tournamentId = tournamentId;
        this.status = status;
        this.openDate = openDate;
        this.startDate = startDate;
        this.finishDate = finishDate;
    }

    public void setMeetingList(@Nullable final List<Meeting> meetingList) {
        this.meetingList = meetingList;
    }

    public void setMatchList(@Nullable final List<Match> matchList) {
        this.matchList = matchList != null ? matchList : new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nullable
    public Account getAccount() {
        return account;
    }

    public long getTournamentId() {
        return tournamentId;
    }

    @Nullable
    public TourStatus getStatus() {
        return status;
    }

    @Nullable
    public List<Meeting> getMeetingList() {
        return meetingList;
    }

    @Nonnull
    public List<Match> getMatchList() {
        return matchList;
    }

    @Nullable
    public Date getOpenDate() {
        return openDate;
    }

    @Nullable
    public Date getStartDate() {
        return startDate;
    }

    @Nullable
    public Date getFinishDate() {
        return finishDate;
    }
}
