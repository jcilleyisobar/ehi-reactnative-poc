//
//  EHIAboutPointsViewModel.m
//  Enterprise
//
//  Created by frhoads on 1/12/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIAboutPointsViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHIAboutPointsHeaderViewModel.h"
#import "EHIUserManager.h"

@interface EHIAboutPointsViewModel()
@property (strong, nonatomic) EHIAboutPointsHeaderViewModel *headerModel;
@property (strong, nonatomic) EHIAboutPointsRedeemViewModel *redeemModel;
@property (strong, nonatomic) EHIModel *footerModel;
@property (strong, nonatomic) NSArray *reusableModels;
@end

@implementation EHIAboutPointsViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title = EHILocalizedString(@"about_points_title", @"About Points", @"");
    }
    
    return self;
}

- (EHIAboutPointsHeaderViewModel *)headerModel
{
    if(!_headerModel) {
        _headerModel = [[EHIAboutPointsHeaderViewModel alloc] initWithModel:self.loyalty];
    }
    
    return _headerModel;
}

- (EHIAboutPointsRedeemViewModel *)redeemModel
{
    if(!_redeemModel) {
        _redeemModel = [EHIAboutPointsRedeemViewModel new];
    }
    
    return _redeemModel;
}

- (EHIModel *)footerModel
{
    if(!_footerModel) {
        _footerModel = [EHIModel new];
    }
    
    return _footerModel;
}

- (NSArray *)reusableModels
{
    if(!_reusableModels) {
        _reusableModels = [EHIAboutPointsReusableViewModel all];
    }
    
    return _reusableModels;
}

- (void)selectItemAtIndexPath:(NSIndexPath *)indexPath
{
    switch(indexPath.section) {
        case EHIAboutPointsSectionFooter:
            [self showAboutEnterprisePlus]; break;
        default:
            break;
    }
}

- (void)showAboutEnterprisePlus
{
    self.router.transition.push(EHIScreenAboutEnterprisePlus).start(nil);
}

# pragma mark - Passthrough

- (EHIUserBasicProfile *)profile
{
    return [EHIUser currentUser].profiles.basic;
}

- (EHIUserLoyalty *)loyalty
{
    return self.profile.loyalty;
}

@end
