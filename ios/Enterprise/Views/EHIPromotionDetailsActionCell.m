//
//  EHIPromotionDetailsActionCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 3/28/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIPromotionDetailsActionCell.h"
#import "EHIPromotionDetailsActionCellViewModel.h"
#import "EHIButton.h"

@interface EHIPromotionDetailsActionCell ()
@property (strong, nonatomic) EHIPromotionDetailsActionCellViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIButton *startReservationButton;
@property (weak  , nonatomic) IBOutlet UIView *footerDivider;
@end

@implementation EHIPromotionDetailsActionCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIPromotionDetailsActionCellViewModel new];
    }

    return self;
}

- (IBAction)didTapStartReservation:(id)sender
{
    [self ehi_performAction:@selector(didTapWeekendSpecialStartReservation) withSender:self];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIPromotionDetailsActionCellViewModel *)model
{
    [super registerReactions:model];

    model.bind.map(@{
        source(model.reservationButtonTitle) : dest(self, .startReservationButton.ehi_title)
    });
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.footerDivider.frame)
    };
}

@end
