package io.dwak.reactorbinding;

public class Preconditions {
    public static boolean checkNotNull(Object... objects) {
        for (Object object : objects) {
            if (object == null) {
                return false;
            }
        }

        return true;
    }
}
