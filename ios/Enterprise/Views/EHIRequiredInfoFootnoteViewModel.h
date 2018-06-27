//
//  EHIRequiredInfoFootnoteViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 07/05/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

typedef NS_ENUM(NSUInteger, EHIRequiredInfoFootnoteType) {
    EHIRequiredInfoFootnoteTypeProfile,
    EHIRequiredInfoFootnoteTypeEnrollment,
    EHIRequiredInfoFootnoteTypeReservation
};

@interface EHIRequiredInfoFootnoteViewModel : EHIViewModel <MTRReactive>

+ (instancetype)initWithType:(EHIRequiredInfoFootnoteType)type;

@property (copy  , nonatomic, readonly) NSAttributedString *note;
@property (assign, nonatomic, readonly) EHIRequiredInfoFootnoteType type;

@end
