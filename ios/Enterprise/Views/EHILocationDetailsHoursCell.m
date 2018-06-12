//
//  EHILocationDetailsHoursCell.m
//  Enterprise
//
//  Created by Ty Cobb on 2/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationDetailsHoursCell.h"
#import "EHILocationDetailsHoursViewModel.h"

@interface EHILocationDetailsHoursCell ()
@property (strong, nonatomic) EHILocationDetailsHoursViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *dateLabel;
@property (weak  , nonatomic) IBOutlet UILabel *timeLabel;
@end

@implementation EHILocationDetailsHoursCell

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        self.viewModel = [EHILocationDetailsHoursViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHILocationDetailsHoursViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(updateFontStyles:)];
    
    model.bind.map(@{
        source(model.date) : dest(self, .dateLabel.text),
        source(model.time) : dest(self, .timeLabel.text),
    });
}

- (void)updateFontStyles:(MTRComputation *)computation
{
    // bold the font if this is today's time
    EHIFontStyle fontStyle = self.viewModel.isToday ? EHIFontStyleBold : EHIFontStyleRegular;
   
    // update the fonts of both labels
    UIFont *font = [UIFont ehi_fontWithStyle:fontStyle size:self.dateLabel.font.pointSize];
    self.dateLabel.font = font;
    self.timeLabel.font = font;
}

# pragma mark - Layout

+ (EHILayoutMetrics *)defaultMetrics
{
    EHILayoutMetrics *metrics = [super defaultMetrics];
    metrics.fixedSize = (CGSize){ .width = EHILayoutValueNil, .height = 30.0f };
    metrics.isAutomaticallyRegisterable = NO;
    return metrics;
}

@end
