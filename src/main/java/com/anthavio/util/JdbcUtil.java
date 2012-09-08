package com.anthavio.util;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Util metody pro praci s primitovnimy parametry na java.sql.PreparedStatement a java.sql.ResultSet
 */
public class JdbcUtil {

	public static Byte getByte(ResultSet rs, String columnName) throws SQLException {
		byte x = rs.getByte(columnName);
		if (rs.wasNull()) {
			return null;
		} else {
			return Byte.valueOf(x);
		}
	}

	public static Byte getByte(ResultSet rs, int columnIndex) throws SQLException {
		byte x = rs.getByte(columnIndex);
		if (rs.wasNull()) {
			return null;
		} else {
			return Byte.valueOf(x);
		}
	}

	public static Short getShort(ResultSet rs, String columnName) throws SQLException {
		short x = rs.getShort(columnName);
		if (rs.wasNull()) {
			return null;
		} else {
			return Short.valueOf(x);
		}
	}

	public static Short getShort(ResultSet rs, int columnIndex) throws SQLException {
		short x = rs.getShort(columnIndex);
		if (rs.wasNull()) {
			return null;
		} else {
			return Short.valueOf(x);
		}
	}

	public static Character getCharacter(ResultSet rs, String columnName) throws SQLException {
		String x = rs.getString(columnName);
		if (x == null) {
			return null;
		} else {
			if (x.length() > 1) {
				throw new IllegalArgumentException("Value is too long for char: " + x);
			}
			return x.charAt(0);
		}
	}

	public static Character getCharacter(ResultSet rs, int columnIndex) throws SQLException {
		String x = rs.getString(columnIndex);
		if (x == null) {
			return null;
		} else {
			if (x.length() > 1) {
				throw new IllegalArgumentException("Value is too long for char: " + x);
			}
			return x.charAt(0);
		}
	}

	public static Integer getInteger(ResultSet rs, String columnName) throws SQLException {
		int x = rs.getInt(columnName);
		if (rs.wasNull()) {
			return null;
		} else {
			return Integer.valueOf(x);
		}
	}

	public static Integer getInteger(ResultSet rs, int columnIndex) throws SQLException {
		int x = rs.getInt(columnIndex);
		if (rs.wasNull()) {
			return null;
		} else {
			return Integer.valueOf(x);
		}
	}

	public static String getString(ResultSet rs, String columnName) throws SQLException {
		String x = rs.getString(columnName);
		if (x != null) {
			x = x.trim();
		}
		return x;
	}

	public static String getString(ResultSet rs, int columnIndex) throws SQLException {
		String x = rs.getString(columnIndex);
		if (x != null) {
			x = x.trim();
			if (x.length() == 0) {
				return null;
			}
		}
		return x;
	}

	public static Long getLong(ResultSet rs, String columnName) throws SQLException {
		long x = rs.getLong(columnName);
		if (rs.wasNull()) {
			return null;
		} else {
			return Long.valueOf(x);
		}
	}

	public static Long getLong(ResultSet rs, int columnIndex) throws SQLException {
		long x = rs.getLong(columnIndex);
		if (rs.wasNull()) {
			return null;
		} else {
			return Long.valueOf(x);
		}
	}

	public static Boolean getBoolean(ResultSet rs, String columnName) throws SQLException {
		boolean x = rs.getBoolean(columnName);
		if (rs.wasNull()) {
			return null;
		} else {
			return Boolean.valueOf(x);
		}
	}

	public static Boolean getBoolean(ResultSet rs, int columnIndex) throws SQLException {
		boolean x = rs.getBoolean(columnIndex);
		if (rs.wasNull()) {
			return null;
		} else {
			return Boolean.valueOf(x);
		}
	}

	public static Float getFloat(ResultSet rs, String columnName) throws SQLException {
		float x = rs.getFloat(columnName);
		if (rs.wasNull()) {
			return null;
		} else {
			return Float.valueOf(x);
		}
	}

