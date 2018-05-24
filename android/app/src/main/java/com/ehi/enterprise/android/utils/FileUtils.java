package com.ehi.enterprise.android.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtils {

	private static final String TAG = FileUtils.class.getSimpleName();

	public static String readFromfile(Context context, String fileName) {
		StringBuilder returnString = new StringBuilder();
		InputStream fIn = null;
		InputStreamReader isr = null;
		BufferedReader input = null;
		try {
			fIn = context.getResources().getAssets()
					.open(fileName);
			isr = new InputStreamReader(fIn, "UTF-8");
			input = new BufferedReader(isr);
			String line = "";
			while ((line = input.readLine()) != null) {
				returnString.append(line);
			}
		} catch (Exception e) {
			DLog.w(TAG, e);
		} finally {
			try {
				if (isr != null)
					isr.close();
				if (fIn != null)
					fIn.close();
				if (input != null)
					input.close();
			} catch (Exception e2) {
				e2.getMessage();
			}
		}
		return returnString.toString();
	}
}
