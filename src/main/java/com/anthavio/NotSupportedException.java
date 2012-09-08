package com.anthavio;

/**
 * @author vanek
 *
 * Vyhodit pokud prijde ze vstupu neocekavana volba
 * Typicke pouziti v default casti switch
 * 
 */
public class NotSupportedException extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;

	public NotSupportedException(Object choice) {
		super(String.valueOf(choice));
	}
}
