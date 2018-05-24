package com.ehi.enterprise.android.ui.location.interfaces;

import android.view.MotionEvent;

public interface IMapDragDelegate {

	void onMapStartDrag();

	void onMapStopDrag();

	void onMarkerClick();

	void onMapTouch(MotionEvent e);
}
