package ad4si2.lfp.utils.validation;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EntityValidatorError {

    // описание ошибки валидации
    @Nonnull
    private String rawMessage;

    // код ресурса из MessageSource
    @Nonnull
    private String userMessageKey;

    // ключ поля, к которому относится ошибка (если нет - значит, относится к объекту в целом)
    @Nullable
    private String fieldKey;

    // конструктор для общей ошибки
    public EntityValidatorError(@Nonnull final String rawMessage, @Nonnull final String userMessageKey) {
        this.rawMessage = rawMessage;
        this.userMessageKey = userMessageKey;
    }

    // конструктор для ошибки, привязанной к полю
    public EntityValidatorError(@Nullable final String fieldKey, @Nonnull final String rawMessage, @Nonnull final String userMessageKey) {
        this.fieldKey = fieldKey;
        this.rawMessage = rawMessage;
        this.userMessageKey = userMessageKey;
    }

    @Nonnull
    public String getRawMessage() {
        return rawMessage;
    }

    @Nonnull
    public String getUserMessageKey() {
        return userMessageKey;
    }

    @Nullable
    public String getFieldKey() {
        return fieldKey;
    }

    public boolean isCommonError() {
        return fieldKey == null;
    }

    @Nonnull
    public String getTextForUser(@Nonnull final MessageSource messageSource) {
        try {
            if (userMessageKey != null) {
                return messageSource.getMessage(userMessageKey, null, null);
            }
        } catch (final NoSuchMessageException e) {
        }
        return rawMessage;
    }

    @Override
    public String toString() {
        return "" + fieldKey + " -> " + rawMessage + "(" + userMessageKey + ")";
    }
}
