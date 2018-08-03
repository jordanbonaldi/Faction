package net.neferett.linaris.faction.utils;

import java.util.function.Predicate;

public class TimeUtils {

	public static Predicate<Long> CreateTestCoolDown(final long time) {
		return past -> getTimeLeft(past, time) > 0;
	}

	public static Long getTimeLeft(final Long past, final long time) {
		return past / 1000 + time - System.currentTimeMillis() / 1000;
	}
}
