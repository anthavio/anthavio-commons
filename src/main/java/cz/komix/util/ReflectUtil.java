/**
 * 
 */
package cz.komix.util;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author vanek
 *
 */
public class ReflectUtil {

	/**
	 * Returns a set of all interfaces implemented by class supplied. This includes all
	 * interfaces directly implemented by this class as well as those implemented by
	 * superclasses or interface superclasses.
	 * 
	 * @param clazz
	 * @return all interfaces implemented by this class
	 */
	public static Set<Class<?>> getImplementedInterfaces(Class<?> clazz) {
		Set<Class<?>> interfaces = new HashSet<Class<?>>();

		if (clazz.isInterface()) {
			interfaces.add(clazz);
		}

		while (clazz != null) {
			for (Class<?> iface : clazz.getInterfaces()) {
				interfaces.addAll(getImplementedInterfaces(iface));
			}
			clazz = clazz.getSuperclass();
		}

		return interfaces;
	}

	/**
	 * Funguje jak pro extends tak implements tridy
	 * Navic vraci TypeVariable misto null pokud generic typ neni znam
	 * 
	 * Returns an array of Type objects representing the actual type arguments
	 * to targetType used by clazz.
	 * 
	 * @param clazz the implementing class (or subclass)
	 * @param targetType the implemented generic class or interface
	 * @return an array of Type objects or null
	 */
	public static <T> Type[] getActualTypeArguments(Class<? extends T> clazz, Class<T> targetType) {
		Set<Class<?>> classes = new HashSet<Class<?>>();
		classes.add(clazz);

		if (targetType.isInterface()) {
			classes.addAll(getImplementedInterfaces(clazz));
		}

		Class<?> superClass = clazz.getSuperclass();
		while (superClass != null) {
			classes.add(superClass);
			superClass = superClass.getSuperclass();
		}

		for (Class<?> search : classes) {
			for (Type type : (targetType.isInterface() ? search.getGenericInterfaces() : new Type[] { search
					.getGenericSuperclass() })) {
				if (type instanceof ParameterizedType) {
					ParameterizedType parameterizedType = (ParameterizedType) type;
					if (targetType.equals(parameterizedType.getRawType())) {
						return parameterizedType.getActualTypeArguments();
					}
				}
			}
		}

		return null;
	}

	/**
	 * Funguje jen pro tridy dedici z tridy a ne implementujici interface
	 * 
	 * Get the actual type arguments a child class has used to extend a generic base class.
	 * (Taken from http://www.artima.com/weblogs/viewpost.jsp?thread=208860.
	 * Thanks mathieu.grenonville for finding this solution!)
	 */
	public static <T> List<Class<?>> getTypeArguments(Class<T> baseClass, Class<? extends T> childClass) {
		Map<Type, Type> resolvedTypes = new HashMap<Type, Type>();
		Type type = childClass;
		// start walking up the inheritance hierarchy until we hit baseClass
		while (!getClass(type).equals(baseClass)) {
			if (type instanceof Class) {
				// there is no useful information for us in raw types, so just
				// keep going.
				type = ((Class) type).getGenericSuperclass();
			} else {
				ParameterizedType parameterizedType = (ParameterizedType) type;
				Class<?> rawType = (Class) parameterizedType.getRawType();

				Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
				TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
				for (int i = 0; i < actualTypeArguments.length; i++) {
					resolvedTypes.put(typeParameters[i], actualTypeArguments[i]);
				}

				if (!rawType.equals(baseClass)) {
					type = rawType.getGenericSuperclass();
				}
			}
		}

		// finally, for each actual type argument provided to baseClass,
		// determine (if possible)
		// the raw class for that type argument.
		Type[] actualTypeArguments;
		if (type instanceof Class) {
			actualTypeArguments = ((Class) type).getTypeParameters();
		} else {
			actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
		}
		List<Class<?>> typeArgumentsAsClasses = new ArrayList<Class<?>>();
		// resolve types by chasing down type variables.
		for (Type baseType : actualTypeArguments) {
			while (resolvedTypes.containsKey(baseType)) {
				baseType = resolvedTypes.get(baseType);
			}
			typeArgumentsAsClasses.add(getClass(baseType));
		}
		return typeArgumentsAsClasses;
	}

	/**
	 * Get the underlying class for a type, or null if the type is a variable type.
	 */
	private static Class<?> getClass(Type type) {
		if (type instanceof Class) {
			return (Class) type;
		} else if (type instanceof ParameterizedType) {
			return getClass(((ParameterizedType) type).getRawType());
		} else if (type instanceof GenericArrayType) {
			Type componentType = ((GenericArrayType) type).getGenericComponentType();
			Class<?> componentClass = getClass(componentType);
			if (componentClass != null) {
				return Array.newInstance(componentClass, 0).getClass();
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
}
