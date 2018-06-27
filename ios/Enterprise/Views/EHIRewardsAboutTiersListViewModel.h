//
//  EHIRewardsAboutTiersListViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 1/16/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHILoyaltyTierDataProvider.h"
#import "EHITierDetailsViewModel.h"

typedef NS_ENUM(NSInteger, EHIRewardsAboutTiersListSection) {
    EHIRewardsAboutTiersListSectionPlus,
    EHIRewardsAboutTiersListSectionSilver,
    EHIRewardsAboutTiersListSectionGold,
    EHIRewardsAboutTiersListSectionPlatinum
};

@interface EHIRewardsAboutTiersListViewModel : EHIViewModel <MTRReactive>
@property (strong, nonatomic, readonly) EHITierDetailsViewModel *plusModel;
@property (strong, nonatomic, readonly) EHITierDetailsViewModel *silverModel;
@property (strong, nonatomic, readonly) EHITierDetailsViewModel *goldModel;
@property (strong, nonatomic, readonly) EHITierDetailsViewModel *platinumModel;
@property (assign, nonatomic, readonly) EHIRewardsAboutTiersListSection selectedSection;

@property (assign, nonatomic) EHIRewardsAboutTiersListSection currentSection;
@property (strong, nonatomic) NSDictionary *tierStates;

- (UIColor *)colorForSection:(EHIRewardsAboutTiersListSection)section;
- (NSString *)titleForSection:(EHIRewardsAboutTiersListSection)section;
- (void)selectSection:(EHIRewardsAboutTiersListSection)section;

@end
