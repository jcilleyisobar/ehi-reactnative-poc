package io.dwak.reactor;

import android.util.Log;

/**
 * Helper class for logging
 */
final class Lumberjack {
    static void log(final String tag, final String message, final LogLevel level) {
        if (Reactor.getInstance().shouldLog()) {
            if (Reactor.getInstance().getLogLevel().logLevelValue >= level.logLevelValue) {
                if (Reactor.getInstance().getLog() != null) {
                    Reactor.getInstance().getLog().log(message);
                }
                else {
                    Log.d(tag, message);
                }
            }
        }
    }

    static void logError(final String tag, final String message, final Throwable error){
        if(Reactor.getInstance().shouldLog()){
            Log.e(tag, message, error);
        }
    }

}
