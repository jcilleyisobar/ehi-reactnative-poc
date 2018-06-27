//
//  EHIReservationScheduleView.m
//  Enterprise
//
//  Created by Alex Koller on 4/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationScheduleView.h"
#import "EHIReservationScheduleViewModel.h"

@interface EHIReservationScheduleView ()
@property (strong, nonatomic) EHIReservationScheduleViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *selectedTimeLabel;
@property (weak  , nonatomic) IBOutlet UILabel *selectedDateTitleLabel;
@property (weak  , nonatomic) IBOutlet UIImageView *accessoryImageView;
@end

@implementation EHIReservationScheduleView

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIReservationScheduleViewModel new];
        self.isEditable = YES;
    }
    return self;
}

- (void)registerReactions:(EHIReservationScheduleViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.viewTitle) : dest(self, .titleLabel.text),
        source(model.dateTitle) : dest(self, .selectedDateTitleLabel.text),
        source(model.timeTitle) : dest(self, .selectedTimeLabel.text),
    });
}

# pragma mark - Computed Properties

- (EHIReservationScheduleViewType)type
{
    return self.viewModel.type;
}

# pragma mark - Setters

- (void)setType:(EHIReservationScheduleViewType)type
{
    self.viewModel.type = type;
}

- (void)setIsEditable:(BOOL)isEditable
{
    _isEditable = isEditable;
    
    self.accessoryImageView.hidden = !isEditable;
}

# pragma mark - Replacement

+ (BOOL)isReplaceable
{
    return YES;
}

@end
