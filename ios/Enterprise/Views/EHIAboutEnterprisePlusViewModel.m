//
//  EHIAboutEnterprisePlusViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 12/22/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIAboutEnterprisePlusViewModel.h"
#import "EHIAboutEnterprisePlusHeaderViewModel.h"
#import "EHIAboutEnterprisePlusPointsViewModel.h"
#import "EHIAboutEnterprisePlusTierHeaderViewModel.h"
#import "EHIRewardsAboutTiersListViewModel.h"
#import "EHIAboutEnterprisePlusFooterViewModel.h"

@interface EHIAboutEnterprisePlusViewModel ()
@property (strong, nonatomic) EHIViewModel *headerModel;
@property (strong, nonatomic) NSArray *pointsModels;
@property (strong, nonatomic) EHIViewModel *tiersHeaderModel;
@property (strong, nonatomic) EHIRewardsAboutTiersListViewModel *tiersModel;
@property (strong, nonatomic) EHIViewModel *footerModel;
@property (strong, nonatomic) EHIModel *legalModel;
@end

@implementation EHIAboutEnterprisePlusViewModel

- (NSString *)title
{
    return EHILocalizedString(@"rewards_learn_more_title", @"About Enterprise Plus", @"");
}

- (EHIViewModel *)headerModel
{
    if(!_headerModel) {
        _headerModel = [EHIAboutEnterprisePlusHeaderViewModel new];
    }
    
    return _headerModel;
}

- (NSArray *)pointsModels
{
    if(!_pointsModels) {
        _pointsModels = [EHIAboutEnterprisePlusPointsViewModel all];
    }
    
    return _pointsModels;
}

- (EHIViewModel *)tiersHeaderModel
{
    if(!_tiersHeaderModel) {
        _tiersHeaderModel = [EHIAboutEnterprisePlusTierHeaderViewModel new];
    }
    
    return _tiersHeaderModel;
}

- (EHIRewardsAboutTiersListViewModel *)tiersModel
{
    if(!_tiersModel) {
        _tiersModel = [EHIRewardsAboutTiersListViewModel new];
    }
    
    return _tiersModel;
}

- (EHIViewModel *)footerModel
{
    if(!_footerModel) {
        _footerModel = [EHIAboutEnterprisePlusFooterViewModel new];
    }
    
    return _footerModel;
}

- (EHIModel *)legalModel
{
    if(!_legalModel) {
        _legalModel = [EHIModel placeholder];
    }
    
    return _legalModel;
}

@end
