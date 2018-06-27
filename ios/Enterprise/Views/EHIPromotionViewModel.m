//
//  EHIPromotionViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 3/30/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIPromotionViewModel.h"
#import "EHIConfiguration.h"

@interface EHIPromotionViewModel ()
@property (copy, nonatomic) NSString *promotionName;
@end

@implementation EHIPromotionViewModel

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    [self refresh:nil];
}

- (void)didBecomeActive
{
    [super didBecomeActive];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refresh:) name:EHICountriesRefreshedNotification object:nil];
}

- (void)didResignActive
{
    [super didResignActive];
    
    [[NSNotificationCenter defaultCenter] removeObserver:self name:EHICountriesRefreshedNotification object:nil];
}

- (void)refresh:(NSNotification *)notification
{
    self.promotionName = [NSLocale ehi_country].weekendSpecial.name.uppercaseString;
}

# pragma mark - Accessors

- (NSString *)promotionButtonTitle
{
    return EHILocalizedString(@"menu_weekend_special_get_started", @"GET STARTED", @"title for menu: Promotion");
}

@end
