//
//  EHITermsViewModel.m
//  Enterprise
//
//  Created by Ty Cobb on 6/25/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHITermsViewModel.h"
#import "EHIServices+Config.h"

@interface EHITermsViewModel ()
@property (assign, nonatomic) BOOL isLoading;
@property (strong, nonatomic) EHIWebContent *terms;
@end

@implementation EHITermsViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title       = EHILocalizedString(@"terms_and_conditions_updated_title", @"Terms and Conditions", @"Title for the T&C modal");
        _acceptTitle = EHILocalizedString(@"terms_and_conditions_accept_title", @"ACCEPT", @"Title for the T&C accept button");
        _isLoading   = YES;
    }
    
    return self;
}

- (instancetype)initWithContentString:(NSString *)content
{
    if(self = [self initWithModel:[EHIModel placeholder]]) {
        _terms = [EHIWebContent webContentWithBody:content];
        _title = EHILocalizedString(@"reservation_policy_terms_and_conditions_title", @"TERMS OF USE", @"");
    }
    return self;
}

- (void)didInitialize
{
    [super didInitialize];
    
    if (self.terms) {
        self.isLoading = NO;
        return;
    }
    
    [[EHIServices sharedInstance] fetchContentForType:EHIWebContentTypeTermsAndConditions handler:^(EHIWebContent *terms, EHIServicesError *error) {
        self.isLoading = NO;
        if(!error.hasFailed) {
            self.terms = terms;
        }
    }];
}

# pragma mark - Action

- (void)acceptTerms:(BOOL)didAccept
{
    // dismiss the terms vc
    self.router
        .transition.dismiss.start(nil);
  
    // call the handler and destroy it
    ehi_call(self.handler)(didAccept ? self.terms.version : nil, didAccept);
    self.handler = nil;
}

# pragma mark - Accessors

- (NSString *)termsContent
{
    return [self.terms.body ehi_fontAdjustedHtml];
}

# pragma mark - MTRReactive

+ (NSArray *)nonreactiveProperties:(EHITermsViewModel *)object
{
    return @[
        @key(object.handler),
    ];
}

@end
