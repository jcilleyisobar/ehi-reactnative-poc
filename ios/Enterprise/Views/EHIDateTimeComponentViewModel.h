//
//  EHIDateTimeComponentViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 5/3/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHITemporalSelectionViewModel.h"
#import "EHIDateTimeComponentSection.h"
#import "EHIDateTimeUpdatableProtocol.h"

typedef NS_ENUM(NSInteger, EHIDateTimeComponentLayout) {
    EHIDateTimeComponentLayoutMap,
    EHIDateTimeComponentLayoutFilter
};

@interface EHIDateTimeComponentViewModel : EHIViewModel <MTRReactive, EHIDateTimeUpdatableProtocol>

@property (assign, nonatomic, readonly) EHIDateTimeComponentLayout layout;
@property (copy  , nonatomic, readonly) NSString *pickupTitle;
@property (copy  , nonatomic, readonly) NSString *returnTitle;
@property (strong, nonatomic, readonly) EHITemporalSelectionViewModel *pickupDateModel;
@property (strong, nonatomic, readonly) EHITemporalSelectionViewModel *pickupTimeModel;
@property (strong, nonatomic, readonly) EHITemporalSelectionViewModel *returnDateModel;
@property (strong, nonatomic, readonly) EHITemporalSelectionViewModel *returnTimeModel;
@property (assign, nonatomic, readonly) BOOL hasData;

@property (assign, nonatomic) BOOL hidePickupTimeSection;
@property (assign, nonatomic) BOOL hideReturnTimeSection;

- (void)setDate:(NSDate *)date inSection:(EHIDateTimeComponentSection)section;

@end
