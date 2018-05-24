//
//  EHIToast.h
//  Enterprise
//
//  Created by Alex Koller on 4/7/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

extern const NSTimeInterval EHIToastDurationShort;
extern const NSTimeInterval EHIToastDurationLong;

@interface EHIToast : NSObject

typedef NS_ENUM(NSUInteger, EHIToastStyle) {
    EHIToastStyleDark,
    EHIToastStyleLight
};

typedef NS_ENUM(NSUInteger, EHIToastPosition) {
    EHIToastPositionCenter,
    EHIToastPositionBottom
};

/** The styling applied to the toast */
@property (assign, nonatomic) EHIToastStyle style;
/** The position where the toast should be presented */
@property (assign, nonatomic) EHIToastPosition position;
/** The message to display in the toast */
@property (copy  , nonatomic) NSString *message;
/** The duration of the toast; defaults to @c EHIToastDurationShort */
@property (assign, nonatomic) NSTimeInterval duration;

@end
