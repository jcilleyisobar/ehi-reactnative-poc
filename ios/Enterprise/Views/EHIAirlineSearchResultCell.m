//
//  EHIAirlineSearchResultCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 6/22/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIAirlineSearchResultCell.h"
#import "EHIAirlineSearchResultViewModel.h"

@interface EHIAirlineSearchResultCell ()
@property (strong, nonatomic) EHIAirlineSearchResultViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *airportNameLabel;
@end

@implementation EHIAirlineSearchResultCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIAirlineSearchResultViewModel new];
    }

    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIAirlineSearchResultViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.airlineName) : dest(self, .airportNameLabel.text)
    });
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.airportNameLabel.frame) + EHILightPadding
    };
}

@end
