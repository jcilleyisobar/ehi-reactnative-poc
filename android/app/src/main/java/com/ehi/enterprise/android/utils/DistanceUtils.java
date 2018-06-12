package com.ehi.enterprise.android.utils;

import android.content.Context;
import android.location.Location;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.utils.manager.LocalDataManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;


public class DistanceUtils {

	//list of countries which use Imperial units system
	private static final String US = "US";
	private static final String LR = "LR";
	private static final String MM = "MM";
	private static final String GB = "GB";

	private static final double MILES_FACTOR = 0.62137119223733;

	public static String getFormattedDistanceToLocation(Context context, Location from, Location to) {
		return getFormattedDistanceToLocation(from,
				to,
				context.getString(R.string.metrics_miles),
				context.getString(R.string.metrics_kilometers));
	}

	public static double round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}


	public static String getFormattedDistanceToLocation(Location from,
	                                                    Location to,
	                                                    String imperialPrefix,
	                                                    String metricPrefix) {
		if (from == null
				|| to == null
				|| imperialPrefix == null
				|| metricPrefix == null) {
			return "";
		}
		double distanceInMeters = from.distanceTo(to);
		String locale = LocalDataManager.getInstance().getPreferredCountryCode();
		if (locale.equals(US)
				|| locale.equals(LR)
				|| locale.equals(MM)
				|| locale.equals(GB)) {
			//imperial
			return getDistanceDecimalFormat().format(metersToMiles(distanceInMeters)) + " " + imperialPrefix;
		}
		else {
			//metric
			return getDistanceDecimalFormat().format(metersToKilometers(distanceInMeters)) + " " + metricPrefix;
		}

	}

	public static double metersToKilometers(double meters) {
		return meters / 1000;
	}

	public static double metersToMiles(double meters) {
		return meters * MILES_FACTOR / 1000;
	}

	public static double killometersToMiles(double kilometers) {
		return kilometers * MILES_FACTOR;
	}

	private static DecimalFormat getDistanceDecimalFormat() {
		DecimalFormat df = new DecimalFormat();
		df.setMinimumFractionDigits(0);
		df.setMaximumFractionDigits(1);
		df.setMinimumIntegerDigits(1);
		df.setMaximumIntegerDigits(5);
		return df;
	}
}

