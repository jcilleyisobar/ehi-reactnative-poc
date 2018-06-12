//
//  EHILogging.h
//  Enterprise
//
//  Created by Ty Cobb on 1/13/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import <CocoaLumberjack/DDLog.h>

typedef NS_OPTIONS(NSInteger, EHILogDomain) {
    EHILogDomainGeneral   = 1 << 31,
    EHILogDomainNetwork   = 1 << 30,
    EHILogDomainModels    = 1 << 29,
    EHILogDomainFiles     = 1 << 28,
    EHILogDomainAnalytics = 1 << 27,
    EHILogDomainMemory    = 1 << 26,
};

// define the active log level/domains
#define EHILogDomains (EHILogDomainGeneral | EHILogDomainNetwork)

#ifndef DEBUG
    #define EHILogLevel LOG_LEVEL_OFF
#elif TESTS
    #define EHILogLevel (LOG_LEVEL_ERROR | EHILogDomains)
#else
    #define EHILogLevel (LOG_LEVEL_DEBUG | EHILogDomains)
#endif

// redefine CocoaLumberjack's LOG_MAYBE to check the flag correctly
#undef  LOG_MAYBE
#define LOG_MAYBE(async, lvl, flg, ctx, fnct, frmt, ...) \
        do { if(((lvl) & (flg)) == (flg) && !TARGET_OS_WATCH) LOG_MACRO(async, lvl, flg, ctx, nil, fnct, frmt, ##__VA_ARGS__); } while(0)

// define domain-based logging macros
#define EHIDomainLogForLevel(_level, _domain, _format, ...) \
        LOG_OBJC_MAYBE(LOG_ASYNC_ ## _level, EHILogLevel, LOG_FLAG_ ## _level | _domain, 0, _format, ##__VA_ARGS__)

#define EHIDomainError(_domain, _format, ...)   EHIDomainLogForLevel(ERROR,   _domain, _format, ##__VA_ARGS__)
#define EHIDomainWarn(_domain, _format, ...)    EHIDomainLogForLevel(WARN,    _domain, _format, ##__VA_ARGS__)
#define EHIDomainInfo(_domain, _format, ...)    EHIDomainLogForLevel(INFO,    _domain, _format, ##__VA_ARGS__)
#define EHIDomainDebug(_domain, _format, ...)   EHIDomainLogForLevel(DEBUG,   _domain, _format, ##__VA_ARGS__)
#define EHIDomainVerbose(_domain, _format, ...) EHIDomainLogForLevel(VERBOSE, _domain, _format, ##__VA_ARGS__)

// define conveience logging macros that use the general domain
#define EHIError(_format, ...)   EHIDomainError(EHILogDomainGeneral,   _format, ##__VA_ARGS__)
#define EHIWarn(_format, ...)    EHIDomainWarn(EHILogDomainGeneral,    _format, ##__VA_ARGS__)
#define EHIInfo(_format, ...)    EHIDomainInfo(EHILogDomainGeneral,    _format, ##__VA_ARGS__)
#define EHIDebug(_format, ...)   EHIDomainDebug(EHILogDomainGeneral,   _format, ##__VA_ARGS__)
#define EHIVerbose(_format, ...) EHIDomainVerbose(EHILogDomainGeneral, _format, ##__VA_ARGS__)

// interface for bootstrapping the logger
@interface EHILogging : NSObject
+ (void)prepareToLaunch;
@end
