//
//  EHIReservationScheduleCell.m
//  Enterprise
//
//  Created by Alex Koller on 4/3/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationScheduleCell.h"
#import "EHIReservationScheduleCellViewModel.h"
#import "EHIReservationScheduleView.h"
#import "EHIReservationScheduleViewModel.h"
#import "EHIReservationViewStyle.h"

@interface EHIReservationScheduleCell ()
@property (strong, nonatomic) EHIReservationScheduleCellViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *containerView;
@property (weak  , nonatomic) IBOutlet UIView *topDivider;
@property (weak  , nonatomic) IBOutlet EHIReservationScheduleView *pickScheduleView;
@property (weak  , nonatomic) IBOutlet EHIReservationScheduleView *returnScheduleView;
@end

@implementation EHIReservationScheduleCell

- (void)updateWithModel:(id)model metrics:(EHILayoutMetrics *)metrics
{
    [super updateWithModel:model metrics:metrics];

    [self.pickScheduleView updateWithModel:self.viewModel.pickupDate];
    self.pickScheduleView.type = EHIReservationScheduleViewTypePickup;
    self.pickScheduleView.isEditable = metrics.tag != EHIReservationViewStyleConfirmation;
    
    [self.returnScheduleView updateWithModel:self.viewModel.returnDate];
    self.returnScheduleView.type = EHIReservationScheduleViewTypeReturn;
    self.returnScheduleView.isEditable = metrics.tag != EHIReservationViewStyleConfirmation;
    
    self.topDivider.hidden = !self.viewModel.showTopDivider;
}

#pragma mark - Metrics

- (CGSize)intrinsicContentSize
{
    return (CGSize){
        .width = EHILayoutValueNil,
        .height = CGRectGetHeight(self.containerView.bounds)
    };
}

@end
