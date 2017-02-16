package ad4si2.lfp;

import ad4si2.lfp.utils.exceptions.ValidationException;
import ad4si2.lfp.utils.validation.EntityValidator;
import org.junit.Assert;

import javax.annotation.Nonnull;

public class TestUtils {

    public static <ENTITY, SERVICE extends EntityValidator<ENTITY>> void validateDuplicateTest(@Nonnull final SERVICE service,
                                                                                               @Nonnull final ENTITY exist,
                                                                                               @Nonnull final ENTITY duplicated) {
        Assert.assertTrue(service.validateEntry(duplicated, false).hasErrors());
        Assert.assertTrue(service.validateEntry(duplicated, true).hasErrors());
        Assert.assertFalse(service.validateEntry(exist, true).hasErrors());
    }

    public static <ENTITY, SERVICE extends EntityValidator<ENTITY>> void validateCreateFailTest(@Nonnull final SERVICE service,
                                                                                                @Nonnull final ENTITY e) {
        Assert.assertTrue(service.validateEntry(e, false).hasErrors());
    }

    public static <ENTITY, SERVICE extends EntityValidator<ENTITY>> void validateUpdateFailTest(@Nonnull final SERVICE service,
                                                                                                @Nonnull final ENTITY e) {
        Assert.assertTrue(service.validateEntry(e, true).hasErrors());
    }

    public static void catchExceptionTest(@Nonnull final Runnable r) {
        try {
            r.run();
            Assert.assertTrue(false);
        } catch (ValidationException e) {
            Assert.assertTrue(true);
        }
    }
}
