//
//  EHIRentalsUpcomingRentalCell.m
//  Enterprise
//
//  Created by fhu on 4/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIRentalsUpcomingRentalCell.h"
#import "EHIRentalsUpcomingRentalViewModel.h"
#import "EHILabel.h"

@interface EHIRentalsUpcomingRentalCell()
@property (weak, nonatomic) IBOutlet UIView *containerView;
@property (weak, nonatomic) IBOutlet EHILabel *dateLabel;
@property (weak, nonatomic) IBOutlet EHILabel *locationLabel;
@property (weak, nonatomic) IBOutlet EHILabel *confirmationLabel;
@property (weak, nonatomic) IBOutlet UIImageView *arrow;
@end

@implementation EHIRentalsUpcomingRentalCell

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIRentalsUpcomingRentalViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIRentalsUpcomingRentalViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.reservationTime)    : dest(self, .dateLabel.text),
        source(model.location)           : dest(self, .locationLabel.text),
        source(model.confirmationNumber) : dest(self, .confirmationLabel.text),
        source(model.shouldHideArrow)    : dest(self, .arrow.hidden)
    });
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize){
        .width = EHILayoutValueNil,
        .height = self.containerView.frame.size.height
    };
}

@end
