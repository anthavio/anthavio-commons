/**
 * 
 */
package net.anthavio.commons.test;

import static org.fest.assertions.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.anthavio.log.ToString;
import net.anthavio.log.ToStringBuilder;
import net.anthavio.util.HibernateHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * @author vanek
 *
 */
public class ToStringTest {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@BeforeClass
	public void beforeTest() {
		HibernateHelper.isHibernatePresent();
		EventStoringAppender.getEvents().clear();
	}

	@Test
	public void testKmxToStingBuilder() {
		JaxbTestClass obj1 = buildJaxbClass();
		EventStoringAppender.getEvents().clear();
		log.debug("\n" + obj1);
		ILoggingEvent enterEvent = EventStoringAppender.getEvents().get(0);
		assertThat(enterEvent.getMessage()).contains("list=[Brekeke, Kvakvakva]");
		assertThat(enterEvent.getMessage()).contains("cisla=[1, 1.1, 2, 3]");
		assertThat(enterEvent.getMessage()).contains("map={Cosi=Kdesi}");
		assertThat(enterEvent.getMessage()).contains("array={Prase,Ovce,Godzilla}");
		assertThat(enterEvent.getMessage()).contains("rekurze=JaxbTestClass[1]");
		assertThat(enterEvent.getMessage()).doesNotContain("hashed=tajny text");
		assertThat(enterEvent.getMessage()).doesNotContain("nelogovat=");

	}

	/**
	 * Pozor, ty hodnoty slouzi i k testu KmxToStingBuilder!
	 */
	private JaxbTestClass buildJaxbClass() {
		JaxbTestClass obj1 = new JaxbTestClass();
		obj1.map = new HashMap<String, String>();
		obj1.map.put("Cosi", "Kdesi");
		obj1.list = new ArrayList<String>();
		obj1.list.add("Brekeke");
		obj1.list.add("Kvakvakva");

		obj1.cisla = new ArrayList<Number>();
		obj1.cisla.add(new Integer(1));
		obj1.cisla.add(new Double(1.1));
		obj1.cisla.add(new BigInteger("2"));
		obj1.cisla.add(new BigDecimal(3));

		obj1.array = new String[] { "Prase", "Ovce", "Godzilla" };

		obj1.hashed = "tajny text";
		obj1.nelogovat = "nevypisovany text";

		obj1.rekurze = new JaxbTestClass[] { new JaxbTestClass() };
		return obj1;
	}

	static class JaxbTestClass {

		@ToString(detail = true)
		private List<String> list = new ArrayList<String>();

		@ToString(detail = true)
		private List<Number> cisla;

		@ToString(detail = true)
		private Map<String, String> map = new HashMap<String, String>();

		@ToString(detail = true)
		private String[] array;

		private JaxbTestClass[] rekurze;

		@ToString(hide = true)
		private String nelogovat;

		@ToString(hash = true)
		private String hashed;

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
			//return ReflectionToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
		}

	}
}


