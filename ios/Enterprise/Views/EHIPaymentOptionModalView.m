//
//  EHIPaymentOptionModalView.m
//  Enterprise
//
//  Created by Rafael Ramos on 2/18/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIPaymentOptionModalView.h"
#import "EHIPaymentOptionModalViewModel.h"
#import "EHIButton.h"
#import "EHILabel.h"

@interface EHIPaymentOptionModalView ()
@property (strong, nonatomic) EHIPaymentOptionModalViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *prepayTitle;
@property (weak  , nonatomic) IBOutlet UILabel *prepayDetails;
@property (weak  , nonatomic) IBOutlet UILabel *payLaterTitle;
@property (weak  , nonatomic) IBOutlet UILabel *payLaterDetails;
@property (weak  , nonatomic) IBOutlet EHILabel *policiesLabel;
@end

@implementation EHIPaymentOptionModalView

# pragma mark - Reactions

- (void)registerReactions:(EHIPaymentOptionModalViewModel *)model
{
    [super registerReactions:model];

    model.bind.map(@{
           source(model.prepayTitle)      : dest(self, .prepayTitle.text),
           source(model.prepayDetails)    : dest(self, .prepayDetails.text),
           source(model.payLaterTitle)    : dest(self, .payLaterTitle.text),
           source(model.payLaterDetails)  : dest(self, .payLaterDetails.text),
           source(model.prepayPolicy)     : dest(self, .policiesLabel.attributedText)
    });
}

@end
