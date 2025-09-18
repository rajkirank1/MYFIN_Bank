package com.company.userservice.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Utility to check whether an object (and nested objects) contain empty fields.
 *
 * Note: "empty" is defined as:
 * - null reference
 * - empty String (trimmed length == 0)
 * - empty Collection or Map
 * - empty array (length == 0)
 *
 * The checker recurses into fields that are not part of the java.* packages
 * to inspect nested DTO/entity objects. Primitive fields are not considered empty.
 */
public class FieldChecker {

    /**
     * Checks if an object has empty fields.
     *
     * @param object the object to check
     * @return true if the object has empty fields, false otherwise
     */
    public static boolean hasEmptyFields(Object object) {
        if (object == null) {
            return true;
        }
        // Use IdentityHashMap as visited set to prevent infinite recursion for cyclic graphs
        IdentityHashMap<Object, Boolean> visited = new IdentityHashMap<>();
        return hasEmptyFieldsRecursive(object, visited);
    }

    private static boolean hasEmptyFieldsRecursive(Object object, IdentityHashMap<Object, Boolean> visited) {
        if (object == null) return true;

        // avoid cycles
        if (visited.containsKey(object)) {
            return false; // already inspected and we didn't find emptiness then
        }
        visited.put(object, Boolean.TRUE);

        Class<?> cls = object.getClass();

        // handle basic types directly
        if (object instanceof String) {
            return ((String) object).trim().isEmpty();
        }
        if (object instanceof Collection) {
            return ((Collection<?>) object).isEmpty();
        }
        if (object instanceof Map) {
            return ((Map<?, ?>) object).isEmpty();
        }
        if (cls.isArray()) {
            return Array.getLength(object) == 0;
        }

        // For JDK types (java.*), we don't recurse into internals (except handled above).
        // Treat them as non-empty (unless they were null / empty handled previously).
        String pkgName = cls.getPackage() != null ? cls.getPackage().getName() : "";
        if (pkgName.startsWith("java.")) {
            return false;
        }

        // inspect declared fields (including private). Do not inspect static fields.
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            boolean accessible = field.isAccessible();
            try {
                if (!accessible) field.setAccessible(true);
                Object value = field.get(object);

                // primitives can't be null and are not considered empty
                if (field.getType().isPrimitive()) {
                    continue;
                }

                // direct checks
                if (value == null) {
                    return true;
                }
                if (value instanceof String) {
                    if (((String) value).trim().isEmpty()) {
                        return true;
                    } else {
                        continue;
                    }
                }
                if (value instanceof Collection) {
                    if (((Collection<?>) value).isEmpty()) return true;
                    else continue;
                }
                if (value instanceof Map) {
                    if (((Map<?, ?>) value).isEmpty()) return true;
                    else continue;
                }
                if (value.getClass().isArray()) {
                    if (Array.getLength(value) == 0) return true;
                    else continue;
                }

                // if field type belongs to java.* (but not handled above), skip recursion
                Package fPkg = value.getClass().getPackage();
                String fPkgName = fPkg != null ? fPkg.getName() : "";
                if (fPkgName.startsWith("java.")) {
                    // not considered empty (already handled common cases)
                    continue;
                }

                // otherwise recursively inspect nested object
                if (hasEmptyFieldsRecursive(value, visited)) {
                    return true;
                }

            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to inspect field: " + field.getName(), e);
            } finally {
                if (!accessible) {
                    field.setAccessible(false);
                }
            }
        }

        return false;
    }
}
