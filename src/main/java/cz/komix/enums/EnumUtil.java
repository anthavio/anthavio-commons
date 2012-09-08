package cz.komix.enums;

public final class EnumUtil {

	private EnumUtil() {
	}

	public static <I extends EnumLong<?>> I getEnum(Class<I> type, long id) {
		I[] types = type.getEnumConstants();
		for (I t : types) {
			if (t.getId() == id) {
				return t;
			}
		}
		throw new IllegalArgumentException("Unknown EnumLong id: " + id);
	}

	public static <I extends EnumInt<?>> I getEnum(Class<I> type, int id) {
		I[] types = type.getEnumConstants();
		for (I t : types) {
			if (t.getId() == id) {
				return t;
			}
		}
		throw new IllegalArgumentException("Unknown EnumInt id: " + id);
	}

	public static <I extends EnumStr<?>> I getEnum(Class<I> type, String code) {
		I[] types = type.getEnumConstants();
		for (I t : types) {
			if (t.getCode().equals(code)) {
				return t;
			}
		}
		throw new IllegalArgumentException("Unknown EnumStr code: " + code);
	}
}
