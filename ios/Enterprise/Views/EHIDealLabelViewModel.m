//
//  EHIDealLabelViewModel.m
//  Enterprise
//
//  Created by Rafael Machado on 19/06/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIDealLabelViewModel.h"
#import "EHIPromotionRenderable.h"

@interface EHIDealLabelViewModel ()
@property (strong, nonatomic) id<EHIPromotionRenderable> representable;
@end

@implementation EHIDealLabelViewModel
@synthesize delegate;

- (void)updateWithModel:(id)model
{
    if([model conformsToProtocol:@protocol(EHIPromotionRenderable)]) {
        _representable = model;
        _dealName      = [model shortTitle];
        _terms         = EHILocalizedString(@"deals_termsapply", @"Terms Apply", @"");
    }
}

# pragma mark - EHIDealInteractable

- (void)tapDeal
{
    [self.delegate show:self.representable];
}

@end
