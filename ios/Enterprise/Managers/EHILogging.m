//
//  EHILogging.m
//  Enterprise
//
//  Created by Ty Cobb on 1/13/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import <CocoaLumberjack/DDASLLogger.h>
#import <CocoaLumberjack/DDTTYLogger.h>
#import "EHILogging.h"

@interface EHILogFormatter : NSObject <DDLogFormatter>

@end

@implementation EHILogging

+ (void)prepareToLaunch
{
    NSArray *loggers = @[
        [DDASLLogger new],
        [DDTTYLogger new]
    ];
    
    // a custom log formatter for prettier output
    EHILogFormatter *formatter = [EHILogFormatter new];

    // apply the formatter to each logger and register it
    for(DDAbstractLogger *logger in loggers) {
        [logger setLogFormatter:formatter];
        [DDLog addLogger:logger];
    }
}

@end

@implementation EHILogFormatter

- (NSString *)formatLogMessage:(DDLogMessage *)message
{
    return [[NSString alloc] initWithFormat:@"%@ (%@): %@", EHIDomainNameForMessage(message), EHILevelNameForMessage(message), message->logMsg];
}

NSString * EHIDomainNameForMessage(DDLogMessage *message)
{
    // extract just the domain bits
    EHILogDomain domain = message->logFlag & ~LOG_LEVEL_VERBOSE;
    
    switch(domain) {
        case EHILogDomainGeneral:
            return @"GENERAL  ";
        case EHILogDomainNetwork:
            return @"NETWORK  ";
        case EHILogDomainModels:
            return @"MODELS   ";
        case EHILogDomainFiles:
            return @"FILES    ";
        case EHILogDomainAnalytics:
            return @"ANALYTICS";
        case EHILogDomainMemory:
            return @"MEMORY   ";
    }
}

NSString * EHILevelNameForMessage(DDLogMessage *message)
{
    // extract just the log level bits
    NSInteger level = message->logFlag & LOG_LEVEL_VERBOSE;
    
    switch(level) {
        case LOG_FLAG_ERROR:
            return @"E";
        case LOG_FLAG_INFO:
            return @"I";
        case LOG_FLAG_VERBOSE:
            return @"V";
        case LOG_FLAG_WARN:
            return @"W";
        case LOG_FLAG_DEBUG:
            return @"D";
        default: return @"U";
    }
}

@end
