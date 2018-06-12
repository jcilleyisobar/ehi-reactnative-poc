//
//  EHIReservationExtrasViewModel.h
//  Enterprise
//
//  Created by Ty Cobb on 4/1/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationStepViewModel.h"
#import "EHIReservation.h"
#import "EHICarClassViewModel.h"
#import "EHISectionHeader.h"
#import "EHIExtrasExtraCell.h"
#import "EHIExtrasExtraViewModel.h"
#import "EHIReservationPriceButtonType.h"

typedef NS_ENUM(NSUInteger, EHIExtrasSection) {
    EHIExtrasSectionCarClass,
    EHIExtrasSectionPlacard,
    EHIExtrasSectionIncluded,
    EHIExtrasSectionMandatory,
    EHIExtrasSectionEquipment,
    EHIExtrasSectionFuel,
    EHIExtrasSectionProtection,
    EHIExtrasSectionTermsAndConditions
};

@class EHIPlacardViewModel;
@interface EHIExtrasViewModel : EHIReservationStepViewModel <MTRReactive>

@property (copy  , nonatomic) NSString *title;
@property (copy  , nonatomic) EHIPlacardViewModel *placardModel;
@property (strong, nonatomic) EHICarClassViewModel *carClassModel;
@property (strong, nonatomic) EHICarClassViewModel *carClassPlaceholderModel;
@property (strong, nonatomic) EHICarClassExtras *carExtras;
@property (strong, nonatomic) EHIModel *termsModel;
@property (strong, nonatomic) EHICarClassPriceSummary *priceSummary;
@property (assign, nonatomic) EHIReservationPriceButtonSubtitleType priceSubtitleType;
@property (assign, nonatomic) EHIReservationPriceButtonType priceType;

@property (copy  , nonatomic) NSString *loadingTitle;
@property (copy  , nonatomic) NSAttributedString *buttonTitle;
@property (copy  , nonatomic) NSIndexPath *selectedPath;
@property (copy  , nonatomic) NSIndexPath *selectedTogglePath;
@property (copy  , nonatomic) NSIndexPath *lastSelectedPath;
@property (assign, nonatomic) BOOL isLoading;
@property (assign, nonatomic) BOOL shouldDisableContinueButton;
@property (assign, nonatomic) BOOL justChangedToggle;

@property (assign, nonatomic, readonly) BOOL priceIsLoading;

- (EHISectionHeaderModel *)headerForSection:(EHIExtrasSection)section;
- (NSArray *)extrasInSection:(EHIExtrasSection)section;

- (void)selectExtraAtIndexPath:(NSIndexPath *)indexPath;
- (void)showDetailsForExtraAtIndexPath:(NSIndexPath *)indexPath;

- (void)didChangeQuantityOfExtras;
- (void)finishUpdatingExtras;

@end
