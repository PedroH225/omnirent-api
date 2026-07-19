package br.com.omnirent.common.formatter;

import java.time.Duration;

public final class DurationMessageUtil {

	private static final String TIME_UNIT_PREFIX =  "time.unit.";
	
	private DurationMessageUtil() {}
	
	public static DurationMessage resolveMessage(Duration duration) {
	    long hours = duration.toHours();
	    if (hours > 0) {
	        return new DurationMessage(hours, hours == 1 
	        		? TIME_UNIT_PREFIX + "hour" : TIME_UNIT_PREFIX + "hours");
	    }

	    long minutes = duration.toMinutes();
	    if (minutes > 0) {
	        return new DurationMessage(
	        		minutes, minutes == 1 
	        		? TIME_UNIT_PREFIX + "minute" : TIME_UNIT_PREFIX + "minutes");
	    }
	    return new DurationMessage(duration.toSeconds(), TIME_UNIT_PREFIX + "seconds");
	}
}
