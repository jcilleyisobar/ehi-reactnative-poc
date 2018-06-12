//
//  EHIConfirmationContractCancelModal.m
//  Enterprise
//
//  Created by Rafael Ramos on 5/20/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIConfirmationContractCancelModal.h"
#import "EHIConfirmationContractCancelModalViewModel.h"
#import "EHIContractNotificationView.h"

@interface EHIConfirmationContractCancelModal ()
@property (strong, nonatomic) EHIConfirmationContractCancelModalViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *subtitleLabel;
@property (weak  , nonatomic) IBOutlet EHIContractNotificationView *contractView;
@property (weak  , nonatomic) IBOutlet UIView *containerView;
@end

@implementation EHIConfirmationContractCancelModal

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIConfirmationContractCancelModalViewModel new];
    }

    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIConfirmationContractCancelModalViewModel *)model
{
    [super registerReactions:model];

    model.bind.map(@{
        source(model.subtitle)      : dest(self, .subtitleLabel.text),
        source(model.contractModel) : dest(self, .contractView.viewModel)
    });
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.containerView.frame)
    };
}

@end
