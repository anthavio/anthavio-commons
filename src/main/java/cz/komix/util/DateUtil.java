package cz.komix.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;

/**
 * 
 * @author vanek
 * 
 * TODO Prevzano a nutno polepsit
 */
public class DateUtil {

	/**
	 * Pocet milisekund za den
	 */
	public static final long DAY_MILLIS = 86400000;

	/**
	 * Počet milisekund za hodinu
	 */
	public static final long HOUR_MILLIS = 3600000;

	public static final String D_M_YYYY = "d.M.yyyy";

	public static final String DD_MM_YYYY = "dd.MM.yyyy";

	public static final String DD_MM_YY = "dd.MM.yy";

	public static final String D_M_YYYY_HH_MM = "d.M.yyyy HH:mm";

	public static final String DD_MM_YYYY_HH_MM = "dd.MM.yyyy HH:mm";

	public static final String D_M_YYYY_HH_MM_SS = "d.M.yyyy HH:mm:ss";

	public static final String D_M_YYYY_HH_MM_SS_SSS = "d.M.yyyy HH:mm:ss,SSS";

	private static Map<String, FastDateFormat> fmtCache = new HashMap<String, FastDateFormat>();

	public static String format(Calendar calendar, String pattern) {
		return format(calendar.getTime(), pattern);
	}

	public static String format(Date date, String pattern) {
		FastDateFormat dateFormat = fmtCache.get(pattern);
		if (dateFormat == null) {
			dateFormat = FastDateFormat.getInstance(pattern);
			fmtCache.put(pattern, dateFormat);
		}
		return dateFormat.format(date);
	}

	private static final Pattern TZ_REGEX = Pattern.compile("([+-][0-9][0-9]):?([0-9][0-9])$");

	public static String printXsdDateTime(Date dt) {
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		DateFormat tzFormatter = new SimpleDateFormat("Z");
		String timezone = tzFormatter.format(dt);
		return formatter.format(dt) + timezone.substring(0, 3) + ":" + timezone.substring(3);
	}

	/**
	 * xsd:dateTime           2009-12-06T15:59:34+01:00
	 * yyyy-MM-dd'T'HH:mm:ssZ 2009-12-06T15:59:34+0100
	 */
	public static Date parseXsdDateTime(String xsdDateTime) {
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		Matcher mat = TZ_REGEX.matcher(xsdDateTime);
		if (mat.find()) {
			String tzCode = "GMT" + mat.group(1) + mat.group(2); // eg "GMT+0100"
			TimeZone tz = TimeZone.getTimeZone(tzCode);
			sdf.setTimeZone(tz);
		}

		try {
			return sdf.parse(xsdDateTime);
		} catch (ParseException px) {
			throw new IllegalArgumentException(xsdDateTime + " is not xsd:dateTime");
		}
	}

	/**
	 * throw IllegalArgumentException when @value is invalid
	 */
	public static Date parse(String value, String pattern) {
		if (value == null) {
			throw new IllegalArgumentException("Null pattern");
		}
		if (value == null) {
			throw new IllegalArgumentException("Null value");
		}
		try {
			return new SimpleDateFormat(pattern).parse(value);
		} catch (ParseException px) {
			throw new IllegalArgumentException(value + " is not parseable with pattern " + pattern);
		}
	}

	/**
	 * return null when @value is invalid
	 */
	public static Date parseQuietly(String value, String pattern) {
		if (pattern == null) {
			throw new IllegalArgumentException("Null pattern");
		}
		if (StringUtils.isBlank(value)) {
			return null;
		}
		try {
			return new SimpleDateFormat(pattern).parse(value);
		} catch (ParseException px) {
			return null;
		}
	}

	public static java.sql.Date getSqlDate(Date value) {
		if (value != null) {
			return new java.sql.Date(value.getTime());
		}
		return null;
	}

