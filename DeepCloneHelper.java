package org.example;

import java.lang.reflect.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DeepCloneHelper {
	private static Map<Object, Object> clonesMap = new HashMap<>();

	public static <T> T deepClone(T object) {
		try {
			clonesMap = new HashMap<>();
			return deepCloneHelper(object);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static <T> T deepCloneHelper(T object) throws IllegalAccessException {
		if (object == null) return null;

		if (clonesMap.containsKey(object)) {
			return (T) clonesMap.get(object);
		}
		if (isImmutable(object)) {
			return object;
		}
		if (object instanceof Collection) {
			return cloneCollection(object);
		}
		if (object instanceof Map) {
			return cloneMap(object);
		}
		if (object.getClass().isArray()) {
			return cloneArray(object);
		}

		return cloneObject(object);
	}

	private static <T> T cloneObject(T object) throws IllegalAccessException {
		var clazz = object.getClass();
		T clone = (T) createInstance(clazz);

		clonesMap.put(object, clone);

		var fields = clazz.getDeclaredFields();
		for (var field : fields) {
			field.setAccessible(true);
			if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
				continue;
			}
			var fieldValue = field.get(object);
			var clonedFieldValue = deepCloneHelper(fieldValue);
			field.set(clone, clonedFieldValue);
		}
		return clone;
	}

	private static <T> T createInstance(Class<T> clazz) {
		T clone;
		try {
			var noArgsConstructor = clazz.getDeclaredConstructor();
			noArgsConstructor.setAccessible(true);
			clone = (T) noArgsConstructor.newInstance();
		} catch (InstantiationException | NoSuchMethodException | InvocationTargetException e) {
			try {
				// If no-argument constructor is not available, use a constructor with field types
				var fields = clazz.getDeclaredFields();
				var fieldTypes = new Class<?>[fields.length];

				for (var i = 0; i < fields.length; i++) {
					fields[i].setAccessible(true);
					fieldTypes[i] = fields[i].getType();
				}

				var allFieldsConstructor = clazz.getDeclaredConstructor(fieldTypes);
				allFieldsConstructor.setAccessible(true);

				// Create default values for the constructor parameters
				var fieldValues = new Object[fields.length];
				for (int i = 0; i < fields.length; i++) {
					fieldValues[i] = getDefaultValue(fieldTypes[i]);
				}

				clone = (T) allFieldsConstructor.newInstance(fieldValues);
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		return clone;

	}

	private static <T> T cloneMap(T object) throws IllegalAccessException {
		var map = (Map<?, ?>) object;
		Map<Object, Object> newMap;
		try {
			newMap = (Map<Object, Object>) object.getClass().newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		}
		clonesMap.put(object, newMap);
		for (Map.Entry<?, ?> entry : map.entrySet()) {
			var clonedKey = deepCloneHelper(entry.getKey());
			var clonedValue = deepCloneHelper(entry.getValue());
			newMap.put(clonedKey, clonedValue);
		}
		return (T) newMap;
	}

	private static <T> T cloneCollection(T object) throws IllegalAccessException {

		var collection = (Collection<?>) object;
		Collection<Object> newCollection;
		try {
			newCollection = (Collection<Object>) object.getClass().newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		}
		clonesMap.put(object, newCollection);
		for (var element : collection) {
			var clonedElement = deepCloneHelper(element);
			newCollection.add(clonedElement);
		}
		return (T) newCollection;

	}

	private static <T> T cloneArray(T object) throws IllegalAccessException {
		var componentType = object.getClass().getComponentType();
		if (!componentType.isPrimitive()) {
			var length = Array.getLength(object);
			var newArray = Array.newInstance(componentType, length);
			clonesMap.put(object, newArray);
			for (var i = 0; i < length; i++) {
				var element = Array.get(object, i);
				var clonedElement = deepCloneHelper(element);
				Array.set(newArray, i, clonedElement);
			}
			return (T) newArray;
		} else {
			var length = Array.getLength(object);
			var newArray = Array.newInstance(componentType, length);
			System.arraycopy(object, 0, newArray, 0, length);
			return (T) newArray;
		}
	}


	private static <T> boolean isImmutable(T object) {
		return object instanceof Integer || object instanceof Long ||
				object instanceof Short || object instanceof Byte ||
				object instanceof Double || object instanceof Float ||
				object instanceof Boolean || object instanceof Character ||
				object instanceof String;
	}
	private static Object getDefaultValue(Class<?> type) {
		if (type.isPrimitive()) {
			if (type == boolean.class) return false;
			if (type == byte.class) return (byte) 0;
			if (type == char.class) return '\0';
			if (type == short.class) return (short) 0;
			if (type == int.class) return 0;
			if (type == long.class) return 0L;
			if (type == float.class) return 0.0f;
			if (type == double.class) return 0.0;
		}
		return null;
	}
}
