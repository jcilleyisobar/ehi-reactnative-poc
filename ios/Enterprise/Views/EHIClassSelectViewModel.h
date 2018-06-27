//
//  EHIClassSelectViewModel.h
//  Enterprise
//
//  Created by mplace on 3/19/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationStepViewModel.h"
#import "EHIReservationBuilder.h"
#import "EHIRedemptionPointsViewModel.h"
#import "EHIInformationBannerViewModel.h"
#import "EHIRedemptionPointsViewModel.h"
#import "EHIContractDetails.h"
#import "NAVPreview.h"
#import "EHICurrencyDiffersViewModel.h"

typedef NS_ENUM(NSUInteger, EHIClassSelectSection) {
    EHIClassSelectSectionRedemption,
    EHIClassSelectSectionBanner,
    EHIClassSelectSectionFilter,
    EHIClassSelectSectionActiveFilter,
    EHIClassSelectSectionDiscount,
    EHIClassSelectSectionCurrencyDiffers,
    EHIClassSelectSectionCarClasses,
    EHIClassSelectSectionFootnotes,
    EHIClassSelectTermsAndConditions
};

@interface EHIClassSelectViewModel : EHIReservationStepViewModel <MTRReactive>

@property (copy  , nonatomic) NSString *title;
@property (copy  , nonatomic) NSArray *carClassModels;
@property (copy  , nonatomic) NSArray *activeFilters;
@property (strong, nonatomic) EHIContractDetails *discount;
@property (strong, nonatomic) EHICurrencyDiffersViewModel *currencyModel;
@property (strong, nonatomic) EHIRedemptionPointsViewModel *redemptionModel;
@property (strong, nonatomic) EHIInformationBannerViewModel *bannerModel;
@property (strong, nonatomic) id<EHIPriceContext> price;
@property (assign, nonatomic) BOOL isLoading;
@property (assign, nonatomic) BOOL shouldInsetCarClassSection;
@property (strong, nonatomic) EHIModel *termsModel;

// computed
@property (assign, nonatomic, readonly) BOOL hideRedemption;

- (NAVPreview *)previewForIndex:(NSUInteger)index;

- (void)showEnterprisePlus;
- (void)selectCarClassAtIndex:(NSInteger)index;
- (void)showTermsAndConditions;
- (void)showDetailsForCarClassAtIndex:(NSInteger)index;
- (void)showFilterScreen;
- (void)clearFilters;

@end
