//
//  EHIEnrollmentStepTwoMatchView.m
//  Enterprise
//
//  Created by Rafael Machado on 05/01/18.
//Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIEnrollmentStepTwoMatchView.h"
#import "EHIEnrollmentStepTwoMatchViewModel.h"
#import "EHIButton.h"

@interface EHIEnrollmentStepTwoMatchView ()
@property (strong, nonatomic) EHIEnrollmentStepTwoMatchViewModel *viewModel;

@property (weak  , nonatomic) IBOutlet UILabel *stepTitleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *matchMessageLabel;
@property (weak  , nonatomic) IBOutlet UILabel *addressTitleLabel;
@property (weak  , nonatomic) IBOutlet UITextField *addressTextField;
@property (weak  , nonatomic) IBOutlet EHIButton *changeButton;
@property (weak  , nonatomic) IBOutlet EHIButton *keepButton;
@end

@implementation EHIEnrollmentStepTwoMatchView

# pragma mark - Subclassing Hooks

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIEnrollmentStepTwoMatchViewModel new];
    }
    
    return self;
}

- (void)updateWithModel:(EHIEnrollmentStepTwoMatchViewModel *)model
{
    [super updateWithModel:model];
    
    self.viewModel = model;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.addressTextField.userInteractionEnabled = NO;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIEnrollmentStepTwoMatchViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.stepTitle)        : dest(self, .stepTitleLabel.text),
        source(model.matchMessage)     : dest(self, .matchMessageLabel.text),
        source(model.addressTitle)     : dest(self, .addressTitleLabel.text),
        source(model.formattedAddress) : dest(self, .addressTextField.text),
        source(model.changeTitle)      : dest(self, .changeButton.ehi_title),
        source(model.keepTitle)        : dest(self, .keepButton.ehi_title),
    });
}

# pragma mark - Actions

- (IBAction)didTapChange:(EHIButton *)sender
{
    [self.viewModel didTapChange];
    
    [self ehi_performAction:@selector(enrollmentStepTwoMatchDidTapChange) withSender:self];
}

- (IBAction)didTapKeep:(EHIButton *)sender
{
    [self.viewModel didTapKeep];
    
    [self ehi_performAction:@selector(enrollmentStepTwoMatchDidTapKeep) withSender:self];
}

# pragma mark - Replaceability

+ (BOOL)isReplaceable
{
    return YES;
}

@end
