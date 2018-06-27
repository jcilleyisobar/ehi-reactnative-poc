//
//  EHIFlightDetailsViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 6/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSUInteger, EHIFlightDetailsState) {
    EHIFlightDetailsStateNone,
    EHIFlightDetailsStateReview,
};

typedef NS_ENUM(NSUInteger, EHIFlightDetailsSection) {
    EHIFlightDetailsSectionHelp,
    EHIFlightDetailsSectionSearch,
    EHIFlightDetailsSectionNoFlightButton,
    EHIFlightDetailsSectionFlightNumber
};

@class EHIFormFieldLabelViewModel;
@class EHIFormFieldButtonViewModel;
@class EHIFormFieldTextViewModel;
@class EHIFlightDetailsSearchViewModel;
@interface EHIFlightDetailsViewModel : EHIViewModel <MTRReactive>

@property (copy  , nonatomic, readonly) NSString *title;
@property (copy  , nonatomic, readonly) NSString *submitTitle;
@property (assign, nonatomic, readonly) BOOL invalidForm;
@property (assign, nonatomic, readonly) BOOL isLoading;
@property (strong, nonatomic) EHIFormFieldLabelViewModel *helpModel;
@property (strong, nonatomic) EHIFormFieldButtonViewModel *noFlightModel;
@property (strong, nonatomic) EHIFormFieldTextViewModel *flightNumberModel;
@property (strong, nonatomic) EHIFlightDetailsSearchViewModel *searchModel;
@property (assign, nonatomic) EHIFlightDetailsState state;

- (void)submitFlightDetails;
- (void)showSearchAirlines;

@end

NS_ASSUME_NONNULL_END
