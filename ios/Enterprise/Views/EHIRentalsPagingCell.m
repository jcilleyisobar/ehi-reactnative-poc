//
//  EHIRentalsPagingCell.m
//  Enterprise
//
//  Created by Ty Cobb on 7/7/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIRentalsPagingCell.h"
#import "EHIRentalsPagingViewModel.h"
#import "EHIActionButton.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHIRentalsPagingCell ()
@property (strong, nonatomic) EHIRentalsPagingViewModel *viewModel;
@property (weak  , nonatomic, null_unspecified) IBOutlet EHIActionButton *loadMoreButton;
@end

@implementation EHIRentalsPagingCell

- (nullable instancetype)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        self.viewModel = [EHIRentalsPagingViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIRentalsPagingViewModel *)model
{
    [super registerReactions:model];
   
    model.bind.map(@{
        source(model.loadMoreTitle) : dest(self, .loadMoreButton.ehi_title),
        source(model.isLoading)     : dest(self, .loadMoreButton.isLoading)
    });
}

# pragma mark - Actions

- (IBAction)didTapLoadMoreButton:(UIButton *)button
{
    [self.viewModel loadMoreRentalsWithHandler:^(NSError *error) {
        if(!error) {
            [self ehi_performAction:@selector(pagingCellDidLoadMoreRentals:) withSender:self];
        }
    }];
}

# pragma mark - Layout

+ (EHILayoutMetrics *)defaultMetrics
{
    EHILayoutMetrics *metrics = [super defaultMetrics];
    metrics.isAutomaticallyRegisterable = NO;
    metrics.fixedSize = (CGSize){
        .width  = EHILayoutValueNil,
        .height = 86.0f
    };
    
    return metrics;
}

@end

NS_ASSUME_NONNULL_END
