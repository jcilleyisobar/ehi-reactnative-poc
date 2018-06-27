//
//  EHIDealDetailViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 06/06/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIModel+Mock.h"
#import "EHIDealDetailViewModel.h"
#import "EHIDealCardViewModel.h"
#import "EHIDealContentViewModel.h"
#import "EHISectionHeaderModel.h"
#import "EHIServices+Contracts.h"
#import "EHIReservationBuilder.h"
#import "EHIToastManager.h"

@interface EHIDealDetailViewModel ()
@property (assign, nonatomic) BOOL isLoading;
@property (strong, nonatomic) id<EHIPromotionRenderable> renderable;
@property (strong, nonatomic) EHIDealCardViewModel *dealModel;
@property (strong, nonatomic) EHIDealContentViewModel *descriptionModel;
@property (strong, nonatomic) EHIDealContentViewModel *termsModel;
@end

@implementation EHIDealDetailViewModel

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    if([model conformsToProtocol:@protocol(EHIPromotionRenderable)]) {
        self.renderable = model;
    }
}

- (NSAttributedString *)bookTitle
{
    EHIAttributedStringBuilder *builder = EHIAttributedStringBuilder.new
        .appendText(EHILocalizedString(@"deals_book_cta", @"BOOK NOW", @"").uppercaseString)
        .fontStyle(EHIFontStyleBold, 18.0f)
        .color(UIColor.whiteColor);
    
    if(!self.renderable.cid) {
        builder
            .newline
            .space
            .appendText(EHILocalizedString(@"deals_nocid_cta", @"Promotion code is not needed for this deal", @"").uppercaseString)
            .fontStyle(EHIFontStyleLight, 12.0f);
    }
    
    return builder.string;
}

- (EHISectionHeaderModel *)headerForSection:(EHIDealDetailSection)section
{
    if(section == EHIDealDetailSectionTerms) {
        return [EHISectionHeaderModel modelWithTitle:EHILocalizedString(@"eu_terms_screen_title", @"", @"")];
    }
    
    return nil;
}

# pragma mark - Actions

- (void)bookNow
{
    NSString *promoCode = self.renderable.cid;
    
    [EHIReservationBuilder sharedInstance].discountCode = promoCode;
    
    dispatch_after_seconds(0.3, ^{
        self.router.transition
            .push(EHIScreenLocations)
            .start(nil);
        
        if(promoCode) {
            [EHIToastManager showMessage:EHILocalizedString(@"deals_toast", @"Promotion code has been applied", @"")];
        }
    });
}

# pragma mark - Accessors

- (void)setRenderable:(id<EHIPromotionRenderable>)renderable
{
    _renderable = renderable;
    
    self.dealModel  = [EHIDealCardViewModel modelWithRenderable:self.renderable layout:EHIDealLayoutDetail];
    
    [self setupLongDescription];
    [self setupTermsAndConditions];
}

- (NSString *)title
{
    return EHILocalizedString(@"deals_page_title", @"Deals", @"");
}

# pragma mark - EHIDealActionable

- (void)show:(id<EHIPromotionRenderable>)renderable
{
    
}

//
// Helpers
//

- (void)setupLongDescription
{
    NSString *longDescription = self.renderable.longDescription;
    
    if(self.renderable.cid == nil) {
        NSString *noCid = EHILocalizedString(@"deals_nocid", @"No promotion code is needed for this deal. You'll automatically get it just by following the details above.", @"");
        longDescription = [longDescription stringByAppendingFormat:@"<br/>%@", noCid];
    }
    
    self.descriptionModel = [[EHIDealContentViewModel alloc] initWithModel:longDescription];
}

- (void)setupTermsAndConditions
{
    id<EHIPromotionRenderable> renderable = self.renderable;
    BOOL shouldFetch = renderable.cid != nil;
    if(shouldFetch) {
        [self fetchTerms:renderable];
    } else if(renderable.terms) {
        self.termsModel = [[EHIDealContentViewModel alloc] initWithModel:renderable.terms];
    }
}

- (void)fetchTerms:(id<EHIPromotionRenderable>)renderable
{
    self.isLoading = YES;
    [[EHIServices sharedInstance] fetchContractNumber:renderable.cid handler:^(EHIContractDetails *contract, EHIServicesError *error) {
        self.isLoading = NO;
        
        [error consume];
        
        if(contract.termsAndConditions) {
            self.termsModel = [[EHIDealContentViewModel alloc] initWithModel:contract.termsAndConditions];
        }
    }];
}

@end
