//
//  EHIWebViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 6/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIWebViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHIServices+Config.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHIWebViewModel ()
@property (assign, nonatomic) EHIWebContentType type;
@end

@implementation EHIWebViewModel

- (instancetype)initWithType:(EHIWebContentType)type htmlString:(NSString *)htmlString;
{
    if(self = [super init]) {
        _type       = type;
        _htmlString = [htmlString ehi_fontAdjustedHtml];
    }
    
    return self;
}

- (instancetype)initWithType:(EHIWebContentType)type;
{
    if(self = [super init]) {
        _type = type;
    }
    
    return self;
}

- (void)didInitialize
{
    [super didInitialize];
    
    switch (self.type) {
        case EHIWebContentTypePrivacy:
        case EHIWebContentTypeTermsOfUse:
        case EHIWebContentTypeTermsAndConditions:
        case EHIWebContentTypePrepayTermsAndConditions:
        case EHIWebContentTypeTaxes:
        case EHIWebContentTypeLicenses: {
            [self fetchVersionedContentWithType:self.type];
            break;
        }
        case EHIWebContentTypeNone:
        case EHIWebContentTypeWeekendSpecialTermsAndConditions: {
            break;
        }
    }
}

# pragma mark - Presentation / Dismissal

- (void)push
{
    self.hideModalNavigationBar = YES;
    self.router.transition
        .push(EHIScreenWebView).object(self).start(nil);
}

- (void)present
{
    self.router.transition
        .present(EHIScreenWebView).object(self).start(nil);
}

- (void)dismiss
{
    self.router.transition.dismiss.start(nil);
}

- (BOOL)isAnchorLink:(NSString *)link
{
    return [link containsString:@"#"];
}

-(NSString *)javascriptScrollCommandForLink:(NSString *)link
{
    NSString *command = nil;
    
    NSArray<NSString *>* components = [link componentsSeparatedByString:@"#"];
    if(components.count == 2) {
        NSString *target = components.lastObject;
        command = [NSString stringWithFormat: @"                  \
            function findElement(name) {                          \
                let byName = document.getElementsByName(name)[0]; \
                let byId   = document.getElementById(name);       \
                                                                  \
                return byName || byId;                            \
            }                                                     \
                                                                  \
            let element = findElement(\"%@\");                    \
            if(element){                                          \
                element.scrollIntoView();                         \
            }", target];
    }
    
    return command;
}

- (BOOL)canHandleAnchorLinks
{
    return SYSTEM_VERSION_LESS_THAN_OR_EQUAL_TO(10);
}

# pragma mark - Services

- (void)fetchVersionedContentWithType:(EHIWebContentType)type
{
    self.isLoading = YES;
    [[EHIServices sharedInstance] fetchContentForType:type handler:^(EHIWebContent *content, EHIServicesError *error) {
        self.isLoading = NO;
        
        if(!error.hasFailed) {
            self.htmlString = [content.body ehi_fontAdjustedHtml];
        }
    }];
}


# pragma mark - Accessors

- (NSString *)title
{
    switch(self.type) {
        case EHIWebContentTypeNone:
            return _title;
        case EHIWebContentTypePrivacy:
            return EHILocalizedString(@"privacy_policy_navigation_title", @"Privacy Policy", @"");
        case EHIWebContentTypeTermsOfUse:
            return EHILocalizedString(@"terms_of_use_navigation_title", @"Terms of Use", @"");
        case EHIWebContentTypeTermsAndConditions:
            return EHILocalizedString(@"terms_and_conditions_title", @"Terms & Conditions", @"");
        case EHIWebContentTypePrepayTermsAndConditions:
            return EHILocalizedString(@"terms_and_conditions_prepay_title", @"Prepayment Policy Terms & Conditions", @"");
        case EHIWebContentTypeLicenses:
            return EHILocalizedString(@"third_party_licenses_navigation_title", @"3rd Party Licenses", @"");
        case EHIWebContentTypeTaxes:
            return EHILocalizedString(@"class_details_taxes_fees_summary_title", @"Taxes & Fees", @"");
        case EHIWebContentTypeWeekendSpecialTermsAndConditions:
            return EHILocalizedString(@"weekend_special_terms_and_conditions_navigation_ti", @"Terms & Conditions", @"");
    }
}

@end

NS_ASSUME_NONNULL_END
