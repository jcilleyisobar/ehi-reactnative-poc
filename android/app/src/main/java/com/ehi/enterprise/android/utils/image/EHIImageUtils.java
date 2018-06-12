package com.ehi.enterprise.android.utils.image;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.util.DisplayMetrics;

import com.ehi.enterprise.android.models.location.EHIImage;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public class EHIImageUtils {
	@Retention(RetentionPolicy.SOURCE)
	@StringDef({IMAGE_QUALITY_LOW, IMAGE_QUALITY_MEDIUM, IMAGE_QUALITY_HIGH})
	public @interface ImageQuality {
	}

	private static final String IMAGE_QUALITY_LOW = "low";
	private static final String IMAGE_QUALITY_MEDIUM = "medium";
	private static final String IMAGE_QUALITY_HIGH = "high";

	@Retention(RetentionPolicy.SOURCE)
	@StringDef({IMAGE_WIDTH_480, IMAGE_WIDTH_640, IMAGE_WIDTH_768})
	public @interface ImageWidth {
	}

	private static final String IMAGE_WIDTH_480 = "480";
	private static final String IMAGE_WIDTH_640 = "640";
	private static final String IMAGE_WIDTH_768 = "768";

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({IMAGE_TYPE_SIDE_PROFILE, IMAGE_TYPE_THREE_QUARTER})
	public @interface ImageType {
	}

	public static final int IMAGE_TYPE_SIDE_PROFILE = 0;
	public static final int IMAGE_TYPE_THREE_QUARTER = 1;

	private static final double XHDPI = 2.0;
	private static final double XXHDPI = 3.0;

	public static String getCarClassImageUrl(@NonNull List<EHIImage> images,
	                                         @ImageType int imageType, int containerWidth) {
		if (images == null
				|| images.size() == 0
				|| images.size() <= imageType) {
			return "";
		}
		EHIImage image = images.get(imageType);
		String imagePath = image.getPath();
		String imageWidth = getImageWidthForContainer(image, containerWidth);
		String imageQuality = chooseImage(image);
		String url = imagePath.replace("{width}", imageWidth);
		url = url.replace("{quality}", imageQuality);
		return url;
	}

	public static String getCarClassImageForWear (@NonNull String path, int width, String quality) {
		String url = path.replace("{width}", String.valueOf(width));
		url = url.replace("{quality}", quality);
		return url;
	}

	private static String getImageWidthForContainer(EHIImage image, int containerWidth) {
		int selectedImageWidth = containerWidth;
		int previousDifference = Integer.MAX_VALUE;
		boolean imageWidthFound = false;

		for (String s : image.getSupportedWidth()) {
			final int parsedInt = Integer.parseInt(s);
			int difference = Math.abs(parsedInt - selectedImageWidth);

			if (difference < previousDifference) {
				if (parsedInt > containerWidth) {
					selectedImageWidth = parsedInt;
					imageWidthFound = true;
					break;
				}
			}
		}

		if (!imageWidthFound) {
			selectedImageWidth = Integer.parseInt(image.getSupportedWidth().get(image.getSupportedWidth().size() - 1));
		}

		return String.valueOf(selectedImageWidth);
	}

	private static @ImageQuality String chooseImage(EHIImage image) {
		if (image.getSupportedQualities().contains(IMAGE_QUALITY_HIGH)) {
			return IMAGE_QUALITY_HIGH;
		}
		else if (image.getSupportedQualities().contains(IMAGE_QUALITY_MEDIUM)) {
			return IMAGE_QUALITY_MEDIUM;
		}
		else if (image.getSupportedQualities().contains(IMAGE_QUALITY_LOW)) {
			return IMAGE_QUALITY_LOW;
		}
		else {
			return IMAGE_QUALITY_LOW;
		}
	}

	/*
	*   0.75 - ldpi
	*	1.0 - mdpi
	*	1.5 - hdpi
	*	2.0 - xhdpi
	*	3.0 - xxhdpi
	*	4.0 - xxxhdpi
	*/
	private static String getDeviceDensity(Context context) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		Float mDensity = metrics.density;
		if (mDensity > XXHDPI) {
			//xlarge
			return "xlarge";
		}
		else if (mDensity <= XXHDPI && mDensity > XHDPI) {
			//large
			return "large";
		}
		else {
			//small
			return "small";
		}
	}
}
