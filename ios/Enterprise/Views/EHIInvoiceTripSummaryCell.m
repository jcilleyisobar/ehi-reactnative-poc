//
//  EHIInvoiceTripSummaryCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 7/19/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIInvoiceTripSummaryCell.h"
#import "EHIInvoiceTripSummaryViewModel.h"
#import "EHILabel.h"
#import "EHIButton.h"

@interface EHIInvoiceTripSummaryCell ()
@property (strong, nonatomic) EHIInvoiceTripSummaryViewModel *viewModel;

@property (weak, nonatomic) IBOutlet UIView *contentContainer;

@property (weak, nonatomic) IBOutlet EHILabel *pickupTitleLabel;
@property (weak, nonatomic) IBOutlet EHILabel *pickupDateLabel;
@property (weak, nonatomic) IBOutlet EHILabel *pickupLocationLabel;
@property (weak, nonatomic) IBOutlet EHILabel *pickupCityLabel;
@property (weak, nonatomic) IBOutlet EHIButton *pickupPhoneButton;
@property (weak, nonatomic) IBOutlet EHILabel *returnTitleLabel;
@property (weak, nonatomic) IBOutlet EHILabel *returnDateLabel;
@property (weak, nonatomic) IBOutlet EHILabel *returnLocationLabel;
@property (weak, nonatomic) IBOutlet EHILabel *returnCityLabel;
@property (weak, nonatomic) IBOutlet EHIButton *returnPhoneButton;
@property (weak, nonatomic) IBOutlet EHILabel *totalTitleLabel;
@property (weak, nonatomic) IBOutlet EHILabel *totalLabel;
@property (weak, nonatomic) IBOutlet EHILabel *pointsTitleLabel;
@property (weak, nonatomic) IBOutlet EHILabel *pointsLabel;
@property (weak, nonatomic) IBOutlet UIView *pointsContainer;
@property (weak, nonatomic) IBOutlet UIView *totalContainer;
@property (weak, nonatomic) IBOutlet UIView *totalBottomDivider;
@end

@implementation EHIInvoiceTripSummaryCell

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        self.viewModel = [EHIInvoiceTripSummaryViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIInvoiceTripSummaryViewModel *)model
{
    [super registerReactions:model];

    [MTRReactor autorun:self action:@selector(invalidatePoints:)];
    
    model.bind.map(@{
        source(model.pickupTitle)      : dest(self, .pickupTitleLabel.text),
        source(model.pickupDate)       : dest(self, .pickupDateLabel.text),
        source(model.pickupLocation)   : dest(self, .pickupLocationLabel.text),
        source(model.pickupCity)       : dest(self, .pickupCityLabel.text),
        source(model.pickupPhone)      : dest(self, .pickupPhoneButton.ehi_title),
        source(model.returnTitle)      : dest(self, .returnTitleLabel.text),
        source(model.returnDate)       : dest(self, .returnDateLabel.text),
        source(model.returnLocation)   : dest(self, .returnLocationLabel.text),
        source(model.returnCity)       : dest(self, .returnCityLabel.text),
        source(model.returnPhone)      : dest(self, .returnPhoneButton.ehi_title),
        source(model.totalTitle)       : dest(self, .totalTitleLabel.text),
        source(model.totalPrice)       : dest(self, .totalLabel.text),
        source(model.pointsTitle)      : dest(self, .pointsTitleLabel.text),
        source(model.points)           : dest(self, .pointsLabel.text),
    });
}

- (void)invalidatePoints:(MTRComputation *)computation
{
    BOOL showPoints = self.viewModel.showPoints;
    MASLayoutPriority constraintPriority = showPoints ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    
    [self.pointsContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@(0.0f)).with.priority(constraintPriority);
    }];
    
    self.totalBottomDivider.hidden = showPoints;
}

# pragma mark - Actions

- (IBAction)didTapReturnPhone:(EHIButton *)sender
{
    [self.viewModel promptCallTo:EHIInvoiceTripSummaryPhoneReturnLocation];
}

- (IBAction)didTapPickupPhone:(EHIButton *)sender
{
    [self.viewModel promptCallTo:EHIInvoiceTripSummaryPhonePickupLocation];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    UIView *view = self.viewModel.showPoints ? self.pointsContainer : self.totalContainer;
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(view.frame)
    };
}

@end
