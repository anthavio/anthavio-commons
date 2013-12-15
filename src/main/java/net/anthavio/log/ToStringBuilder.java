package net.anthavio.log;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import net.anthavio.util.HashUtil;
import net.anthavio.util.HibernateHelper;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;


/**
 * ToStringBuilder, ktery dokaze zpracovavat anotaci {@link ToString}.
 * 
 * Pomoci teto anotace lze napr. ignorovat nektere fieldy, ktere v toString()
 * nemaji byt (passwords apod).
 * 
 * Dale je upraveno logovani kolekci - neloguje se cely obsah, ale jen pocet
 * prvku
 * 
 * @see ToString
 * @see ToStringStyle
 * @author janousekm, vanek
 */
public class ToStringBuilder extends ReflectionToStringBuilder {

	private static final boolean hibernatePresent = HibernateHelper.isHibernatePresent();

	private static final ToStringStyle STYLE = new net.anthavio.log.ToStringStyle();

	public ToStringBuilder(Object object) {
		super(object, STYLE);
	}

	public ToStringBuilder(Object object, ToStringStyle style) {
		super(object, style);
	}

	/**
	 * Pokud je nastaveno "@ToString(ignore = true)" field bude ignorovan
	 * 
	 * @see ToString
	 */
	@Override
	protected boolean accept(Field field) {
		ToString tsAnnotation = field.getAnnotation(ToString.class);
		if (tsAnnotation != null && tsAnnotation.hide()) {
			return false;
		}
		return super.accept(field);
	}

	/**
	 * Rozsireni puvodni metody o omezene logovani kolekci
	 * 
	 * @see ToString
	 * @see org.apache.commons.lang.builder.ReflectionToStringBuilder#getValue(java.lang.reflect.Field)
	 */
	@Override
	protected Object getValue(Field field) throws IllegalArgumentException, IllegalAccessException {

		Object value = field.get(this.getObject());

		if (value == null) {
			return null;
		}

		ToString tsAnnotation = field.getAnnotation(ToString.class);
		if (hibernatePresent) {
			value = HibernateHelper.getHibernateProxiedValue(value);
		}

		// defaultne se vypisuje pouze delka poli a kolekci
		boolean detail = tsAnnotation != null && tsAnnotation.detail();

		if (detail == false) {
			if (value instanceof Collection<?>) {
				StringBuilder sb = new StringBuilder();
				int size = ((Collection<?>) value).size();
				sb.append(value.getClass().getSimpleName());
				sb.append("[").append(size).append("]");
				return sb.toString();
			} else if (value instanceof Map<?, ?>) {
				StringBuilder sb = new StringBuilder();
				int size = ((Map<?, ?>) value).size();
				sb.append(value.getClass().getSimpleName());
				sb.append("[").append(size).append("]");
				return sb.toString();
			} else if (value.getClass().isArray()) {
				StringBuilder sb = new StringBuilder();
				int size = Array.getLength(value);
				String simpleName = value.getClass().getSimpleName();
				sb.append(simpleName.substring(0, simpleName.length() - 2));
				sb.append("[").append(size).append("]");
				return sb.toString();
			}
		}

		boolean doHashValue = tsAnnotation != null && tsAnnotation.hash();

		if (doHashValue) {
			return HashUtil.MD5(value.toString(), null);
		}

		// nic s tim nedelame
		return value;
	}


	/**
	 * Pokud neni pouzita instance, vola se klasicky {@link ToStringBuilder}, cimz
	 * jsou vsechny features teto tridy ignorovany
	 */
	public static String reflectionToString(Object object) {
		return new ToStringBuilder(object, ToStringStyle.SHORT_PREFIX_STYLE).toString();
	}

	public static String reflectionToString(Object object, ToStringStyle style) {
		return new ToStringBuilder(object, style).toString();
	}

	public static String toString(Object object) {
		return new ToStringBuilder(object, ToStringStyle.SHORT_PREFIX_STYLE).toString();
	}

	public static String toString(Object object, ToStringStyle style) {
		return new ToStringBuilder(object, style).toString();
	}
}