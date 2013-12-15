package net.anthavio.log;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Anotace nese informace pro {@link ToStringBuilder}. Pomoci
 * {@link ToString#hide()} lze oznacit field, ktery nesmi byt logovan.
 * {XXX@link ToString#maxLength()} bude jednoho krasneho dne orezavat hodnotu fieldu na
 * zadany pocet znaku
 * 
 * @author janousekm
 */
@Documented
@Target(FIELD)
@Retention(RUNTIME)
public @interface ToString {

	/** Ignorovat field v toString()? */
	boolean hide() default false;

	/** Ma se pri toString() vypsat cely obsah kolekce? */
	boolean detail() default false;

	/**
	 * Oznacuje osobni udaj
	 * Do logu je vypsan zakodovany obsah fieldu  
	 */
	boolean hash() default false;
}