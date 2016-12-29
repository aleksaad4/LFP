package ad4si2.lfp.utils.validation;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public class EntityValidatorResult {

    // список всех ошибок валидации
    @Nonnull
    private List<EntityValidatorError> errors = new ArrayList<>();

    public EntityValidatorResult() {
    }

    public EntityValidatorResult(@Nonnull final List<EntityValidatorError> errors) {
        this.errors = errors;
    }

    public void addError(@Nonnull final EntityValidatorError error) {
        errors.add(error);
    }

    @Nonnull
    public EntityValidatorResult addErrors(@Nonnull final Collection<EntityValidatorError> newErrors) {
        errors.addAll(newErrors);
        return this;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    @Nonnull
    public List<EntityValidatorError> getErrors() {
        return new ArrayList<>(errors);
    }

    @Nonnull
    public List<EntityValidatorError> getCommonErrors() {
        return errors.stream().filter(EntityValidatorError::isCommonError).collect(Collectors.toList());
    }

    @Nonnull
    public List<EntityValidatorError> getErrorsForField(@Nonnull final String fieldName) {
        return errors.stream().filter(error -> fieldName.equals(error.getFieldKey())).collect(Collectors.toList());
    }

    @Nonnull
    public Set<String> getErrorFields() {
        final Set<String> fields = new HashSet<>();
        errors.stream().filter(error -> error.getFieldKey() != null).forEach(error -> fields.add(error.getFieldKey()));
        return fields;
    }

    @Override
    public String toString() {
        return hasErrors() ? "ERROR: " + errors : "OK";
    }
}
