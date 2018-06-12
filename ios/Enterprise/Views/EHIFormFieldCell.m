//
//  EHIFormFieldCell.m
//  Enterprise
//
//  Created by Alex Koller on 5/1/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIFormFieldCell.h"
#import "EHIFormFieldViewModel.h"
#import "EHIFormFieldLabelCell.h"
#import "EHIFormFieldBasicProfileCell.h"
#import "EHIFormFieldTextCell.h"
#import "EHIFormFieldTextToggleCell.h"
#import "EHIFormFieldTextViewCell.h"
#import "EHIFormFieldToggleCell.h"
#import "EHIFormFieldDropdownCell.h"
#import "EHIFormFieldDateCell.h"
#import "EHIFormFieldDateMonthYearCell.h"
#import "EHILabel.h"
#import "EHIRestorableConstraint.h"
#import "EHIFormFieldButtonCell.h"
#import "EHIFormFieldActionButtonCell.h"

#define EHIFormFieldNoTitleTopSpacing (10.0)

@interface EHIFormFieldCell ()
@property (strong, nonatomic) EHIFormFieldViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *contentContainer;
@property (weak  , nonatomic) IBOutlet UIView *controlContainer;
@property (weak  , nonatomic) IBOutlet EHILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet EHILabel *subtitleLabel;
@property (weak  , nonatomic) IBOutlet UIView *inputField;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *containerTopSpacing;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *titleHeightConstraint;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *subtitleHeightConstraint;
@end

@implementation EHIFormFieldCell

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.controlContainer.layer.borderColor = [UIColor ehi_redColor].CGColor;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIFormFieldViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateTitle:)];
    [MTRReactor autorun:self action:@selector(invalidateConstraints:)];
    [MTRReactor autorun:self action:@selector(invalidateErrorVisibility:)];
    
    model.bind.map(@{
        source(model.subtitle)     : dest(self, .subtitleLabel.text),
        source(model.isUneditable) : ^(NSNumber *uneditable) {
            self.inputField.userInteractionEnabled = ![uneditable boolValue];
        }
    });
}

- (void)invalidateTitle:(MTRComputation *)computation
{
    NSString *title = self.viewModel.title;
    NSAttributedString *attributedTitle = self.viewModel.attributedTitle;
    
    if(title) {
        self.titleLabel.text = title;
    } else if(attributedTitle) {
        self.titleLabel.attributedText = attributedTitle;
    } else {
        self.titleLabel.text = nil;
    }
}

- (void)invalidateConstraints:(MTRComputation *)computation
{
    depend(self.viewModel.hidesTitle);
    depend(self.viewModel.hidesSubtitle);
    
    [self setNeedsUpdateConstraints];
}

- (void)invalidateErrorVisibility:(MTRComputation *)computation
{
    BOOL showsError = self.viewModel.showsError;
    
    self.controlContainer.layer.borderWidth = showsError ? 1.0f : 0.0f;
}

# pragma mark - Override

- (void)updateConstraints
{
    [super updateConstraints];
    
    self.titleHeightConstraint.isDisabled    = self.viewModel.hidesTitle;
    self.subtitleHeightConstraint.isDisabled = self.viewModel.hidesSubtitle;
    self.containerTopSpacing.constant        = self.viewModel.hidesTitle ? EHIFormFieldNoTitleTopSpacing : EHIRestorableConstant;
}

- (BOOL)canBecomeFirstResponder
{
    return !self.viewModel.isUneditable;
}

- (BOOL)resignFirstResponder
{
    BOOL didResign = [self.inputField resignFirstResponder];
    
    if(didResign) {
        [self ehi_performAction:@selector(didResignFirstResponderForCell:) withSender:self];
    }
    
    return didResign || [super resignFirstResponder];
}

- (void)setIsLastInSection:(BOOL)isLastInSection
{
    // compute next button on the keyboard, based on the section
    // skip if a custom one was set in the viewmodel
    BOOL customReturnKey = self.viewModel.returnKeyType != UIReturnKeyDefault;
    if(!customReturnKey) {
        self.viewModel.returnKeyType = isLastInSection ? UIReturnKeyDone : UIReturnKeyNext;
    }
    
    [super setIsLastInSection:!self.viewModel.isLastInGroup || isLastInSection];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    UIView *bottomView   = self.viewModel.isLastInGroup ? self.divider : self.contentContainer;
    CGRect bottomFrame   = [bottomView convertRect:bottomView.bounds toView:self.contentView];
    CGFloat extraPadding = self.viewModel.extraPadding;
    return (CGSize) {
        .width = EHILayoutValueNil,
        .height = CGRectGetMaxY(bottomFrame) + extraPadding
    };
}

# pragma mark - Factory

+ (Class<EHIListCell>)subclassForModel:(EHIFormFieldViewModel *)model
{
    switch (model.type) {
        case EHIFormFieldTypeLabel:
            return EHIFormFieldLabelCell.class;
        case EHIFormFieldTypeButton:
            return EHIFormFieldButtonCell.class;
        case EHIFormFieldTypeActionButton:
            return EHIFormFieldActionButtonCell.class;
        case EHIFormFieldTypeBasicProfile:
            return EHIFormFieldBasicProfileCell.class;
        case EHIFormFieldTypeText:
            return EHIFormFieldTextCell.class;
        case EHIFormFieldTypeTextToggle:
            return EHIFormFieldTextToggleCell.class;
        case EHIFormFieldTypeTextView:
            return EHIFormFieldTextViewCell.class;
        case EHIFormFieldTypeToggle:
            return EHIFormFieldToggleCell.class;
        case EHIFormFieldTypeDropdown:
            return EHIFormFieldDropdownCell.class;
        case EHIFormFieldTypeDate:
            return EHIFormFieldDateCell.class;
        case EHIFormFieldTypeDateMonthYear:
            return EHIFormFieldDateMonthYearCell.class;
        default:
            [NSException raise:@"Invalid form type" format:@"EHIFormFieldType cannot be EHIFormFieldTypeUnknown"];
            return self;
    }
}

+ (NSArray *)potentialSubclasses
{
    return @[
        [EHIFormFieldLabelCell class],
        [EHIFormFieldButtonCell class],
        [EHIFormFieldActionButtonCell class],
        [EHIFormFieldBasicProfileCell class],
        [EHIFormFieldTextCell class],
        [EHIFormFieldTextToggleCell class],
        [EHIFormFieldTextViewCell class],
        [EHIFormFieldToggleCell class],
        [EHIFormFieldDropdownCell class],
        [EHIFormFieldDateCell class],
        [EHIFormFieldDateMonthYearCell class],
    ];
}

@end
