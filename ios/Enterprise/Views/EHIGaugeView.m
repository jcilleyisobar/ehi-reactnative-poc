//
//  EHIGaugeView.m
//  Enterprise
//
//  Created by Rafael Ramos on 1/6/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIGaugeView.h"
#import "EHIGaugeViewModel.h"
#import "EHIArcSegmentsView.h"
#import "EHIMeterView.h"

@interface EHIGaugeView ()
@property (strong, nonatomic) EHIGaugeViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIArcSegmentsView *arcSegmentsView;
@property (weak  , nonatomic) IBOutlet EHIMeterView *meterView;
@property (weak  , nonatomic) IBOutlet EHIMeterView *innerMeterView;
@end

@implementation EHIGaugeView

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIGaugeViewModel new];
    }
    
    return self;
}

- (void)layoutSubviews
{
    [super layoutSubviews];
    
    [self updateViewsViewModel];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIGaugeViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:^(MTRComputation *computation) {
        [self updateViewsViewModel];
    }];
    
    [MTRReactor autorun:^(MTRComputation *computation) {
        self.innerMeterView.fill = self.viewModel.fill;
    }];
}

- (void)updateViewsViewModel
{
    self.arcSegmentsView.arcData  = self.viewModel.arcData;
    self.innerMeterView.meterData = self.viewModel.innerMeterData;
    self.meterView.meterData      = self.viewModel.meterData;
}

+ (BOOL)isReplaceable
{
    return YES;
}

@end
