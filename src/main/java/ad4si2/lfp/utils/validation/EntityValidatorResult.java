package ad4si2.lfp.utils.validation;

import ad4si2.lfp.utils.data.IDeleted;
import ad4si2.lfp.utils.data.IEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
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

    @Nonnull
    public static EntityValidatorResult validatorResult(@Nonnull final String errorMessage, @Nonnull final String errorCode) {
        final EntityValidatorResult result = new EntityValidatorResult();
        result.addError(new EntityValidatorError(errorMessage, errorCode));
        return result;
    }

    @Nonnull
    public static EntityValidatorResult validatorResult(@Nonnull final String fieldName, @Nonnull final String errorMessage,
                                                        @Nonnull final String errorCode) {
        final EntityValidatorResult result = new EntityValidatorResult();
        result.addError(new EntityValidatorError(fieldName, errorMessage, errorCode));
        return result;
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

    public void addError(@Nonnull final EntityValidatorError error) {
        errors.add(error);
    }

    @Nonnull
    public EntityValidatorResult addErrors(@Nonnull final Collection<EntityValidatorError> newErrors) {
        errors.addAll(newErrors);
        return this;
    }

    @Nonnull
    public <T> EntityValidatorResult checkMaxSize(@Nonnull final String fieldName, final int size,
                                                  @Nonnull final T object, @Nonnull final Function<T, String> getter) {
        final String fieldValue = getter.apply(object);
        if (!(fieldValue == null || fieldValue.length() < size)) {
            addError(new EntityValidatorError(fieldName, object.getClass().getSimpleName().toLowerCase() + "_" + fieldName + "_max_size",
                    "Object [" + object.getClass().getSimpleName() + "] field [" + fieldName + "] max size limit"));
        }
        return this;
    }

    @Nonnull
    public <T> EntityValidatorResult checkPositiveValue(@Nonnull final String fieldName,
                                                        @Nonnull final T object,
                                                        @Nonnull final Function<T, Integer> getter) {
        final Integer fieldValue = getter.apply(object);
        if (fieldValue != null && fieldValue < 0) {
            addError(new EntityValidatorError(fieldName, object.getClass().getSimpleName().toLowerCase() + "_" + fieldName + "_incorrect",
                    "Object [" + object.getClass().getSimpleName() + "] field [" + fieldName + "] has incorrect value [" + fieldValue + "]"));
        }
        return this;
    }

    @Nonnull
    public EntityValidatorResult checkDeleted(@Nonnull final IDeleted iDeleted) {
        if (iDeleted.isDeleted()) {
            addError(new EntityValidatorError("common.entity_deleted",
                    "Object [" + iDeleted.getClass().getSimpleName() + "] already deleted"));
        }
        return this;
    }

    @Nonnull
    public <T> EntityValidatorResult checkIsEmpty(@Nonnull final String fieldName, @Nonnull final T object,
                                                  @Nonnull final Function<T, String> getter) {
        final String fieldValue = getter.apply(object);
        if (fieldValue == null || fieldValue.trim().isEmpty()) {
            addError(new EntityValidatorError(fieldName, object.getClass().getSimpleName().toLowerCase() + "_" + fieldName + "_is_empty",
                    "Object [" + object.getClass().getSimpleName() + "] field [" + fieldName + "] is empty"));
        }
        return this;
    }

    @Nonnull
    public <T, FIELD_TYPE> EntityValidatorResult checkIsNull(@Nonnull final String fieldName, @Nonnull final T object,
                                                             @Nonnull final Function<T, FIELD_TYPE> getter) {
        final FIELD_TYPE fieldValue = getter.apply(object);
        if (fieldValue == null) {
            addError(new EntityValidatorError(fieldName, object.getClass().getSimpleName().toLowerCase() + "_" + fieldName + "_is_empty",
                    "Object [" + object.getClass().getSimpleName() + "] field [" + fieldName + "] is empty"));
        }
        return this;
    }

    @Nonnull
    public <T extends IEntity, FIELD_TYPE> EntityValidatorResult checkNotModify(@Nonnull final String fieldName,
                                                                                @Nonnull final T object,
                                                                                @Nullable final T oldObject,
                                                                                @Nonnull final Function<T, FIELD_TYPE> getter) {
        // по какой-то причине старый объект не найден - это странно, но в таком случае будут ошибки ранее (например, редактирование отсутствующего объекта)
        if (oldObject == null) {
            return this;
        }
        final FIELD_TYPE newFieldValue = getter.apply(object);
        final FIELD_TYPE oldFieldValue = getter.apply(oldObject);
        if (newFieldValue != oldFieldValue) {
            addError(new EntityValidatorError(fieldName, object.getClass().getSimpleName().toLowerCase() + "_" + fieldName + "_can_t_modify",
                    "Object [" + object.getClass().getSimpleName() + "] can't modify field [" + fieldName + "]"));
        }
        return this;
    }

    @Nonnull
    public <T extends IEntity, FIELD_TYPE> EntityValidatorResult checkDuplicate(@Nonnull final String fieldName, @Nonnull final T object,
                                                                                @Nonnull final Function<T, FIELD_TYPE> getter,
                                                                                @Nonnull final Function<FIELD_TYPE, List<T>> finder,
                                                                                final boolean forUpdate) {
        final FIELD_TYPE fieldValue = getter.apply(object);
        if (fieldValue != null) {
            final List<T> list = finder.apply(fieldValue);
            if (!list.isEmpty()) {
                final EntityValidatorError error = new EntityValidatorError(fieldName, object.getClass().getSimpleName().toLowerCase() + "_" + fieldName + "_exists",
                        "Object [" + object.getClass().getSimpleName() + "] with field [" + fieldName + "] value [" + fieldValue + "] already exists");
                if (!forUpdate) {
                    // создание нового объекта, а уже нашелся такой
                    addError(error);
                } else {
                    // обновление объекта, проверка что тот, что нашелся - не наш
                    if (list.stream().noneMatch(e -> e.getId() == object.getId())) {
                        // среди этих объектов есть какой-то, но не наш
                        addError(error);
                    }
                }
            }
        }
        return this;
    }

    @Nonnull
    public <T extends IEntity, FIELD_TYPE_1, FIELD_TYPE_2> EntityValidatorResult checkDuplicate(@Nonnull final String fieldName1,
                                                                                                @Nonnull final String fieldName2,
                                                                                                @Nonnull final T object,
                                                                                                @Nonnull final Function<T, FIELD_TYPE_1> getter1,
                                                                                                @Nonnull final Function<T, FIELD_TYPE_2> getter2,
                                                                                                @Nonnull final BiFunction<FIELD_TYPE_1, FIELD_TYPE_2, List<T>> finder,
                                                                                                final boolean forUpdate) {
        final FIELD_TYPE_1 fieldValue1 = getter1.apply(object);
        final FIELD_TYPE_2 fieldValue2 = getter2.apply(object);
        if (fieldValue1 != null && fieldValue2 != null) {
            final List<T> list = finder.apply(fieldValue1, fieldValue2);
            if (!list.isEmpty()) {
                final EntityValidatorError error = new EntityValidatorError(
                        "Object [" + object.getClass().getSimpleName() + "] with [" + fieldName1 + " : " + fieldValue1 + "] and [" + fieldName2 + " : " + fieldValue2 + "] already exists",
                        object.getClass().getSimpleName().toLowerCase() + "_" + fieldName1 + "_" + fieldName2 + "_exists");
                if (!forUpdate) {
                    // создание нового объекта, а уже нашелся такой
                    addError(error);
                } else {
                    // обновление объекта, проверка что тот, что нашелся - не наш
                    if (list.stream().noneMatch(e -> e.getId() == object.getId())) {
                        // среди этих объектов есть какой-то, но не наш
                        addError(error);
                    }
                }
            }
        }
        return this;
    }

    @Nonnull
    public <T, F extends IEntity<ID, F>, ID> EntityValidatorResult checkLinkedValue(@Nonnull final String fieldName,
                                                                                    @Nonnull final T object,
                                                                                    @Nullable final ID linkedId,
                                                                                    @Nonnull final Function<ID, F> getter,
                                                                                    final boolean mustNonnull) {
        final EntityValidatorError error = new EntityValidatorError(fieldName, object.getClass().getSimpleName().toLowerCase() + "_" + fieldName + "_incorrect",
                "Can'f find [" + fieldName + "] with id [" + linkedId + "] for object [" + object.getClass().getSimpleName() + "]");

        // вообще не задан, а должен быть
        if (linkedId == null && mustNonnull) {
            addError(error);
        } else if (linkedId != null) {
            // задан, но не обязательный
            // проверим, что объект находится
            final F entity = getter.apply(linkedId);
            if (entity == null) {
                addError(error);
            }
        }
        return this;
    }
}
