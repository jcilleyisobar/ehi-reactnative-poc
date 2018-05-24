package com.ehi.enterprise.android.utils.manager;


import android.os.Build;

import org.junit.Test;

import static com.ehi.enterprise.helpers.StaticFieldsMockHelper.setFinalStatic;
import static org.junit.Assert.assertEquals;


public class DateUtilManagerTest {

    private DateUtilManager subject;
    private final String input = "••••-••-05";

    @Test
    public void shouldFormatAmericanStyle() throws Exception {
        subject = new DateUtilManager();
        assertEquals("••/05/••••", subject.replaceChars(input, "MM/d/yyyy"));
    }

    @Test
    public void shouldFormatEuropeanStyle() throws Exception {
        subject = new DateUtilManager();
        assertEquals("05/••/••••", subject.replaceChars(input, "d/MM/yyyy"));
    }

    @Test
    public void shouldFormatGermanStyle() throws Exception {
        subject = new DateUtilManager();
        assertEquals("05.••.••••", subject.replaceChars(input, "d.MM.yyyy"));
    }

    @Test
    public void shouldNotFormatWhenSdkBelowLevel18() throws Exception {
        setFinalStatic(Build.VERSION.class.getField("SDK_INT"), 17);
        subject = new DateUtilManager();
        assertEquals(input, subject.formatMaskedDate(input));
    }

}
