package ad4si2.lfp.utils.validation;

import javax.annotation.Nonnull;

public interface EntityValidator<T> {

    /**
     * общий метод валидации корректности входных данных для создания или редактирования
     *
     * @param entry     - сущность, которую собрались добавлять/редактировать
     * @param forUpdate - сущность не новая, это будет update
     * @return результаты валидации
     * исключений валидатор кидать не должен, только если Runtime
     */
    @Nonnull
    EntityValidatorResult validateEntry(T entry, boolean forUpdate);

    /** интерфейс может быть расширен функциями валидации отдельных полей, если для них возможно провести  */

}
