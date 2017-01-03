package ad4si2.lfp.web.controllers.admin.football;

import ad4si2.lfp.data.entities.football.Team;
import ad4si2.lfp.data.services.football.CountryService;
import ad4si2.lfp.data.services.football.TeamService;
import ad4si2.lfp.utils.web.AjaxResponse;
import ad4si2.lfp.web.controllers.BaseRestController;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;

@RestController
@RequestMapping("/rest/admin/team")
public class TeamRestController extends BaseRestController<Long, Team, TeamService> {

    @Inject
    private TeamService teamService;

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
    public AjaxResponse create(@RequestBody @Nonnull final Team entity) {
        return super.create(entity);
    }

    @RequestMapping(method = RequestMethod.PATCH)
    public AjaxResponse update(@RequestBody @Nonnull final Team entity) {
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
    protected TeamService getService() {
        return teamService;
    }

    @Override
    protected void fillEntity(@Nonnull final Team entity) {
        super.fillEntity(entity);
        // страна
        fillLinkedValues(entity, Team::getCountryId, countryService, Team::setCountry);

    }

    @Override
    protected void fillEntityList(@Nonnull final List<Team> entities) {
        super.fillEntityList(entities);
        // страна
        fillLinkedValues(entities, Team::getCountryId, countryService, Team::setCountry);
    }
}