	public static String getDateToSQLOracle(Date value) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		return ("TO_DATE('" + sdf.format(value) + "','DD.MM.YYYY')");
	}

	public static java.sql.Date getActualSqlDate() {
		return new java.sql.Date(new Date().getTime());
	}

	public static Date getActualDateNoTime() {
		return getDateNoTime(new Date());
	}

	public static Date getActualDateTime() {
		return new Date();
	}

	/**
	 * @return Date without Time
	 */
	public static Date getDateNoTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	/**
	 * @return 31.12.2999 without time
	 */
	public static Date getDate2999() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, 2999);
		c.set(Calendar.MONTH, 11);
		c.set(Calendar.DAY_OF_MONTH, 31);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}

	/**
	 * @return 1.1.0001 without time
	 */
	public static Date getDate0001() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, 1);
		c.set(Calendar.MONTH, 0);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}

	/**
	 * @return Calendar without Time
	 */
	public static Calendar getCalendarNoTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}

	/**
	 * Vrati pocet hodin mezi prvnim a druhym datumem.
	 */
	public static int getHoursDiff(Date date1, Date date2) {
		long l_dat1 = date1.getTime();
		long l_dat2 = date2.getTime();
		long rozdil = l_dat2 - l_dat1;
		return (int) (rozdil / HOUR_MILLIS); //we want floor not round!
	}

	public static int getDaysDiff(Date date1, Date date2) {
		return getDaysDiff(date1, date2, true);
	}

	/**
	 * Vrati pocet dni mezi prvnim a druhym datumem, nehlede na cas
	 */
	public static int getDaysDiff(Date older, Date newer, boolean absValue) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(older);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		long lOlder = cal.getTimeInMillis() + cal.getTimeZone().getOffset(cal.getTimeInMillis());

		cal.setTime(newer);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		long lNewer = cal.getTimeInMillis() + cal.getTimeZone().getOffset(cal.getTimeInMillis());

		int diff = Math.round((lNewer - lOlder) / DAY_MILLIS);
		return absValue ? Math.abs(diff) : diff;
	}

	/**
	 * Vrati pocet mesicu mezi prvnim a druhym datem 
	 * 12.8.2006 - 12.7.2006 = 1
	 * 13.8.2006 - 12.7.2006 = 2
	 */
	public static int getMonthsDiff(Date older, Date newer) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(older);
		int rok1 = cal.get(Calendar.YEAR);
		int mes1 = cal.get(Calendar.MONTH);
		int den1 = cal.get(Calendar.DAY_OF_MONTH);

		cal.setTime(newer);
		int rok2 = cal.get(Calendar.YEAR);
		int mes2 = cal.get(Calendar.MONTH);
		int den2 = cal.get(Calendar.DAY_OF_MONTH);

		int diff = Math.abs(rok2 - rok1) * 12 + (mes2 - mes1);
		if (den1 <= den2) {
			return diff;
		} else { //den1 > den2 = necely mesic
			return diff - 1;
		}
	}

	public static int getYearsDiff(Date older, Date newer) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(older);
		int rok1 = cal.get(Calendar.YEAR);
		int den1 = cal.get(Calendar.DAY_OF_YEAR);

		cal.setTime(newer);
		int rok2 = cal.get(Calendar.YEAR);
		int den2 = cal.get(Calendar.DAY_OF_YEAR);

		int diff = Math.abs(rok2 - rok1);
		if (den1 <= den2) {
			return diff;
		} else { //den1 > den2 = necely mesic
			return diff - 1;
		}
	}

	/**
	 * Vrati pocet roku mezi prvnim a druhym datem
	 */
	public static int getPersonAge(Date dateBirth, Date dateTest) {
		Calendar c = Calendar.getInstance();
		c.setTime(dateBirth);
		int birthYear = c.get(Calendar.YEAR);
		int birthDay = c.get(Calendar.DAY_OF_YEAR);

		c.setTime(dateTest);
		int testYear = c.get(Calendar.YEAR);
		int testDay = c.get(Calendar.DAY_OF_YEAR);

		int years = testYear - birthYear;
		if (testDay >= birthDay) {
			return years;
		} else {
			return years - 1;
		}
	}

	/**
	 * TODO co je toto????
	 */
	public static long getDiffInDays(Date higher, Date lower) {
		long millisDiff = higher.getTime() - lower.getTime();
		long days = millisDiff % (3600 * 24);

		return days;
	}

	public static Date addSeconds(Date date, int count) {
		return add(date, count, Calendar.SECOND);
	}

	public static Date addHours(Date date, int count) {
		return add(date, count, Calendar.HOUR_OF_DAY);
	}

	public static Date addDays(Date date, int count) {
		return add(date, count, Calendar.DAY_OF_YEAR);
	}

	public static Date addMonths(Date date, int count) {
		return add(date, count, Calendar.MONTH);
	}

	public static Date addYears(Date date, int count) {
		return add(date, count, Calendar.YEAR);
	}

	public static Date add(Date date, int count, int field) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(field, count);
		return c.getTime();
	}

	public static boolean isIntInterval(Date mezOd, Date mezDo, Date testOd, Date testDo) {
		boolean retval = false;
		if (mezDo == null) {
			mezDo = DateUtil.getDate2999();
		}
		if (testDo == null) {
			testDo = DateUtil.getDate2999();
		}
		retval = isInInterval(mezOd, mezDo, testOd);
		if (retval) {
			return retval;
		}
		retval = isInInterval(mezOd, mezDo, testDo);
		if (retval) {
			return retval;
		}
		if (testOd != null
				&& ((testOd.after(mezOd) && testOd.before(mezDo)) || (testDo.before(mezDo) && testDo
						.after(mezOd))) || (testOd.before(mezOd) && testDo.after(mezDo))) {
			retval = true;
		}

		return retval;
	}

	/**
	 * Test datumoveho intervalu na vnitřek intervalu První zadaný interval musí
	 * být vždy větší nebo stejně velký než testovaný interval.
	 * 
	 */
	public static boolean isInInterval(Date intPlod, Date intPldo, Date testPlod, Date testPldo) {
		boolean retval = false;
		if (intPldo == null) {
			intPldo = DateUtil.getDate2999();
		}
		if (testPldo == null) {
			testPldo = DateUtil.getDate2999();
		}
		retval = isInInterval(intPlod, intPldo, testPlod);
		if (!retval) {
			return retval;
		}
		retval = isInInterval(intPlod, intPldo, testPldo);
		return retval;
	}

	/**
	 * Test datumu na interval
	 * 
	 */
	public static boolean isInInterval(Date intOd, Date intDo, Date date) {
		boolean retval = false;
		if (intDo == null) {
			intDo = DateUtil.getDate2999();
		}
		if (date == null) {
			date = DateUtil.getDate2999();
		}
		if ((intOd != null && date.compareTo(intOd) == 0) || (date.compareTo(intDo) == 0)
				|| (intOd != null && date.after(intOd) && date.before(intDo))) {
			retval = true;
		}

		return retval;
	}

	/**
	 * Test datumu na interval. Metoda sleduje, zda je testovaný interval za
	 * zadaným datumem
	 */
	public static boolean isIntervalAfterDate(Date intOd, Date intDo, Date date) {
		boolean retval = false;
		if (intDo == null) {
			intDo = DateUtil.getDate2999();
		}
		if (date == null) {
			date = DateUtil.getDate2999();
		}
		if (intOd != null && date.before(intOd) && date.before(intDo)) {
			retval = true;
		}

		return retval;
	}

	/**
	 * Test datumu na interval. Metoda sleduje, zda je testovaný interval před
	 * zadaným datumem
	 * 
	 */
	public static boolean isIntervalBeforeDate(Date intOd, Date intDo, Date date) {
		boolean retval = false;
		if (date == null) {
			date = DateUtil.getDate2999();
		}
		if (intOd != null && intOd.before(date)) {
			retval = true;
		}

		return retval;
	}

	/**
	 * Maji oba datumy stejny den?
	 */
	public static boolean isSameDay(Date date1, Date date2) {
		Calendar cal1 = getCalendarNoTime(date1);
		Calendar cal2 = getCalendarNoTime(date2);
		return cal1.getTimeInMillis() == cal2.getTimeInMillis();
	}

	/**
	 * Minimální datum
	 */
	public static Date minDate(Date dat1, Date dat2) {
		if (dat1.before(dat2)) {
			return dat1;
		}
		return dat2;
	}

	/**
	 * Maxmální datum
	 */
	public static Date maxDate(Date dat1, Date dat2) {
		if (dat1.after(dat2)) {
			return dat1;
		}
		return dat2;
	}

	/**
	 * @return prvni den v mesici
	 */
	public static Date getFirstDayOfMonth(Date value) {
		if (value == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(value);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return cal.getTime();
	}

	/**
	 * @return posledni den v mesici
	 */
	public static Date getLastDayOfMonth(Date value) {
		if (value == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(value);
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.DAY_OF_YEAR, -1);
		return cal.getTime();
	}

	/**
	 * @return prvni den v roce
	 */
	public static Date getFirstDayOfYear(Date value) {
		if (value == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(value);
		cal.set(Calendar.DAY_OF_YEAR, 1);
		return cal.getTime();
	}

	/**
	 * @return posledni den v roce
	 */
	public static Date getLastDayOfYear(Date value) {
		if (value == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(value);
		cal.set(Calendar.DAY_OF_YEAR, cal.getActualMaximum(java.util.Calendar.DAY_OF_YEAR));
		return cal.getTime();
	}

	/**
	 * Vrati stejny den, ale 0 hodin 0 minut 0 sekund 0 milis 
	 */
	public static Date getStartOfDay(Date value) {
		if (value == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(value);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	/**
	 * Vrati stejny stejny den, ale 23 hodin 59 minut 59 sekund 999 milis 
	 */
	public static Date getEndOfDay(Date value) {
		if (value == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(value);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		return cal.getTime();
	}

	/**
	 * Vrati datum presne za den vcetne hodiny, minuty, ...
	 */
	public static Date getNextDay(Date value) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(value);
		cal.add(Calendar.DAY_OF_YEAR, 1);
		return cal.getTime();
	}

	/**
	 * Vrati datum za den ale 0 hodin 0 minut 0 sekund 0 milis
	 */
	public static Date getNextDayNoTime(Date value) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(value);
		cal.add(Calendar.DAY_OF_YEAR, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static Date getDate(int day, int month, int year, int hour, int minute, int second,
			int millis) {
		Calendar c = Calendar.getInstance();
		c.setLenient(false);
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month - 1); // Calendar.MONTH is zero based !!!
		c.set(Calendar.DAY_OF_MONTH, day);
		if (hour == 24) {
			c.set(Calendar.HOUR_OF_DAY, 0);
		} else {
			c.set(Calendar.HOUR_OF_DAY, hour);
		}
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, second);
		c.set(Calendar.MILLISECOND, millis);
		return c.getTime();
	}

	/**
	 * Use month 1-12 !
	 */
	public static Date getDate(int day, int month, int year) {
		return getDate(day, month, year, 0, 0, 0, 0);
	}

	/**
	 * @return 365 or 366
	 */
	public static int getDaysInYear(Date value) {
		if (value == null) {
			return 0;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(value);
		return cal.getActualMaximum(java.util.Calendar.DAY_OF_YEAR);
	}

	/**
	 * @return 28 or 29 or 30 or 31
	 */
	public static int getDaysInMonth(Date value) {
		if (value == null) {
			return 0;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(value);
		return cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
	}

	/**
	 * @param month Negative means previous month. Zero is this month. 1 - January, 12 - December 
	 * @return first and last month datetime (01.02.2010 00.00.00.000 - 28.02.2010 23.59.59.999)
	 */
	public static Date[] getMonthRange(int month) {
		return getMonthRange(month, null);
	}

	/**
	 * From 01.MM.yyyy 00.00.00.000 to 28-31.MM.yyyy 23.59.59.999
	 * 
	 * @param year optional year
	 * @param month Negative means previous month. Zero is this month. 1 - January, 12 - December 
	 * @return first and last month datetime (01.02.2000 00.00.00.000 - 29.02.2000 23.59.59.999)
	 */
	public static Date[] getMonthRange(int month, Integer year) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		if (year != null) {
			calendar.set(Calendar.YEAR, year);
		}

		if (month <= 0) {
			calendar.add(Calendar.MONTH, month);
			month = calendar.get(Calendar.MONTH);
		}
		--month; //month is zero based in Calendar
		calendar.set(Calendar.MONTH, month);

		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

		calendar.add(Calendar.DAY_OF_YEAR, (dayOfMonth * -1) + 1);
		Date after = calendar.getTime();
		int add = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		calendar.add(Calendar.DAY_OF_YEAR, add);
		calendar.add(Calendar.MILLISECOND, -1);
		Date before = calendar.getTime();
		return new Date[] { after, before };
	}

	/**
	 * From Monday 00.00.00.000 to Sunday 23.59.59.999
	 * 
	 * @param week Negative means previous week. Zero is this week. 1 - 52 week of year 
	 * @return first and last week datetime (18.10.2010 00.00.00.000 - 24.10.2010 23.59.59.999)
	 */
	public static Date[] getWeekRange(int week) {
		return getWeekRange(week, null);
	}

	/**
	 * From Monday 00.00.00.000 to Sunday 23.59.59.999
	 * 
	 * @param week Negative means previous week. Zero is this week. 1 - 52 week of year 
	 * @return first and last week datetime (16.10.2000 00.00.00.000 - 22.10.2000 23.59.59.999)
	 */
	public static Date[] getWeekRange(int week, Integer year) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		if (year != null) {
			calendar.set(Calendar.YEAR, year);
		}
		if (week <= 0) {
			calendar.add(Calendar.WEEK_OF_YEAR, week);
			week = calendar.get(Calendar.WEEK_OF_YEAR);
		}
		calendar.set(Calendar.WEEK_OF_YEAR, week);

		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		//SUNDAY=1, MONDAY=2, ..., SATURDAY=7
		if (dayOfWeek == 1) {
			dayOfWeek = 7;
		} else {
			--dayOfWeek;
		}
		calendar.add(Calendar.DAY_OF_YEAR, (dayOfWeek * -1) + 1);
		Date after = calendar.getTime();
		int add = calendar.getActualMaximum(Calendar.DAY_OF_WEEK);
		calendar.add(Calendar.DAY_OF_YEAR, add);
		calendar.add(Calendar.MILLISECOND, -1);
		Date before = calendar.getTime();
		return new Date[] { after, before };
	}

	/**
	 * MaVa Tak tohle nechapu

	public static Date getDateWithHourAndMinutes(Date date, Date hourAndMinutes) {
		if (date == null || hourAndMinutes == null) {
			return null;
		}
		Calendar cHour = Calendar.getInstance();
		cHour.setTime(hourAndMinutes);
		Calendar cDate = getCalendarByDate(date);
		cDate.set(Calendar.HOUR_OF_DAY, cHour.get(Calendar.HOUR_OF_DAY));
		cDate.set(Calendar.MINUTE, cHour.get(Calendar.MINUTE));
		return cDate.getTime();
	}
	*/

	/**
	 * MaVa Tak tohle nechapu
	 * 
	 * Doplni aktualni cas do datumu na vstupu, a to v pripade, 
	 * ze cas neni vyplnen (00:00:00) a ze se jedna o datum k 
	 * dnesnimu dni  

	public static Date addTimeIfNeeded(Date input) {
		// presne tohle je dnesni datum bez casu
		Calendar today = GregorianCalendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);

		// mame tedy dnesni datum bez casu?
		if (today.getTimeInMillis() == input.getTime()) {
			return new Date();
		}
		// nechame puvodni
		return input;
	}
	 */
}