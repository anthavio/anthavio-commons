package net.anthavio.enums;

/**
 * @author vanek
 *
 */
public interface EnumInt<E extends Enum<E>> {

	int getId();

	//E getEnum(); //implementace: return this
}
