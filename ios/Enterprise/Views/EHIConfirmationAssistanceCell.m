//
//  EHIConfirmationAssistanceCell.m
//  Enterprise
//
//  Created by Alex Koller on 4/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIConfirmationAssistanceCell.h"
#import "EHIConfirmationAssistanceViewModel.h"
#import "EHIButton.h"

@interface EHIConfirmationAssistanceCell ()
@property (strong, nonatomic) EHIConfirmationAssistanceViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIButton *quickPickupButton;
@end

@implementation EHIConfirmationAssistanceCell

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        self.viewModel = [EHIConfirmationAssistanceViewModel new];
    }
    return self;
}

- (void)updateWithModel:(id)model metrics:(EHILayoutMetrics *)metrics
{
    [super updateWithModel:model metrics:metrics];
    
    self.quickPickupButton.showsBorder = YES;
    self.quickPickupButton.borderColor = [UIColor ehi_greenColor];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIConfirmationAssistanceViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.quickPickupTitle) : dest(self, .quickPickupButton.ehi_attributedTitle)
    });
}

# pragma mark - Actions

- (IBAction)didTapQuickPickupButton:(id)sender
{
    [self ehi_performAction:@selector(didTapQuickPickupButtonForAssistanceCell:) withSender:self];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    // height hacks because the button won't size to fit its label
    CGFloat height = self.quickPickupButton.titleLabel.bounds.size.height;
    height += EHILightPadding  * 2;
    height += EHIMediumPadding * 2;
    
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = height
    };
}

@end
