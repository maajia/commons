package de.galan.commons.time;

import java.util.Date;
import java.util.function.Supplier;


/**
 * Provides the current Date
 *
 * @author daniel
 */
public class NowDateSupplier implements Supplier<Date> {

	@Override
	public Date get() {
		return new Date();
	}

}
