//
//  EHIDashboardLoadingCell.m
//  Enterprise
//
//  Created by mplace on 5/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIDashboardLoadingCell.h"
#import "EHIDashboardLoadingViewModel.h"
#import "EHIReviewBookingLoadingIndicator.h"

@interface EHIDashboardLoadingCell ()
@property (strong, nonatomic) EHIDashboardLoadingViewModel *viewModel;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet EHIReviewBookingLoadingIndicator *loadingIndicator;
@end

@implementation EHIDashboardLoadingCell

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIDashboardLoadingViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];

    [self.loadingIndicator setIsGreen:YES];
}

# pragma mark - Lifecycle

- (void)updateWithModel:(id)model metrics:(EHILayoutMetrics *)metrics
{
    [super updateWithModel:model metrics:metrics];
    
    [self.loadingIndicator setIsAnimating:YES];
}

- (void)didEndDisplaying
{
    [super didEndDisplaying];
    
    [self.loadingIndicator setIsAnimating:NO];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIDashboardLoadingViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.title) : dest(self, .titleLabel.text)
    });
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = 302.0f
    };
}

@end
