//
//  EHIPromotionViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 3/30/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIPromotionViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHICountry.h"

@implementation EHIPromotionViewModel

# pragma mark - Accessors

- (NSString *)promotionName
{
    return [NSLocale ehi_country].weekendSpecial.name.uppercaseString;
}

- (NSString *)promotionButtonTitle
{
    return EHILocalizedString(@"menu_weekend_special_get_started", @"GET STARTED", @"title for menu: Promotion");
}

@end
