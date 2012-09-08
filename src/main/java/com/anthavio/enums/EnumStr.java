package com.anthavio.enums;

/**
 * @author vanek
 *
 */
public interface EnumStr<E extends Enum<E>> {

	String getCode();

	//E getEnum(); //implementace: return this 
}
