//
//  EHIContractDiscountView.m
//  Enterprise
//
//  Created by Rafael Machado on 5/27/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIContractDiscountView.h"
#import "EHIContractDiscountViewModel.h"
#import "EHIButton.h"
#import "EHIRestorableConstraint.h"

@interface EHIContractDiscountView ()
@property (strong, nonatomic) EHIContractDiscountViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *textContainerView;
@property (weak  , nonatomic) IBOutlet UIImageView *iconImage;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UIView  *termsContainer;
@property (weak  , nonatomic) IBOutlet EHIButton *termsButton;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *termsTopConstraint;
@end

@implementation EHIContractDiscountView

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIContractDiscountViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.backgroundColor = [UIColor ehi_tanColor];
}

# pragma mark - Accessibility

- (void)registerAccessibilityIdentifiers
{
    [super registerAccessibilityIdentifiers];
    
    self.titleLabel.accessibilityIdentifier = EHIReservationDiscountNameKey;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIContractDiscountViewModel *)model
{
    [super registerReactions:model];
    
    // generate the title from multiple view model properties
    [MTRReactor autorun:self action:@selector(invalidateTerms:)];
    
    model.bind.map(@{
        source(model.iconName)          : dest(self, .iconImage.ehi_imageName),
        source(model.termsButtonTitle)  : dest(self, .termsButton.ehi_title),
        source(model.title)             : dest(self, .titleLabel.attributedText)
    });
}

- (void)invalidateTerms:(MTRComputation *)computation
{
    MASLayoutPriority constraintPriority = self.viewModel.shouldShowTerms ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    
    [self.termsContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@(0.0f)).with.priority(constraintPriority);
    }];
}

# pragma mark - Actions

- (IBAction)didTapPolicies:(id)sender
{
    [self.viewModel didTapPolicies];
}

# pragma mark - Layout

+ (BOOL)isReplaceable
{
    return YES;
}

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetHeight(self.textContainerView.frame) + EHIHeavyPadding
    };
}

@end