	public static Float getFloat(ResultSet rs, int columnIndex) throws SQLException {
		float x = rs.getFloat(columnIndex);
		if (rs.wasNull()) {
			return null;
		} else {
			return Float.valueOf(x);
		}
	}

	public static Double getDouble(ResultSet rs, String columnName) throws SQLException {
		double x = rs.getDouble(columnName);
		if (rs.wasNull()) {
			return null;
		} else {
			return Double.valueOf(x);
		}
	}

	public static Double getDouble(ResultSet rs, int columnIndex) throws SQLException {
		double x = rs.getDouble(columnIndex);
		if (rs.wasNull()) {
			return null;
		} else {
			return Double.valueOf(x);
		}
	}

	public static void setBoolean(PreparedStatement ps, int parameterIndex, Boolean x)
			throws SQLException {
		if (x != null) {
			ps.setBoolean(parameterIndex, x.booleanValue());
		} else {
			ps.setNull(parameterIndex, Types.BOOLEAN);
		}
	}

	public static void setByte(PreparedStatement ps, int parameterIndex, Byte x) throws SQLException {
		if (x != null) {
			ps.setByte(parameterIndex, x.byteValue());
		} else {
			ps.setNull(parameterIndex, Types.TINYINT);
		}
	}

	public static void setShort(PreparedStatement ps, int parameterIndex, Short x)
			throws SQLException {
		if (x != null) {
			ps.setShort(parameterIndex, x.shortValue());
		} else {
			ps.setNull(parameterIndex, Types.SMALLINT);
		}
	}

	public static void setInteger(PreparedStatement ps, int parameterIndex, Integer x)
			throws SQLException {
		if (x != null) {
			ps.setInt(parameterIndex, x.intValue());
		} else {
			ps.setNull(parameterIndex, Types.INTEGER);
		}
	}

	public static void setLong(PreparedStatement ps, int parameterIndex, Long x) throws SQLException {
		if (x != null) {
			ps.setLong(parameterIndex, x.longValue());
		} else {
			ps.setNull(parameterIndex, Types.BIGINT);
		}
	}

	public static void setDouble(PreparedStatement ps, int parameterIndex, Double x)
			throws SQLException {
		if (x != null) {
			ps.setDouble(parameterIndex, x.doubleValue());
		} else {
			ps.setNull(parameterIndex, Types.DOUBLE);
		}
	}

	public static void setFloat(PreparedStatement ps, int parameterIndex, Float x)
			throws SQLException {
		if (x != null) {
			ps.setFloat(parameterIndex, x.floatValue());
		} else {
			ps.setNull(parameterIndex, Types.FLOAT);
		}
	}

	public static void setBigDecimal(PreparedStatement ps, int parameterIndex, BigDecimal x)
			throws SQLException {
		if (x != null) {
			ps.setBigDecimal(parameterIndex, x);
		} else {
			ps.setNull(parameterIndex, Types.DECIMAL);

		}
	}

	public static void setDate(PreparedStatement ps, int parameterIndex, java.util.Date x)
			throws SQLException {
		if (x != null) {
			ps.setDate(parameterIndex, new java.sql.Date(x.getTime()));
		} else {
			ps.setNull(parameterIndex, Types.DATE);

		}
	}

	public static void setString(PreparedStatement ps, int parameterIndex, String x)
			throws SQLException {
		if (x != null && x.length() > 0) {
			x = x.trim();
			if (x.length() > 0) {
				ps.setString(parameterIndex, x);
				return;
			}
		}
		ps.setNull(parameterIndex, ps.getParameterMetaData().getParameterType(parameterIndex));

	}

	public static void setCharacter(PreparedStatement ps, int parameterIndex, Character x)
			throws SQLException {
		if (x != null) {
			ps.setString(parameterIndex, x.toString());
		} else {
			ps.setNull(parameterIndex, Types.CHAR);

		}
	}
}