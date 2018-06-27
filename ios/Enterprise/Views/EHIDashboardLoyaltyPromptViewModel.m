//
//  EHIDashboardLoyaltyPromptViewModel.m
//  Enterprise
//
//  Created by mplace on 5/18/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIDashboardLoyaltyPromptViewModel.h"
#import "EHIUserManager.h"

@implementation EHIDashboardLoyaltyPromptViewModel

- (instancetype)initWithModel:(id)model
{
    if (self = [super initWithModel:model]) {
        _title = EHILocalizedString(@"dashboard_loyalty_prompt_cell_title", @"Not an Enterprise Plus member yet?", @"title for the loyalty prompt cell");
        _details = EHILocalizedString(@"dashboard_loyalty_prompt_cell_details", @"You could be earning points toward free rentals and upgrades.", @"details for the loyalty prompt cell");
        _actionButtonTitle = EHILocalizedString(@"dashboard_loyalty_prompt_cell_button_title", @"JOIN NOW", @"button title for the loyalty prompt cell");
        _iconImageName = @"logo_eplus";
    }
    
    return self;
}

# pragma mark - Actions

- (void)joinEnterprisePlus
{
    [EHIAnalytics trackAction:EHIAnalyticsDashActionJoin handler:nil];
    
    [[EHIUserManager sharedInstance] promptSignUpWithHandler:nil];
}

@end
