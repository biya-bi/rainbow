/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.common.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * @author Biya-Bi
 */
public class BeanUtilities {

	/**
	 * Retrieve a value from a property using
	 *
	 * @param obj
	 *            The object who's property you want to fetch
	 * @param property
	 *            The property name
	 * @param defaultValue
	 *            A default value to be returned if either a) The property is
	 *            not found or b) if the property is found but the value is null
	 * @return THe value of the property
	 */
	public static <T> T getProperty(Object obj, String property, T defaultValue) {
		@SuppressWarnings("unchecked")
		T returnValue = (T) getProperty(obj, property);
		if (returnValue == null) {
			returnValue = defaultValue;
		}

		return returnValue;

	}

	/**
	 * Fetch a property from an object. For example of you wanted to get the foo
	 * property on a bar object you would normally call {@code bar.getFoo()}.
	 * This method lets you call it like
	 * {@code BeanUtil.getProperty(bar, "foo","get")}
	 *
	 * @param obj
	 *            The object who's property you want to fetch
	 * @param property
	 *            The property name
	 * @param getterPrefix
	 *            The prefix of the getter method.
	 * @return The value of the property or null if it does not exist.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object getProperty(Object obj, String property, String getterPrefix) {
		Object returnValue = null;

		try {
			String methodName = getterPrefix + property.substring(0, 1).toUpperCase()
					+ property.substring(1, property.length());
			Class clazz = obj.getClass();
			Method method = clazz.getMethod(methodName, new Class[] {});
			returnValue = method.invoke(obj, new Object[] {});
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new RuntimeException(e);
		}

		return returnValue;
	}

	/**
	 * Fetch a property from an object. For example of you wanted to get the foo
	 * property on a bar object you would normally call {@code bar.getFoo()}.
	 * This method lets you call it like
	 * {@code BeanUtil.getProperty(bar, "foo")}
	 *
	 * @param obj
	 *            The object who's property you want to fetch
	 * @param property
	 *            The property name
	 * @return The value of the property or null if it does not exist.
	 */
	public static Object getProperty(Object obj, String property) {
		return getProperty(obj, property, "get");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void setProperty(Object obj, Object value, String property, Class propertyClass) {

		try {
			String methodName = "set" + property.substring(0, 1).toUpperCase()
					+ property.substring(1, property.length());
			Class clazz = obj.getClass();
			Method method = clazz.getMethod(methodName, new Class[] { propertyClass });
			method.invoke(obj, new Object[] { value });

		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
}
