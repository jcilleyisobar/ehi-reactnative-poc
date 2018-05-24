package com.ehi.enterprise.android.models.location.solr;

import com.ehi.enterprise.android.models.EHIModel;
import com.ehi.enterprise.android.utils.DLog;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EHISolrTimeSpan extends EHIModel {

	private static final String TAG = EHISolrTimeSpan.class.getSimpleName();

	private static final SimpleDateFormat sTimeFormat = new SimpleDateFormat("HH:mm");

	@SerializedName("open")
	private String mOpenTime;

	@SerializedName("close")
	private String mCloseTime;

	public Date getOpenTimeDate() throws ParseException {
		return sTimeFormat.parse(mOpenTime);
	}

	public Date getCloseTimeDate() throws ParseException {
		return sTimeFormat.parse(mCloseTime);
	}

	public String getOpenTime() {
		return mOpenTime;
	}

	public String getCloseTime() {
		return mCloseTime;
	}

	public boolean contains(Date time) {
		try {
			Date opening = getOpenTimeDate();
			Date closing = getCloseTimeDate();

            return (time.getTime() == opening.getTime() || time.after(opening))
                    && (time.getTime() == closing.getTime() || time.before(closing));
		} catch (ParseException e) {
			DLog.e(TAG, "", e);
		}
		return false;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof EHISolrTimeSpan)) return false;

		EHISolrTimeSpan that = (EHISolrTimeSpan) o;

		if (mOpenTime != null ? !mOpenTime.equals(that.mOpenTime) : that.mOpenTime != null)
			return false;
		return !(mCloseTime != null ? !mCloseTime.equals(that.mCloseTime) : that.mCloseTime != null);

	}

	@Override
	public int hashCode() {
		int result = mOpenTime != null ? mOpenTime.hashCode() : 0;
		result = 31 * result + (mCloseTime != null ? mCloseTime.hashCode() : 0);
		return result;
	}
}
