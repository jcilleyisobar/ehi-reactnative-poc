//
//  EHITimePickerTimeCell.h
//  Enterprise
//
//  Created by mplace on 4/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHITimePickerTimeCell.h"
#import "EHITimePickerTimeViewModel.h"
#import "EHITimePickerDividerView.h"
#import "EHIRestorableConstraint.h"
#import "EHILabel.h"

@interface EHITimePickerTimeCell ()
@property (strong, nonatomic) EHITimePickerTimeViewModel *viewModel;
@property (weak, nonatomic) IBOutlet EHILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIView *afterHoursBullet;
@property (weak, nonatomic) EHITimePickerDividerView *divider;
@end

@implementation EHITimePickerTimeCell

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if (self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHITimePickerTimeViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    // make the after hours button a circle
    self.afterHoursBullet.layer.cornerRadius = self.afterHoursBullet.bounds.size.width / 2.0f;
}

#pragma mark - Reactions

- (void)registerReactions:(EHITimePickerTimeViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateBackgroundColor:)];
    [MTRReactor autorun:self action:@selector(invalidateBorderStyle:)];

    model.bind.map(@{
        source(model.title) : dest(self, .titleLabel.text),
        source(model.isAfterHours) : ^(NSNumber *isAfterHours) {
            self.afterHoursBullet.hidden = !isAfterHours.boolValue;
        }
    });
}

- (void)invalidateBackgroundColor:(MTRComputation *)computation
{
    BOOL isClosed = self.viewModel.isClosed;
    BOOL isAfterHours = self.viewModel.isAfterHours;

    self.backgroundColor = isClosed ? [UIColor ehi_grayColor1] : [UIColor whiteColor];
    self.titleLabel.textColor = !isClosed || isAfterHours ? [UIColor ehi_blackColor] : [UIColor ehi_grayColor3];
}

- (void)invalidateBorderStyle:(MTRComputation *)computation
{
    // use the solid divider if we are the last closed or open time
    self.divider.type = self.viewModel.isBoundaryTime ? EHITimePickerDividerTypeSolid : EHITimePickerDividerTypeTapered;
}

# pragma mark - Layout

+ (EHILayoutMetrics *)defaultMetrics
{
    EHILayoutMetrics *metrics = [super defaultMetrics];
    metrics.fixedSize = (CGSize){ .width = EHILayoutValueNil, .height = 50.0f };
    return metrics;
}

@end
