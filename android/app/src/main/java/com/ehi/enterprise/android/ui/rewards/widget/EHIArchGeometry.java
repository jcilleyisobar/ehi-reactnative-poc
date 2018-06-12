package com.ehi.enterprise.android.ui.rewards.widget;

import android.graphics.Point;

public class EHIArchGeometry {
    private Point mCenterPoint;
    private double mRadius;
    private int mNumberOfSegments;

    public EHIArchGeometry(Point centerPoint, double radius, int numberOfFragments) {
        this.mCenterPoint = centerPoint;
        this.mRadius = radius;
        this.mNumberOfSegments = numberOfFragments;
    }

    public Point[] getArchPoints() {
        Point[] pointsArray = new Point[mNumberOfSegments + 1];
        for (int i = 0; i < pointsArray.length; i++) {
            final double angle = getSectionAngle(i) * (Math.PI/180);
            final double opposite = getOpposite(angle, mRadius);
            double adjacent = getAdjacent(angle, mRadius);
            pointsArray[i] = new Point((int)(adjacent + mCenterPoint.x), (int)(mCenterPoint.y - opposite));
        }
        return pointsArray;
    }

    public float getSectionAngle(int segmentNumber) {
        return (float)((180 / mNumberOfSegments) * (segmentNumber));
    }

    private double getOpposite(double angle, double hypotenuse) {
        return Math.sin(angle) * hypotenuse;
    }

    private double getAdjacent(double angle, double hypotenuse) {
        return Math.cos(angle) * hypotenuse;
    }

}
