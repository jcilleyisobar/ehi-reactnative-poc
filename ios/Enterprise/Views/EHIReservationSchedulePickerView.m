//
//  EHIReservationScheduleView.m
//  Enterprise
//
//  Created by Ty Cobb on 3/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationSchedulePickerView.h"
#import "EHIReservationSchedulePickerViewModel.h"
#import "EHIReservationSchedulePickerStepCell.h"
#import "EHIReservationBuilder.h"

@interface EHIReservationSchedulePickerView ()
@property (strong, nonatomic) EHIReservationSchedulePickerViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *pickupLabel;
@property (weak  , nonatomic) IBOutlet UILabel *returnLabel;
@property (strong, nonatomic) IBOutletCollection(EHIReservationSchedulePickerStepCell) NSArray *cells;
@property (weak, nonatomic) IBOutlet EHIReservationSchedulePickerStepCell *pickupTimeContainer;
@property (weak, nonatomic) IBOutlet EHIReservationSchedulePickerStepCell *returnTimeContainer;
@end

@implementation EHIReservationSchedulePickerView

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        self.viewModel = [EHIReservationSchedulePickerViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
  
    self.cells.each(^(EHIReservationSchedulePickerStepCell *cell) {
        // update each cell with the tagged view model
        [cell updateWithModel:self.viewModel.steps[cell.tag]];
    });
}

# pragma mark - Reactions

- (void)registerReactions:(EHIReservationSchedulePickerViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.pickupTitle) : dest(self, .pickupLabel.text),
        source(model.returnTitle) : dest(self, .returnLabel.text),
    });
}

# pragma mark - Setters

- (void)setHidesTime:(BOOL)hidesTime
{
    _hidesTime = hidesTime;
    
    if(_hidesTime) {
        [self.pickupTimeContainer removeFromSuperview];
        [self.returnTimeContainer removeFromSuperview];
        [self layoutIfNeeded];
    }
}

# pragma mark - EHIView

+ (BOOL)isReplaceable
{
    return YES;
}

@end
