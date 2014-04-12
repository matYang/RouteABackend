package carpool.HttpServer.commonTest;

import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.Test;

import carpool.HttpServer.common.DateUtility;
import carpool.HttpServer.configurations.EnumConfig.DayTimeSlot;

public class DateUtilityTest {

	@Test
	public void test() {
		Calendar cal = DateUtility.getCurTimeInstance();
		assertTrue(DayTimeSlot.n23.isHourAfter(cal));
		assertFalse(DayTimeSlot.n0.isHourAfter(cal));
	}

}
