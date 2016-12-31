package ad4si2.lfp.utils.events.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ChangeEvent implements Serializable {

    @Nonnull
    private ChangeEventType changeEventType;

    @Nonnull
    private Object object;

    @Nullable
    private Object oldObject;

    private ChangeEvent(@Nonnull final ChangeEventType changeEventType, @Nonnull final Object object) {
        this.changeEventType = changeEventType;
        this.object = object;
    }

    private ChangeEvent(@Nonnull final ChangeEventType changeEventType, @Nonnull final Object object, @Nullable final Object oldObject) {
        this.changeEventType = changeEventType;
        this.object = object;
        this.oldObject = oldObject;
    }

    @Nonnull
    public static ChangeEvent createEvent(@Nonnull final Object object, final ChatChangeWhen when) {
        return new ChangeEvent(when.equals(ChatChangeWhen.PRE) ? ChangeEventType.PRE_CREATE : ChangeEventType.POST_CREATE, object);
    }

    @Nonnull
    public static ChangeEvent updateEvent(@Nonnull final Object newObject, @Nonnull final Object oldObject, final ChatChangeWhen when) {
        return new ChangeEvent(when.equals(ChatChangeWhen.PRE) ? ChangeEventType.PRE_UPDATE : ChangeEventType.POST_UPDATE, newObject, oldObject);
    }

    @Nonnull
    public static ChangeEvent deleteEvent(@Nonnull final Object object, final ChatChangeWhen when) {
        return new ChangeEvent(when.equals(ChatChangeWhen.PRE) ? ChangeEventType.PRE_DELETE : ChangeEventType.POST_DELETE, object);
    }

    @Nonnull
    public <T> ChangeEvent doIf(@Nonnull final Class<T> changeEntityType, @Nonnull final ChangeEventType eventType,
                                @Nonnull final Consumer<T> consumer) {
        if (changeEntityType.isAssignableFrom(object.getClass()) && changeEventType.equals(eventType)) {
            consumer.accept(changeEntityType.cast(object));
        }
        return this;
    }

    @Nonnull
    public <T> ChangeEvent doIf(@Nonnull final Class<T> changeEntityType, @Nonnull final ChangeEventType eventType,
                                @Nonnull final BiConsumer<T, T> consumer) {
        if (changeEntityType.isAssignableFrom(object.getClass()) && changeEventType.equals(eventType)) {
            consumer.accept(changeEntityType.cast(object), changeEntityType.cast(oldObject));
        }
        return this;
    }

    @Nonnull
    public <T> ChangeEvent onPreCreate(@Nonnull final Class<T> changeEntityType, @Nonnull final Consumer<T> consumer) {
        return doIf(changeEntityType, ChangeEventType.PRE_CREATE, consumer);
    }

    @Nonnull
    public <T> ChangeEvent onPostCreate(@Nonnull final Class<T> changeEntityType, @Nonnull final Consumer<T> consumer) {
        return doIf(changeEntityType, ChangeEventType.POST_CREATE, consumer);
    }

    @Nonnull
    public <T> ChangeEvent onPreUpdate(@Nonnull final Class<T> changeEntityType, @Nonnull final BiConsumer<T, T> consumer) {
        return doIf(changeEntityType, ChangeEventType.PRE_UPDATE, consumer);
    }

    @Nonnull
    public <T> ChangeEvent onPostUpdate(@Nonnull final Class<T> changeEntityType, @Nonnull final BiConsumer<T, T> consumer) {
        return doIf(changeEntityType, ChangeEventType.POST_UPDATE, consumer);
    }

    @Nonnull
    public <T> ChangeEvent onPreDelete(@Nonnull final Class<T> changeEntityType, @Nonnull final Consumer<T> consumer) {
        return doIf(changeEntityType, ChangeEventType.PRE_DELETE, consumer);
    }

    @Nonnull
    public <T> ChangeEvent onPostDelete(@Nonnull final Class<T> changeEntityType, @Nonnull final Consumer<T> consumer) {
        return doIf(changeEntityType, ChangeEventType.POST_DELETE, consumer);
    }

    @Nonnull
    public ChangeEventType getChangeEventType() {
        return changeEventType;
    }

    @Nonnull
    public Object getObject() {
        return object;
    }

    @Nullable
    public Object getOldObject() {
        return oldObject;
    }

    @Override
    public String toString() {
        return "ChangeEvent {" +
                "changeEventType=" + changeEventType +
                ", object=" + object +
                ", oldObject=" + oldObject +
                '}';
    }

    public enum ChangeEventType {
        PRE_CREATE(false),
        POST_CREATE(true),
        PRE_UPDATE(false),
        POST_UPDATE(true),
        PRE_DELETE(false),
        POST_DELETE(true);

        private boolean isPostEvent;

        ChangeEventType(final boolean isPostEvent) {
            this.isPostEvent = isPostEvent;
        }

        public boolean isPostEvent() {
            return isPostEvent;
        }
    }

    public enum ChatChangeWhen {
        PRE, POST
    }
}
