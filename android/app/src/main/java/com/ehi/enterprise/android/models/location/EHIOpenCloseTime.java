package com.ehi.enterprise.android.models.location;

import com.ehi.enterprise.android.models.EHIModel;
import com.ehi.enterprise.android.models.location.solr.EHISolrTimeSpan;
import com.ehi.enterprise.android.utils.DLog;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EHIOpenCloseTime extends EHIModel {

	private static final String TAG = EHIOpenCloseTime.class.getSimpleName();

	private static final SimpleDateFormat sTimeFormat = new SimpleDateFormat("HH:mm");

	@SerializedName("open_time")
	private String mOpenTime;

	@SerializedName("close_time")
	private String mCloseTime;

	public EHIOpenCloseTime(EHISolrTimeSpan span) {
		mOpenTime = span.getOpenTime();
		mCloseTime = span.getCloseTime();
	}

	public String getOpenTime() {
		return mOpenTime;
	}

	public String getPreparedOpenTime() {
		if (getOpenTime() != null &&
				mOpenTime.length() < 4) {
			String time = new String(mOpenTime);
			do {
				time = "0" + time;
			} while (time.length() < 4);
			return time;
		}
		return mOpenTime;
	}

	public Date getOpenTimeObject() throws ParseException {
		return sTimeFormat.parse(getPreparedOpenTime());
	}

	public String getCloseTime() {
		return mCloseTime;
	}

	public String getPreparedCloseTime() {
		if (getCloseTime() != null &&
				mCloseTime.length() < 4) {
			String time = new String(mCloseTime);
			do {
				time = "0" + time;
			} while (time.length() < 4);
			return time;
		}
		return mCloseTime;
	}

	public Date getCloseTimeObject() throws ParseException {
		return sTimeFormat.parse(getPreparedCloseTime());
	}

	public boolean contains(Date date) {
		try {
			Date opening = getOpenTimeObject();
			Date closing = getCloseTimeObject();

            return (date.getTime() == opening.getTime() || date.after(opening))
                    && date.getTime() == closing.getTime() || date.before(closing);
		} catch (ParseException e) {
			DLog.e(TAG, "", e);
		}
		return false;
	}


}
