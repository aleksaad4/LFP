package ad4si2.lfp.web.controllers.admin.football;

import ad4si2.lfp.data.entities.football.League;
import ad4si2.lfp.data.services.football.CountryService;
import ad4si2.lfp.data.services.football.LeagueService;
import ad4si2.lfp.utils.web.AjaxResponse;
import ad4si2.lfp.web.controllers.BaseRestController;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;

@RestController
@RequestMapping("/rest/admin/league")
public class LeagueRestController extends BaseRestController<Long, League, LeagueService> {

    @Inject
    private LeagueService leagueService;

    @Inject
    private CountryService countryService;

    @RequestMapping(value = "/countries", method = RequestMethod.GET)
    public AjaxResponse countries() {
        return webUtils.successResponse(countryService.findAll(false));
    }

    @RequestMapping(method = RequestMethod.GET)
    public AjaxResponse list() {
        return super.list();
    }

    @RequestMapping(method = RequestMethod.POST)
    public AjaxResponse create(@RequestBody @Nonnull final League entity) {
        return super.create(entity);
    }

    @RequestMapping(method = RequestMethod.PATCH)
    public AjaxResponse update(@RequestBody @Nonnull final League entity) {
        return super.update(entity);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public AjaxResponse get(@PathVariable("id") @Nonnull final Long id) {
        return super.get(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public AjaxResponse delete(@PathVariable("id") @Nonnull final Long id) {
        return super.delete(id);
    }

    @Override
    protected LeagueService getService() {
        return leagueService;
    }

    @Override
    protected void fillEntity(@Nonnull final League entity) {
        super.fillEntity(entity);
        // страна
        fillLinkedValues(entity, League::getCountryId, countryService, League::setCountry);
    }

    @Override
    protected void fillEntityList(@Nonnull final List<League> entities) {
        super.fillEntityList(entities);
        // страна
        fillLinkedValues(entities, League::getCountryId, countryService, League::setCountry);
    }
}
