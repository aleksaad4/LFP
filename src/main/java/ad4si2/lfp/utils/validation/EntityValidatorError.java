package ad4si2.lfp.utils.validation;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import javax.annotation.Nonnull;

public class EntityValidatorError {

    // описание ошибки валидации
    private String rawMessage;

    // код ресурса из MessageSource
    private String userMessageKey;

    // ключ поля, к которому относится ошибка (если нет - значит, относится к объекту в целом)
    private String fieldKey;

    // конструктор для общей ошибки
    public EntityValidatorError(final String rawMessage, final String userMessageKey) {
        this.rawMessage = rawMessage;
        this.userMessageKey = userMessageKey;
    }

    // конструктор для ошибки, привязанной к полю
    public EntityValidatorError(final String fieldKey, final String rawMessage, final String userMessageKey) {
        this.fieldKey = fieldKey;
        this.rawMessage = rawMessage;
        this.userMessageKey = userMessageKey;
    }

    public String getRawMessage() {
        return rawMessage;
    }

    public String getUserMessageKey() {
        return userMessageKey;
    }

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
