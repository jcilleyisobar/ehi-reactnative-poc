//
//  EHIDealsViewModel.m
//  Enterprise
//
//  Created by Rafael Machado on 19/06/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIDealsViewModel.h"
#import "EHIDealsConfiguration.h"
#import "EHIDealCardViewModel.h"
#import "EHIDealLabelViewModel.h"
#import "EHIDealHeaderViewModel.h"
#import "EHIDealInterfaces.h"

@interface EHIDealsViewModel () <EHIDealActionable>
@property (strong, nonatomic) id<EHIPromotionRenderable> weekend;
@property (strong, nonatomic) EHIDeals *local;
@property (strong, nonatomic) EHIDeals *internacional;
@property (strong, nonatomic) EHIDeals *other;
@end

@implementation EHIDealsViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title = EHILocalizedString(@"deals_page_title", @"Deals", @"");
    }
    
    return self;
}

- (void)updateWithModel:(id)model
{
    if([model isKindOfClass:EHIDealsConfiguration.class]) {
        self.configuration = model;
    }
}

# pragma mark - Accessors

- (void)setConfiguration:(EHIDealsConfiguration *)configuration
{
    self.weekend        = configuration.weekendSpecial;
    self.local          = configuration.local;
    self.internacional  = configuration.internacional;
    self.other          = configuration.other;
}

- (void)setWeekend:(id<EHIPromotionRenderable>)weekend
{
    if(weekend) {
        _weekendSpecial = [self cardModelWithRenderable:weekend];
    }
}

- (void)setLocal:(EHIDeals *)local
{
    _local = local;
    
    _localDeals = (local.deals ?: @[]).map(^(EHIDealInfo *deal){
        return [self cardModelWithRenderable:deal];
    });
}

- (void)setInternacional:(EHIDeals *)internacional
{
    _internacional = internacional;
    
    _internacionalDeals = (internacional.deals ?: @[]).map(^(EHIDealInfo *deal){
        return [self labelModelWithRenderable:deal];
    });
}

- (EHIDealHeaderViewModel *)headerForSection:(EHIDealsSection)section
{
    if(section == EHIDealsSectionInternacional) {
        return [EHIDealHeaderViewModel modelWithTitle:self.internacional.displayName];
    }
    
    return nil;
}

# pragma mark - EHIDealActionable

- (void)show:(id<EHIPromotionRenderable>)renderable
{
    if(renderable) {
        self.router.transition.push(EHIScreenDealDetails).object(renderable).start(nil);
    }
}

//
// Helpers
//

- (EHIDealCardViewModel *)cardModelWithRenderable:(id<EHIPromotionRenderable>)renderable
{
    EHIDealCardViewModel *model = [EHIDealCardViewModel modelWithRenderable:renderable layout:EHIDealLayoutList];
    model.delegate = self;
    
    return model;
}

- (EHIDealLabelViewModel *)labelModelWithRenderable:(id<EHIPromotionRenderable>)renderable
{
    EHIDealLabelViewModel *model = [[EHIDealLabelViewModel alloc] initWithModel:renderable];
    model.delegate = self;
    
    return model;
}

@end
