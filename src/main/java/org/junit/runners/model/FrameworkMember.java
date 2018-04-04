package org.junit.runners.model;

import java.lang.reflect.Modifier;
import java.util.List;

/**
 * Parent class for {@link FrameworkField} and {@link FrameworkMethod}
 *
 * @since 4.7
 */
public abstract class FrameworkMember<T extends FrameworkMember<T>> implements
        Annotatable {
    abstract boolean isShadowedBy(T otherMember);

    /**
     * Check if this member is shadowed by any of the given members. If it
     * is, the other member is removed.
     * 
     * @return member that should be used, or {@code null} if no member should be used.
     */
    final T handlePossibleShadowedMember(List<T> members) {
        for (int i = members.size() - 1; i >=0; i--) {
            T otherMember = members.get(i);
            if (isShadowedBy(otherMember)) {
                if (otherMember.isBridgeMethod()) {
                    /*
                     *  We need to return the previously-encountered bridge method
                     *  because JUnit won't be able to call the parent method,
                     *  because the parent class isn't public.
                     */
                    members.remove(i);
                    return otherMember;
                }
                // We found a shadowed member that isn't a bridge method. Ignore it.
                return null;
            }
        }
        // No shadow or bridge method found. The caller should add *this* member.
        return self();
    }

    @SuppressWarnings("unchecked")
    private T self() {
        return (T) this;
    }

    abstract boolean isBridgeMethod();

    protected abstract int getModifiers();

    /**
     * Returns true if this member is static, false if not.
     */
    public boolean isStatic() {
        return Modifier.isStatic(getModifiers());
    }

    /**
     * Returns true if this member is public, false if not.
     */
    public boolean isPublic() {
        return Modifier.isPublic(getModifiers());
    }

    public abstract String getName();

    public abstract Class<?> getType();

    public abstract Class<?> getDeclaringClass();
}
