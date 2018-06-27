//
//  EHIProfilePaymentAddCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 9/23/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIProfilePaymentAddCell.h"
#import "EHIProfilePaymentAddViewModel.h"
#import "EHIButton.h"
#import "EHIRestorableConstraint.h"

@interface EHIProfilePaymentAddCell ()
@property (strong, nonatomic) EHIProfilePaymentAddViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *topDivider;
@property (weak  , nonatomic) IBOutlet EHIButton *addButton;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *topConstraint;
@end

@implementation EHIProfilePaymentAddCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIProfilePaymentAddViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIProfilePaymentAddViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.title)       : dest(self, .addButton.ehi_title),
        source(model.topSpacing)  : dest(self, .topConstraint.constant),
        source(model.hideDivider) : dest(self, .topDivider.hidden)
    });
}

- (IBAction)didTapAddButton:(id)sender
{
    [self ehi_performAction:@selector(didTapAddCreditCard:) withSender:self];
}

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.addButton.frame) + EHIMediumPadding
    };
}

@end
