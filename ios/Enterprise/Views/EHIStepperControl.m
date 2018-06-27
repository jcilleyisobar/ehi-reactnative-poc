//
//  EHIReservationExtrasStepper.m
//  Enterprise
//
//  Created by fhu on 4/9/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIStepperControl.h"
#import "EHILabel.h"
#import "EHIButton.h"
#import "EHIRestorableConstraint.h"

@interface EHIStepperControl ()
@property (weak, nonatomic) IBOutlet EHILabel *countLabel;
@property (weak, nonatomic) IBOutlet EHIButton *plusButton;
@property (weak, nonatomic) IBOutlet EHIButton *minusButton;
@end

@implementation EHIStepperControl

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.count = 1;
}

- (void)disableButton:(EHIButton *)button
{
    button.isFauxDisabled = self.shouldFauxDisableButtons;
    button.userInteractionEnabled = self.shouldFauxDisableButtons;
    button.alpha = 0.4f;
}

- (void)enableButton:(EHIButton *)button
{
    button.isFauxDisabled = NO;
    button.userInteractionEnabled = YES;
    button.alpha = 1.0f;
}

- (void)invalidateTitle
{
    // if our delegate provides a title for the current count, use it
    if(self.delegate && [self.delegate respondsToSelector:@selector(stepperTitleForCount:)]) {
        self.countLabel.attributedText = [self.delegate stepperTitleForCount:self.count];
    }
    // otherwise use the default
    else {
        self.countLabel.text = [NSString stringWithFormat:@"%zd", self.count];
    }
}

#pragma mark - Actions

- (IBAction)didTapPlusButton:(id)sender
{
    [self.delegate stepper:self didUpdateValue:self.count + 1];
}

- (IBAction)didTapMinusButton:(id)sender
{
    [self.delegate stepper:self didUpdateValue:self.count - 1];
}

#pragma mark - Accessors

- (void)setCount:(NSInteger)count
{
    _count = count;
    [self invalidateTitle];
}

- (void)setPlusButtonEnabled:(BOOL)plusButtonEnabled
{
    if(plusButtonEnabled) {
        [self enableButton:self.plusButton];
    } else {
        [self disableButton:self.plusButton];
    }
}

- (void)setMinusButtonEnabled:(BOOL)minusButtonEnabled
{
    if(minusButtonEnabled) {
        [self enableButton:self.minusButton];
    } else {
        [self disableButton:self.minusButton];
    }
}

- (void)setShouldFauxDisableButtons:(BOOL)shouldFauxDisableButtons
{
    _shouldFauxDisableButtons = shouldFauxDisableButtons;

    // ensure that the setting is applied retroactively
    if(!self.plusButton.userInteractionEnabled) {
        self.plusButton.isFauxDisabled = self.shouldFauxDisableButtons;
    } else if(!self.minusButton.userInteractionEnabled) {
        self.plusButton.isFauxDisabled = self.shouldFauxDisableButtons;
    }
}

# pragma mark - Replaceability

+ (BOOL)isReplaceable
{
    return YES;
}

@end
