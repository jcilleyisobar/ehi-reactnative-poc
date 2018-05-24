//
//  EHILocationEmptyQueryCell.m
//  Enterprise
//
//  Created by mplace on 2/10/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationEmptyQueryCell.h"
#import "EHILocationEmptyQueryViewModel.h"
#import "EHIButton.h"
#import "EHILabel.h"

@interface EHILocationEmptyQueryCell ()
@property (strong, nonatomic) EHILocationEmptyQueryViewModel *viewModel;
@property (weak, nonatomic) IBOutlet EHILabel *titleLabel;
@property (weak, nonatomic) IBOutlet EHILabel *subtitleLabel;
@property (weak, nonatomic) IBOutlet EHIButton *callButton;
@property (weak, nonatomic) IBOutlet EHIButton *nearbyButton;
@end

@implementation EHILocationEmptyQueryCell

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        self.viewModel = [EHILocationEmptyQueryViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.nearbyButton.type = EHIButtonTypeNearby;
    self.nearbyButton.imageHorizontalAlignment = UIControlContentHorizontalAlignmentLeft;
    self.callButton.imageHorizontalAlignment   = UIControlContentHorizontalAlignmentLeft;
}

# pragma mark - Reactions

- (void)registerReactions:(EHILocationEmptyQueryViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.title)    : dest(self, .titleLabel.text),
        source(model.subtitle) : dest(self, .subtitleLabel.text),
        source(model.callButtonTitle)   : dest(self, .callButton.ehi_title),
        source(model.nearbyButtonTitle) : dest(self, .nearbyButton.ehi_title),
    });
}

# pragma mark - Actions

- (IBAction)didTapCallButton:(id)sender
{
    [self.viewModel callHelpNumber];
}

- (IBAction)didTapNearbyButton:(id)sender
{
    [self.viewModel findNearbyLocations];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    CGRect bottomFrame = [self.nearbyButton ehi_frameInView:self];
    
    return (CGSize){
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(bottomFrame) + EHIMediumPadding
    };
}

@end
