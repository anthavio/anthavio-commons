package net.anthavio.commons.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import net.anthavio.util.DateUtil;

import org.assertj.core.api.Assertions;
import org.junit.Test;

/**
 * @author vanek
 * 
 */
public class DateUtilTest {

	public Date leapFebruaryDate = DateUtil.getDate(1, 2, 2000);
	public Date leapFirstDate = DateUtil.getDate(1, 1, 2000);
	public Date leapLastDate = DateUtil.getEndOfDay(DateUtil.getDate(31, 12, 2000));

	public Date simpleFebruaryDate = DateUtil.getDate(1, 2, 2010);
	public Date simpleFirstDate = DateUtil.getDate(1, 1, 2010);
	public Date simpleLastDate = DateUtil.getEndOfDay(DateUtil.getDate(31, 12, 2010));

	@Test
	public void testDiffCount() {
		Date date1 = simpleFirstDate;
		Date date2 = simpleLastDate;
		int diff = DateUtil.getDaysDiff(date1, date2);
		assertThat(diff).isEqualTo(364).overridingErrorMessage(
				"Diff between last day and first day in nonleaping year is 364");

		diff = DateUtil.getDaysDiff(leapFirstDate, leapLastDate);
		assertThat(diff).isEqualTo(365)
				.overridingErrorMessage("Diff between last day and first day in leaping year is 365");

		date1 = DateUtil.getActualDateNoTime();
		date2 = DateUtil.addHours(date1, 1);
		diff = DateUtil.getHoursDiff(date1, date2);
		assertThat(diff).isEqualTo(1).overridingErrorMessage("addHours 1 should be 1 hour diff");
		date2 = DateUtil.addSeconds(date2, -1);// minus second
		diff = DateUtil.getHoursDiff(date1, date2);
		// 59 minut a 59 sekund neni hodina
		assertThat(diff).isEqualTo(0).overridingErrorMessage("addHours 1 addSeconds -1 should be same hour");

		// addDays

		date1 = DateUtil.getActualDateNoTime();
		date2 = DateUtil.addDays(date1, 1);
		diff = DateUtil.getDaysDiff(date1, date2);
		assertThat(diff).isEqualTo(1).overridingErrorMessage("addDays 1 should be 1 day diff");
		diff = DateUtil.getHoursDiff(date1, date2);
		assertThat(diff).isEqualTo(24).overridingErrorMessage("addDays 1 should be 24 hours diff");

		date2 = DateUtil.addSeconds(date2, -1); // minus second
		diff = DateUtil.getDaysDiff(date1, date2);
		// this is valid only if date1 is StartOfDay
		assertThat(diff).isEqualTo(0).overridingErrorMessage("addDays 1 addSeconds -1 should be same day");
		diff = DateUtil.getHoursDiff(date1, date2);
		assertThat(diff).isEqualTo(23).overridingErrorMessage("addDays 1 addSeconds -1 should be 23 hours diff");

		// addMonths

		date1 = DateUtil.getActualDateNoTime();
		date2 = DateUtil.addMonths(date1, 1);
		diff = DateUtil.getMonthsDiff(date1, date2);
		// FIXME nefunguje pro date1 = 31.10.2011
		assertThat(diff).isEqualTo(1).overridingErrorMessage("addMonths 1 should be 1 month diff");
		date2 = DateUtil.addSeconds(date2, -1); // minus second
		diff = DateUtil.getMonthsDiff(date1, date2);
		assertThat(diff).isEqualTo(0).overridingErrorMessage("addMonths 1 addSeconds -1 should be 0 month diff");

		date1 = DateUtil.getEndOfDay(new Date());
		date2 = DateUtil.addSeconds(date1, 1);
		diff = DateUtil.getDaysDiff(date1, date2);
		assertThat(diff).isEqualTo(1).overridingErrorMessage("EndOfDay addSeconds 1 should be next day");

		date1 = DateUtil.getStartOfDay(new Date());
		date2 = DateUtil.addSeconds(date1, -1);
		diff = DateUtil.getDaysDiff(date1, date2);
		assertThat(diff).isEqualTo(1).overridingErrorMessage("StartOfDay addSeconds -1 should be next day");

		// addYear tests
		date1 = simpleFirstDate;
		date2 = DateUtil.addYears(date1, 1);
		diff = DateUtil.getYearsDiff(date1, date2);
		assertThat(diff).isEqualTo(1).overridingErrorMessage("addYears 1 nonleaping should be 1 year diff");
		diff = DateUtil.getMonthsDiff(date1, date2);
		assertThat(diff).isEqualTo(12).overridingErrorMessage("addYears 1 nonleaping should be 12 months diff");
		diff = DateUtil.getDaysDiff(date1, date2);
		assertThat(diff).isEqualTo(365).overridingErrorMessage("addYears 1 nonleaping should be 365 days diff");

		date2 = DateUtil.addSeconds(date2, -1);// minus second
		diff = DateUtil.getYearsDiff(date1, date2);
		assertThat(diff).isEqualTo(0).overridingErrorMessage("addYears 1 addSeconds -1 nonleaping should be 0 year diff");
		diff = DateUtil.getMonthsDiff(date1, date2);
		assertThat(diff).isEqualTo(11).overridingErrorMessage(
				"addYears 1 addSeconds -1 nonleaping should be 11 months diff");
		diff = DateUtil.getDaysDiff(date1, date2);
		assertThat(diff).isEqualTo(364).overridingErrorMessage(
				"addYears 1 addSeconds -1 nonleaping should be 364 days diff");

		date1 = simpleFirstDate;
		date2 = simpleLastDate;
		diff = DateUtil.getPersonAge(date1, date2);
		assertThat(diff).isEqualTo(0).overridingErrorMessage("PersonAge in year of birth should be 0");

		date2 = DateUtil.addSeconds(date2, 1);
		diff = DateUtil.getPersonAge(date1, date2);
		assertThat(diff).isEqualTo(1).overridingErrorMessage("PersonAge of year addSeconds 1 birth should be 1");

		date1 = DateUtil.getDate(5, 5, 1920);
		date2 = DateUtil.getDate(4, 5, 1921);
		diff = DateUtil.getPersonAge(date1, date2);
		assertThat(diff).isEqualTo(0).overridingErrorMessage("PersonAge less then year should be 0");

	}

