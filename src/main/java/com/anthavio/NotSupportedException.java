package com.anthavio;

/**
 * Vyhodit pokud prijde ze vstupu neocekavana volba Typicke pouziti v default
 * casti switch
 * 
 * @author vanek
 * 
 */
public class NotSupportedException extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;

	public NotSupportedException(Object choice) {
		super("Value is not supported: '" + String.valueOf(choice) + "'");
	}
}
