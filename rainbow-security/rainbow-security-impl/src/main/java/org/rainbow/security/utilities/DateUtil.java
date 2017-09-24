package org.rainbow.security.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	public static Date toDate(String isoDateString) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return df.parse(isoDateString);
		} catch (ParseException ex) {
			throw new RuntimeException(ex);
		}
	}
}
