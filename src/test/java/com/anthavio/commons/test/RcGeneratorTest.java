package com.anthavio.commons.test;

import static org.fest.assertions.Assertions.assertThat;

import org.testng.annotations.Test;

import com.anthavio.util.RcGenerator;
import com.anthavio.util.RcGenerator.RcType;


/**
 * @author vanek
 *
 */
public class RcGeneratorTest {

	@Test
	public void generateCommonRc() {
		//		validateCommonRc("040910/0011");
		for (int i = 0; i < 10000; i++) {
			String rc = RcGenerator.generateRcForAge(0, 10, RcType.COMMON);
			validateCommonRc(rc);
		}
	}

	@Test
	public void generateOfficialRc() {
		for (int i = 0; i < 10000; i++) {
			String rc = RcGenerator.generateRcForAge(0, 10);
			validateOfficialRc(rc);
		}
	}

	@Test
	public void generateProblematicRc() {
		for (int i = 0; i < 10000; i++) {
			String rc = RcGenerator.generateRcForAge(0, 10, RcType.NO_MOD_11);
			validateNoMod11Rc(rc);
		}
	}

	private void validateNoMod11Rc(String rc) {
		assertThat(rc.length()).isEqualTo(11).overridingErrorMessage("Invalid RC length: " + rc);
		int checkNumber = Integer.parseInt(rc.substring(0, 6) + rc.substring(7, 10));
		int crcNumber = Integer.valueOf(rc.substring(10));
		assertThat(crcNumber).isEqualTo(0).overridingErrorMessage("Invalid checksum last digit for special RC: " + rc);
		assertThat(checkNumber % 11).isEqualTo(10).overridingErrorMessage("Invalid checksum for special RC (modulo == 10): " + rc);
	}

	private void validateOfficialRc(String rc) {
		assertThat(rc.length()).isEqualTo(11).overridingErrorMessage("Invalid RC length: " + rc);
		int checkNumber = Integer.parseInt(rc.substring(0, 6) + rc.substring(7, 10));
		int crcNumber = Integer.valueOf(rc.substring(10));
		if (checkNumber % 11 == 10) {
			assertThat(crcNumber).isEqualTo(0).overridingErrorMessage("Invalid checksum for special RC (modulo == 10): " + rc);
		} else {
			assertThat(checkNumber % 11).isEqualTo(crcNumber).overridingErrorMessage("Invalid checksum for RC: " + rc);
		}
	}

	private void validateCommonRc(String rc) {
		assertThat(rc.length()).isEqualTo(11).overridingErrorMessage("Invalid RC length: " + rc);
		int suma = Integer.parseInt(rc.substring(0, 2)) + Integer.parseInt(rc.substring(2, 4))
				+ Integer.parseInt(rc.substring(4, 6)) + Integer.parseInt(rc.substring(7, 9))
				+ Integer.parseInt(rc.substring(9, 11));
		assertThat(suma % 11).isEqualTo(0).overridingErrorMessage("Invalid checksum for RC: " + rc);
	}
}