	@Test
	public void testDaysInCount() {
		// january
		Date date = DateUtil.getDate(1, 1, 2010);
		int count = DateUtil.getDaysInMonth(date);
		assertThat(count).isEqualTo(31);
		count = DateUtil.getDaysInYear(date);
		assertThat(count).isEqualTo(365);

		// february
		date = DateUtil.getDate(1, 2, 2010);
		count = DateUtil.getDaysInMonth(date);
		assertThat(count).isEqualTo(28);
		count = DateUtil.getDaysInYear(date);
		assertThat(count).isEqualTo(365);

		// april
		date = DateUtil.getDate(30, 4, 2010);
		count = DateUtil.getDaysInMonth(date);
		assertThat(count).isEqualTo(30);
		count = DateUtil.getDaysInYear(date);
		assertThat(count).isEqualTo(365);

		// leaping year february
		count = DateUtil.getDaysInMonth(leapFebruaryDate);
		assertThat(count).isEqualTo(29);
		count = DateUtil.getDaysInYear(leapFebruaryDate);
		assertThat(count).isEqualTo(366);
	}

	@Test
	public void testFormat() {
		String format = DateUtil.format(leapLastDate, DateUtil.D_M_YYYY_HH_MM_SS_SSS);
		assertThat(format).isEqualTo("31.12.2000 23:59:59,999");

		String xsdDateTime = DateUtil.printXsdDateTime(leapFebruaryDate);
		Date dateTime = DateUtil.parseXsdDateTime(xsdDateTime);
		assertThat(leapFebruaryDate).isEqualTo(dateTime);

		try {
			DateUtil.parse("Nesmyslne datum", "Nesmyslny pattern");
			Assertions.fail("Parsovani chybnym paternem nesmi projit");
		} catch (IllegalArgumentException iax) {

		}

		try {
			DateUtil.parse("Nesmyslne datum", DateUtil.D_M_YYYY_HH_MM_SS_SSS);
			Assertions.fail("Parsovani chybnym datem nesmi projit");
		} catch (IllegalArgumentException iax) {

		}

		try {
			Date date = DateUtil.parseQuietly("1.1.1978", "Nesmyslny pattern");
			Assertions.fail("Parsovani chybnym paternem nesmi projit");
		} catch (IllegalArgumentException iax) {

		}
		Date date = DateUtil.parseQuietly("1x1x1978", DateUtil.D_M_YYYY);
		assertThat(date).isNull();

		date = DateUtil.parseQuietly("1.1.1978", DateUtil.D_M_YYYY);
		assertThat(date).isNotNull();
	}

	@Test
	public void testIntervals() {
		boolean inInterval = DateUtil.isInInterval(leapFirstDate, leapLastDate, leapFebruaryDate);
		assertThat(inInterval).isEqualTo(true);
		inInterval = DateUtil.isInInterval(leapFirstDate, leapLastDate, leapLastDate);
		assertThat(inInterval).isEqualTo(true);
		inInterval = DateUtil.isInInterval(leapFirstDate, leapLastDate, leapFirstDate);
		assertThat(inInterval).isEqualTo(true);

		inInterval = DateUtil.isInInterval(null, null, null);
		assertThat(inInterval).isEqualTo(true);

		inInterval = DateUtil.isInInterval(leapFirstDate, null, null);
		assertThat(inInterval).isEqualTo(true);

		inInterval = DateUtil.isInInterval(leapFirstDate, null, leapFebruaryDate);
		assertThat(inInterval).isEqualTo(true);

		inInterval = DateUtil.isInInterval(leapFirstDate, leapFebruaryDate, leapLastDate);
		assertThat(inInterval).isEqualTo(false).overridingErrorMessage("LastDate should be after interval");
		inInterval = DateUtil.isInInterval(leapFebruaryDate, leapLastDate, leapFirstDate);
		assertThat(inInterval).isEqualTo(false).overridingErrorMessage("FirstDate should be before interval");

		inInterval = DateUtil.isInInterval(leapFirstDate, leapLastDate, null);
		assertThat(inInterval).isEqualTo(false);

		// isAfter
		inInterval = DateUtil.isIntervalAfterDate(leapFebruaryDate, leapLastDate, leapFirstDate);
		assertThat(inInterval).isEqualTo(true);

		inInterval = DateUtil.isIntervalAfterDate(leapLastDate, null, leapFirstDate);
		assertThat(inInterval).isEqualTo(true);

		inInterval = DateUtil.isIntervalAfterDate(leapFirstDate, leapLastDate, leapFirstDate);
		assertThat(inInterval).isEqualTo(false);

		inInterval = DateUtil.isIntervalAfterDate(leapFirstDate, leapLastDate, null);
		assertThat(inInterval).isEqualTo(false);

		inInterval = DateUtil.isIntervalAfterDate(leapFirstDate, null, leapLastDate);
		assertThat(inInterval).isEqualTo(false);

		inInterval = DateUtil.isIntervalAfterDate(null, null, leapFirstDate);
		assertThat(inInterval).isEqualTo(false);

	}
}
