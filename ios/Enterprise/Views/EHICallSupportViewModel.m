//
//  EHICallSupportViewModel.m
//  Enterprise
//
//  Created by mplace on 6/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHICallSupportViewModel.h"
#import "EHIUserManager.h"
#import "EHIConfiguration.h"
#import "EHICollectionButtonAction.h"

typedef NS_ENUM(NSUInteger, EHICallSupportType) {
    EHICallSupportTypeCallCenter,
    EHICallSupportTypeRoadside,
};

@interface EHICallSupportViewModel () <EHIUserListener>
@property (copy  , nonatomic) NSString *title;
@property (copy  , nonatomic) NSString *subtitle;
@property (copy  , nonatomic) NSArray *models;
@property (assign, nonatomic) BOOL hasSingleCallButton;
@end

@implementation EHICallSupportViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        // start listening for user updates
        [[EHIUserManager sharedInstance] addListener:self];
    }
    
    return self;
}

# pragma mark - Actions

- (void)dismiss
{
    self.router.transition
        .dismiss.start(nil);
}

# pragma mark - EHIUserListener

- (void)manager:(EHIUserManager *)manager didChangeAuthenticationForUser:(EHIUser *)user
{
    NSArray *models = @[];
    
    // if the user has a current rental, show roadside otherwise only show the call center button
    self.hasSingleCallButton = !user.currentRentals.count;
    
    if(!self.hasSingleCallButton) {
        // include the roadside and call center models
        models = [models ehi_safelyAppend:[self modelForType:EHICallSupportTypeRoadside]];
    }
    
    self.models = [models ehi_safelyAppend:[self modelForType:EHICallSupportTypeCallCenter]];
}

# pragma mark - Button View Models

- (EHICollectionButtonAction *)modelForType:(EHICallSupportType)type
{
    EHICollectionButtonAction *action = [EHICollectionButtonAction new];
    
    if (self.hasSingleCallButton) {
        action.title = EHILocalizedString(@"dashboard_call_support_button", @"CALL NOW", @"");
    }
    else {
        action.attributedTitle = [self actionTitleForType:type];
    }

    action.iconName = @"icon_phone_01";
    action.block = [self actionForType:type];
    action.alignment = self.hasSingleCallButton ? UIControlContentVerticalAlignmentCenter : UIControlContentHorizontalAlignmentLeft;
    
    return action;
}

- (NSAttributedString *)actionTitleForType:(EHICallSupportType)type
{
    NSString *title    = @"";
    NSString *subtitle = @"";
    
    switch (type) {
        case EHICallSupportTypeCallCenter: {
            title = EHILocalizedString(@"call_support_call_center_button_title", @"CUSTOMER SUPPORT", @"title for the call center button");
            subtitle = EHILocalizedString(@"call_support_call_center_button_subtitle", @"For all other questions", @"subtitle for the call center button");
        } break;
        case EHICallSupportTypeRoadside: {
            title = EHILocalizedString(@"call_support_roadside_assistance_button_title", @"ROADSIDE ASSISTANCE", @"title for the roadside assistance button");
            subtitle = EHILocalizedString(@"call_support_roadside_assistance_button_subtitle", @"Problems with your rental?", @"subtitle for the roadside assistance button");
        } break;
    }
    
    return [self attributedTitle:title subtitle:subtitle];
}

- (NSAttributedString *)attributedTitle:(NSString *)title subtitle:(NSString *)subtitle
{
    return EHIAttributedStringBuilder.new.color([UIColor whiteColor])
        .text(title).fontStyle(EHIFontStyleBold, 18.f).lineSpacing(6.0).newline
        .appendText(subtitle).fontStyle(EHIFontStyleLight, 14.f).string;
}

- (void(^)(UIButton *))actionForType:(EHICallSupportType)type
{
    __weak typeof(self) welf = self;

    return ^(UIButton *button) {
        // track the selected phone number
        [EHIAnalytics trackAction:EHIAnalyticsActionCallSupport handler:^(EHIAnalyticsContext *context) {
            context[EHIAnalyticsPhoneTypeKey] = [self analyticsPhoneNameForType:type];
        }];
        // call the correct number
        [UIApplication ehi_promptPhoneCall:[welf phoneNumberForType:type]];
    };
}

- (NSString *)phoneNumberForType:(EHICallSupportType)type
{
    switch(type) {
        case EHICallSupportTypeCallCenter:
            return self.configuration.primarySupportPhone.number;
        case EHICallSupportTypeRoadside:
            return self.configuration.roadsideAssistancePhone.number;
    }
}

- (NSString *)analyticsPhoneNameForType:(EHICallSupportType)type
{
    switch(type) {
        case EHICallSupportTypeCallCenter:
            return EHIAnalyticsPhoneTypeCallCenter;
        case EHICallSupportTypeRoadside:
            return EHIAnalyticsPhoneTypeRoadside;
    }
}

# pragma mark - Setters

- (void)setModels:(NSArray *)models
{
    _models = models;
    
    self.title = self.hasSingleCallButton
        ? EHILocalizedString(@"dashboard_call_support_title", @"Call Customer Support", @"")
        : EHILocalizedString(@"call_support_modal_title", @"Who do you want to call?", @"call support modal title");
    
    self.subtitle = self.hasSingleCallButton
        ? EHILocalizedString(@"dashboard_call_support_text", @"Get help, ask general questions, or offer your feedback 24 hours a day, 7 days a week.", @"")
        : @"";
}

# pragma mark - Accessors

- (EHIConfiguration *)configuration
{
    return [EHIConfiguration configuration];
}

@end
