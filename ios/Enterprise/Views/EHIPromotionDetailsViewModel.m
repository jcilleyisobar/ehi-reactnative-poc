//
//  EHIPromotionDetailsViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 3/28/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIPromotionDetailsViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHIReservationBuilder.h"
#import "EHIPromotionContract.h"
#import "EHIInfoModalViewModel.h"
#import "EHIUser.h"

@interface EHIPromotionDetailsViewModel()
@property (strong, nonatomic) EHIPromotionContract *weekendSpecial;
@end

@implementation EHIPromotionDetailsViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _navigationTitle = EHILocalizedString(@"weekend_special_navigation_title", @"Promotion Details", @"");
    }
    
    return self;
}

- (EHIPromotionDetailsImageCellViewModel *)imageModel
{
    if(!_imageModel) {
        _imageModel = [EHIPromotionDetailsImageCellViewModel new];
    }
    
    return _imageModel;
}

- (EHIPromotionDetailsTitleCellViewModel *)titleModel
{
    if(!_titleModel) {
        _titleModel = [[EHIPromotionDetailsTitleCellViewModel alloc] initWithModel:self.weekendSpecial.name];
    }
    
    return _titleModel;
}

- (NSArray *)bulletModels
{
    NSArray *bulletItems = [self.weekendSpecial.descriptions copy];
    return (bulletItems ?: @[]).map(^(NSString *bulletItem){
        return [[EHIPromotionDetailsBulletItemCellViewModel alloc] initWithModel:bulletItem];
    });
}

- (EHIPromotionDetailsActionCellViewModel *)actionModel
{
    if(!_actionModel) {
        _actionModel = [[EHIPromotionDetailsActionCellViewModel alloc] initWithModel:self.weekendSpecial];
    }
    
    return _actionModel;
}

- (EHIPromotionDetailsPolicyCellViewModel *)policyModel
{
    if(!_policyModel) {
        _policyModel = [EHIPromotionDetailsPolicyCellViewModel new];
    }
    
    return _policyModel;
}

- (EHIPromotionContract *)weekendSpecial
{
    if(!_weekendSpecial) {
        _weekendSpecial = [NSLocale ehi_country].weekendSpecial;
    }
    
    return _weekendSpecial;
}

# pragma mark - Actions

- (void)didTapWeekendSpecialStartReservation
{
    BOOL hasContractAttached = [EHIUser currentUser].corporateContract != nil;
    if(hasContractAttached) {
        [self presentContractAttachedModal];
    } else {
        [self showLocations];
    }
}

- (void)presentContractAttachedModal
{
    EHIInfoModalViewModel *modal = [EHIInfoModalViewModel new];
    modal.title = EHILocalizedString(@"deals_contract_combination_dialog_title", @"Multiple Codes", @"");
    modal.details = EHILocalizedString(@"deals_contract_combination_dialog_text", @"Deals cannot be combined with other accounts or contracts. Would you like to replace the account on your profile with the selected promotion?", @"");
    modal.firstButtonTitle = EHILocalizedString(@"standard_button_yes", @"Yes", @"");
    modal.secondButtonTitle = EHILocalizedString(@"deals_contract_combination_dialog_no_button", @"No, thanks", @"");
    modal.buttonLayout = EHIInfoModalButtonLayoutSecondaryDismiss;
    
    __weak typeof(self) welf = self;
    [modal present:^BOOL(NSInteger index, BOOL canceled) {
        if(!canceled && index == 0) {
            [welf showLocations];
        }
        
        return YES;
    }];
}

- (void)showLocations
{
    [EHIAnalytics trackAction:EHIAnalyticsWkndPromoDetailsActionStartRes handler:nil];
    
    self.builder.discountCode = self.weekendSpecial.code;
    self.router.transition.push(EHIScreenLocations).start(nil);
}

# pragma mark - Passthrough

- (EHIReservationBuilder *)builder
{
    return [EHIReservationBuilder sharedInstance];
}

@end
