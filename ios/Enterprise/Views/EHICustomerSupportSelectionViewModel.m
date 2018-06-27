//
//  EHICustomerSupportSelectionViewModel.m
//  Enterprise
//
//  Created by fhu on 6/25/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICustomerSupportSelectionViewModel.h"
#import "EHIPhone.h"

@interface EHICustomerSupportSelectionViewModel()
@property (assign, nonatomic) EHISupportUrlType urlType;
@property (assign, nonatomic) EHIPhoneType phoneType;
@end

@implementation EHICustomerSupportSelectionViewModel

+ (instancetype)modelForUrlType:(EHISupportUrlType)type url:(NSString *)url
{
    EHICustomerSupportSelectionViewModel *viewModel = [EHICustomerSupportSelectionViewModel new];
    viewModel.url = url;
    viewModel.urlType = type;
    
    return viewModel;
}

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    if ([model isKindOfClass:[EHIPhone class]]) {
        [self updateWithPhone:model];
    }
}

- (void)updateWithPhone:(EHIPhone *)phone
{
    self.phoneNumber = phone.number;
    self.phoneType   = phone.type;
    
    self.iconImageName = @"icon_phone_03";
    self.detailsText = [self detailsForPhoneType:phone.type];
    
    self.headerAttributedString = EHIAttributedStringBuilder.new
        .text([self titleForPhoneType:phone.type]).fontStyle(EHIFontStyleLight, 24.0f).color([UIColor ehi_greenColor]).lineSpacing(6.0).newline
        .appendText(phone.number).fontStyle(EHIFontStyleLight, 24.0f).color([UIColor ehi_greenColor]).string;
}

#pragma mark - Setters

- (void)setUrlType:(EHISupportUrlType)urlType
{
    _urlType = urlType;
    
    self.iconImageName = [self iconImageForUrlType:urlType];
    self.detailsText = [self detailsForUrlType:urlType];
    self.headerAttributedString = [self headerForUrlType:urlType];
}

//Helpers

- (NSString *)iconImageForUrlType:(EHISupportUrlType)type
{
    switch (type) {
        case EHISupportUrlTypeSendMessage:
            return nil;
        case EHISupportUrlTypeSearchAnswers:
            return @"icon_search";
    }
}

- (NSAttributedString *)headerForUrlType:(EHISupportUrlType)type
{
    switch (type) {
        case EHISupportUrlTypeSendMessage:
            return [self attributedUrlHeaderForText:EHILocalizedString(@"customer_support_send_message_header", @"Send us a Message", @"")];
        case EHISupportUrlTypeSearchAnswers:
            return [self attributedUrlHeaderForText:EHILocalizedString(@"customer_support_search_answer_header", @"Search Answers", @"")];
    }
}

- (NSAttributedString *)attributedUrlHeaderForText:(NSString *)text
{
    return [NSAttributedString attributedStringWithString:text font:[UIFont ehi_fontWithStyle:EHIFontStyleLight size:24.0f] color:[UIColor ehi_greenColor]];
}

- (NSString *)detailsForUrlType:(EHISupportUrlType)type
{
    switch (type) {
        case EHISupportUrlTypeSendMessage:
            return EHILocalizedString(@"customer_support_send_message_details", @"Ask a general or rental-specific question.", @"");
        case EHISupportUrlTypeSearchAnswers:
            return EHILocalizedString(@"customer_support_search_answers_details", @"Search our collection of frequently asked questions and their answers.", @"");
    }
}

- (NSString *)titleForPhoneType:(EHIPhoneType)type
{
    switch (type) {
        case EHIPhoneTypeContactUs:
            return EHILocalizedString(@"customer_support_contact_us_title", @"Customer Service Line:", @"");
        case EHIPhoneTypeRoadside:
            return EHILocalizedString(@"customer_support_roadside_title", @"Roadside Assistance:", @"");
        case EHIPhoneTypeEPlus:
            return EHILocalizedString(@"customer_support_eplus_title", @"Enterprise Plus:", @"");
        case EHIPhoneTypeDisabilities:
            return EHILocalizedString(@"customer_support_disabilities", @"Customers with Disabilities:", @"");
        default:
            return @"";
    }
}

- (NSString *)detailsForPhoneType:(EHIPhoneType)type
{
    switch (type) {
        case EHIPhoneTypeContactUs:
            return EHILocalizedString(@"customer_support_contact_us_details", @"Use for general questions and support.", @"");
        case EHIPhoneTypeRoadside:
            return EHILocalizedString(@"customer_support_roadside_details", @"Use if you experience an issue with your vehicle. If an emergency, please dial 911.", @"");
        case EHIPhoneTypeEPlus:
            return EHILocalizedString(@"customer_support_eplus_details", @"Get help with our reward and loyalty program.", @"");
        case EHIPhoneTypeDisabilities:
            return EHILocalizedString(@"customer_support_disabilities_details", @"Get extra accommodations to meet your needs.", @"");
        default:
            return @"";
    }
}

- (NSString *)eventName
{
    if (self.phoneType) {
        switch (self.phoneType) {
            case EHIPhoneTypeContactUs:
                return EHIAnalyticsActionCustomerService;
            case EHIPhoneTypeRoadside:
                return EHIAnalyticsActionRoadsideAssistance;
            case EHIPhoneTypeEPlus:
                return EHIAnalyticsActionEnterprisePlus;
            case EHIPhoneTypeDisabilities:
                return EHIAnalyticsActionCustomersDisabilities;
            default:
                return @"";
        }
    } else {
        switch (self.urlType) {
            case EHISupportUrlTypeSendMessage:
                return EHIAnalyticsActionSendMessage;
            case EHISupportUrlTypeSearchAnswers:
                return EHIAnalyticsActionFAQs;
            default:
                return @"";
        }
    }
}

@end
