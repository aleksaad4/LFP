package ad4si2.lfp.web.controllers.admin.football;

import ad4si2.lfp.data.entities.football.Country;
import ad4si2.lfp.data.services.football.CountryService;
import ad4si2.lfp.utils.web.AjaxResponse;
import ad4si2.lfp.web.controllers.BaseRestController;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;

@RestController
@RequestMapping("/rest/admin/country")
public class CountryRestController extends BaseRestController<Long, Country, CountryService> {

    @Inject
    private CountryService countryService;

    @RequestMapping(method = RequestMethod.GET)
    public AjaxResponse list() {
        return super.list();
    }

    @RequestMapping(method = RequestMethod.POST)
    public AjaxResponse create(@RequestBody @Nonnull final Country entity) {
        return super.create(entity);
    }

    @RequestMapping(method = RequestMethod.PATCH)
    public AjaxResponse update(@RequestBody @Nonnull final Country entity) {
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
    protected CountryService getService() {
        return countryService;
    }
}
