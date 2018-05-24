//
//  EHIRedemptionPickerCell.m
//  Enterprise
//
//  Created by mplace on 8/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIRedemptionPickerCell.h"
#import "EHIRedemptionPickerViewModel.h"
#import "EHIStepperControl.h"

@interface EHIRedemptionPickerCell () <EHIStepperControlDelegate>
@property (strong, nonatomic) EHIRedemptionPickerViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *contentContainer;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *subtitleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *footerLabel;
@property (weak  , nonatomic) IBOutlet EHIStepperControl *stepper;
@end

@implementation EHIRedemptionPickerCell

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIRedemptionPickerViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    [self.stepper setShouldFauxDisableButtons:YES];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIRedemptionPickerViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.title)       : dest(self, .titleLabel.text),
        source(model.subtitle)    : dest(self, .subtitleLabel.text),
        source(model.footerTitle) : dest(self, .footerLabel.attributedText),
        source(model.plusButtonEnabled)  : dest(self, .stepper.plusButtonEnabled),
        source(model.minusButtonEnabled) : dest(self, .stepper.minusButtonEnabled),
        source(model.daysRedeemed)       : dest(self, .stepper.count)
    });
}

# pragma mark - EHIStepperControlDelegate

- (NSAttributedString *)stepperTitleForCount:(NSInteger)count
{
    return [self.viewModel stepperTitleForDaysRedeemed:count];
}

- (void)stepper:(EHIStepperControl *)stepper didUpdateValue:(NSInteger)integer
{
    self.viewModel.daysRedeemed = integer;
    
    if(self.viewModel.daysRedeemed == integer) {
        [self ehi_performAction:@selector(redemptionPickerDidUpdateDaysRedeemed:) withSender:self];
    }
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    CGRect frame = [self.contentContainer convertRect:self.contentContainer.bounds toView:self];
    
    return (CGSize) {
        .width = EHILayoutValueNil,
        .height = CGRectGetMaxY(frame) + EHIHeaviestPadding
    };
}

@end
