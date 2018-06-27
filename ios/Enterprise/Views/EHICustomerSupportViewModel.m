//
//  EHICustomerSupportViewModel.m
//  Enterprise
//
//  Created by fhu on 6/25/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICustomerSupportViewModel.h"
#import "EHICustomerSupportSelectionViewModel.h"
#import "EHIConfiguration.h"
#import "EHIViewModel_Subclass.h"

@interface EHICustomerSupportViewModel()
@property (strong, nonatomic) NSDictionary *sectionHeaders;
@end

@implementation EHICustomerSupportViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        EHIConfiguration *configuration = [EHIConfiguration configuration];
        _title = EHILocalizedString(@"customer_support_navigation_title", @"Customer Support", @"navigation bar title for Customer Support");
        
        _callModels = configuration.customerSupportNumbers.map(^(EHIPhone *phone) {
            return [[EHICustomerSupportSelectionViewModel alloc] initWithModel:phone];
        });
       
        _moreOptionsModels = @[
           configuration.sendMessageUrl.length   == 0 ? [NSNull null] : [EHICustomerSupportSelectionViewModel modelForUrlType:EHISupportUrlTypeSendMessage url:configuration.sendMessageUrl],
           configuration.searchAnswersUrl.length == 0 ? [NSNull null] : [EHICustomerSupportSelectionViewModel modelForUrlType:EHISupportUrlTypeSearchAnswers url:configuration.searchAnswersUrl]
        ].reject(NSNull.class);
    }
    
    return self;
}

# pragma mark - Actions

- (void)selectIndexPath:(NSIndexPath *)indexPath
{
    EHICustomerSupportSection section = (EHICustomerSupportSection)indexPath.section;
    if (section == EHICustomerSupportSectionCall) {
        [UIApplication ehi_promptPhoneCall:[self modelAtIndexPath:indexPath].phoneNumber];
    } else {
        [UIApplication ehi_promptUrl:[self modelAtIndexPath:indexPath].url];
    }
    
    [self trackSelectionWithState:section forAction:[self modelAtIndexPath:indexPath].eventName];
}

- (EHICustomerSupportSelectionViewModel *)modelAtIndexPath:(NSIndexPath *)indexPath
{
    switch ((EHICustomerSupportSection)indexPath.section) {
        case EHICustomerSupportSectionCall:
            return self.callModels[indexPath.item];
        case EHICustomerSupportSectionMoreOptions:
            return self.moreOptionsModels[indexPath.item];
        default:
            return nil;
    }
}

# pragma mark - Accessors

- (EHISectionHeaderModel *)headerForSection:(EHICustomerSupportSection)section
{
    return self.sectionHeaders[@(section)];
}

- (NSDictionary *)sectionHeaders
{
    if(_sectionHeaders) {
        return _sectionHeaders;
    }
    
    NSAttributedString *moreOptionsString = EHIAttributedStringBuilder.new
        .text(EHILocalizedString(@"customer_settings_more_options_section_header_prefix", @"MORE CONTACT OPTIONS", @"")).fontStyle(EHIFontStyleBold, 14)
        .space.appendText(EHILocalizedString(@"customer_settings_more_options_section_header_suffix", @"(Exits the app)", @"")).fontStyle(EHIFontStyleLight, 14).string;
    
    _sectionHeaders = [EHISectionHeaderModel modelsWithTitles:@[
        [NSNull null],
        EHILocalizedString(@"customer_settings_call_section_header", @"CALL ENTERPRISE", @""),
        moreOptionsString,
    ]];
    
    return _sectionHeaders;
}

#pragma mark - Analytics

- (void)trackSelectionWithState:(EHICustomerSupportSection)section forAction:(NSString *)action
{
    NSString *state = section == EHICustomerSupportSectionCall
        ? EHIAnalyticsStateCustomerSupportCall
        : EHIAnalyticsStateCustomerSupportMoreHelp;
    [EHIAnalytics trackAction:action handler:^(EHIAnalyticsContext *context) {
        context.state = state;
    }];
}

@end
