package io.dwak.reactor;

/**
 * Controls the verbosity of logging
 */
public enum LogLevel {
    /**
     * No logging
     */
    NONE(0),

    /**
     * Only log stack traces for error or for dependency change traces
     */
    DEBUG(1),

    /**
     * Only log when a computation is in compute
     */
    COMPUTE(2),

    /**
     * Log all computation actions
     */
    ALL(3);

    public final int logLevelValue;

    LogLevel(int i) {
        logLevelValue = i;
    }
}
