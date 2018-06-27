//
//  EHIViewModel.m
//  Enterprise
//
//  Created by Ty Cobb on 1/9/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIViewModel_CountrySpecific.h"
#import "EHIReservationBuilder.h"
#import "Reactor.h"

@implementation EHIViewModel

+ (void)initialize
{
    [super initialize];
    
    [MTRReactor reactify:self.class];
}

- (instancetype)init
{
    return [self initWithModel:nil];
}

- (instancetype)initWithModel:(id)model
{
    if(self = [super init]) {
        [self updateWithModel:model];
        
        dispatch_async(dispatch_get_main_queue(), ^{
            [self didInitialize];
        });
    }
    
    return self;
}

- (void)updateWithModel:(id)model { }

# pragma mark - Actions

- (id)takeOwnership
{
    // clean up any lingering reactions
    self.isActive = NO;
    // re-tokenize
    self.ownershipToken = [NSUUID UUID];
    
    return self.ownershipToken;
}

- (void)navigateBack
{
    self.router.transition.pop(1).start(nil);
}

# pragma mark - Setters

- (void)setIsActive:(BOOL)isActive
{
    if(_isActive == isActive) {
        // when an interactive transition is canceled, the view controller needs to activate the view model, again.
        // so, we have to make sure to always call -didBecomeActive when isActive is true
        if(isActive) {
            [self didBecomeActive];
        }
        return;
    }
   
    _isActive = isActive;
   
    if(isActive) {
        [self didBecomeActive];
    } else {
        [self didResignActive];
    }
}

# pragma mark - Lifecycle

- (void)didInitialize { }
- (void)didBecomeActive { }

- (void)didResignActive
{
    // throw away our dependencies when becoming inactive
    [self destroyDependencies];
}

# pragma mark - Accessors

- (EHIRouter *)router
{
    return [EHIMainRouter currentRouter];
}

# pragma mark - Bindings

- (EHIBinder *)bind
{
    return [[EHIBinder alloc] initWithSource:self];
}

- (EHICarClassChargeType)defaultPayment
{
    return [NSLocale ehi_shouldDefaultToPayLater] ? EHICarClassChargeTypePayLater : EHICarClassChargeTypePrepay;
}

@end

@implementation EHIViewModel (Analytics)

- (void)invalidateAnalyticsContext
{
    [self updateAnalyticsContext:[EHIAnalytics context]];
}

- (void)updateAnalyticsContext:(EHIAnalyticsContext *)context
{
    
}

@end
