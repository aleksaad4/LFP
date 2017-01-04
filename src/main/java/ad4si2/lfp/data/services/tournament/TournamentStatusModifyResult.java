package ad4si2.lfp.data.services.tournament;

import ad4si2.lfp.data.entities.tournament.Tournament;
import ad4si2.lfp.utils.validation.EntityValidatorResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TournamentStatusModifyResult {

    @Nonnull
    EntityValidatorResult result = new EntityValidatorResult();

    @Nullable
    Tournament t;

    protected TournamentStatusModifyResult(@Nonnull final EntityValidatorResult result, @Nonnull final Tournament t) {
        this.result = result;
        this.t = t;
    }

    protected TournamentStatusModifyResult(@Nonnull final EntityValidatorResult result) {
        this.result = result;
    }

    public TournamentStatusModifyResult(@Nonnull final Tournament t) {
        this.t = t;
    }

    @Nonnull
    public static TournamentStatusModifyResult success(@Nonnull final Tournament t) {
        return new TournamentStatusModifyResult(t);
    }

    @Nonnull
    public static TournamentStatusModifyResult error(@Nonnull final EntityValidatorResult result) {
        return new TournamentStatusModifyResult(result);
    }

    public boolean isOk() {
        return !result.hasErrors() && t != null;
    }

    @Nullable
    public Tournament getT() {
        return t;
    }

    @Nonnull
    public EntityValidatorResult getResult() {
        return result;
    }
}
