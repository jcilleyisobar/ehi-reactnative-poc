//
//  EHIDealsViewModel.h
//  Enterprise
//
//  Created by Rafael Machado on 19/06/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

typedef NS_ENUM(NSInteger, EHIDealsSection) {
    EHIDealsSectionWeekendSpecial,
    EHIDealsSectionLocal,
    EHIDealsSectionInternacional,
    EHIDealsSectionOther
};

@class EHIDealHeaderViewModel;
@interface EHIDealsViewModel : EHIViewModel <MTRReactive>
@property (copy  , nonatomic, readonly) NSString *title;
@property (strong, nonatomic, readonly) EHIViewModel *weekendSpecial;
@property (copy  , nonatomic, readonly) NSArray *localDeals;
@property (copy  , nonatomic, readonly) NSArray *internacionalDeals;
@property (copy  , nonatomic, readonly) NSArray *otherDeals;

- (EHIDealHeaderViewModel *)headerForSection:(EHIDealsSection)section;

@end
