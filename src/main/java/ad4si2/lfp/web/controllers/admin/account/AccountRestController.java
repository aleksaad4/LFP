package ad4si2.lfp.web.controllers.admin.account;

import ad4si2.lfp.data.entities.account.Account;
import ad4si2.lfp.data.services.account.AccountService;
import ad4si2.lfp.utils.web.AjaxResponse;
import ad4si2.lfp.web.controllers.BaseRestController;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nonnull;

@RestController
@RequestMapping("/rest/admin/account")
public class AccountRestController extends BaseRestController<Long, Account, AccountService> {

    @RequestMapping(method = RequestMethod.GET)
    public AjaxResponse list() {
        return super.list();
    }

    @RequestMapping(method = RequestMethod.POST)
    public AjaxResponse create(@RequestBody @Nonnull final Account entity) {
        return super.create(entity);
    }

    @RequestMapping(method = RequestMethod.PATCH)
    public AjaxResponse update(@RequestBody @Nonnull final Account entity) {
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
    protected AccountService getService() {
        return accountService;
    }
}
