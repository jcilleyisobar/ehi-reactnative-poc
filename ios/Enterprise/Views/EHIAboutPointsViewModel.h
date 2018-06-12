//
//  EHIAboutPointsViewModel.h
//  Enterprise
//
//  Created by frhoads on 1/12/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIRewardsAnalyticsViewModel.h"
#import "EHIAboutPointsHeaderViewModel.h"
#import "EHIAboutPointsRedeemViewModel.h"
#import "EHIAboutPointsReusableViewModel.h"

typedef NS_ENUM(NSUInteger, EHIAboutPointsSection) {
    EHIAboutPointsSectionHeader,
    EHIAboutPointsSectionRedeem,
    EHIAboutPointsSectionPoints,
    EHIAboutPointsSectionFooter
};

@interface EHIAboutPointsViewModel : EHIRewardsAnalyticsViewModel <MTRReactive>
@property (copy  , nonatomic, readonly) NSString *title;
@property (strong, nonatomic, readonly) EHIAboutPointsHeaderViewModel *headerModel;
@property (strong, nonatomic, readonly) EHIAboutPointsRedeemViewModel *redeemModel;
@property (strong, nonatomic, readonly) EHIModel *footerModel;
@property (strong, nonatomic, readonly) NSArray *reusableModels;

- (void)selectItemAtIndexPath:(NSIndexPath *)indexPath;
@end
