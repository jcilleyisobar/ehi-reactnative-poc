//
//  EHIReviewTravelPurposeCell.m
//  Enterprise
//
//  Created by fhu on 6/3/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReviewTravelPurposeCell.h"
#import "EHIReviewTravelPurposeViewModel.h"
#import "EHISegmentedControl.h"

@interface EHIReviewTravelPurposeCell()
@property (weak, nonatomic) IBOutlet EHISegmentedControl *segmentedControl;
@property (weak, nonatomic) IBOutlet UILabel *subtitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (strong, nonatomic) EHIReviewTravelPurposeViewModel *viewModel;
@end

@implementation EHIReviewTravelPurposeCell

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIReviewTravelPurposeViewModel new];
        [self.viewModel selectTravelPurposeAtIndex:[self.segmentedControl selectedSegmentIndex]];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.segmentedControl.fontSize = 18.0;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIReviewTravelPurposeViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateSegmentedControl:)];
    
    model.bind.map(@{
        source(model.title)    : dest(self, .titleLabel.text),
        source(model.subtitle) : dest(self, .subtitleLabel.text)
    });
}

- (void)invalidateSegmentedControl:(MTRComputation *)computation
{
    [self.segmentedControl setTitle:self.viewModel.segmentedControlFirstTitle forSegmentAtIndex:0];
    [self.segmentedControl setTitle:self.viewModel.segmentedControlSecondTitle forSegmentAtIndex:1];
}

# pragma mark - IB Actions

- (IBAction)segmentedControlValueChanged:(id)sender
{
    [self.viewModel selectTravelPurposeAtIndex:[self.segmentedControl selectedSegmentIndex]];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize){
        .width = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.segmentedControl.frame) + EHIMediumPadding
    };
}


@end
