//
//  EHILocationsFilterBannerView.m
//  Enterprise
//
//  Created by Rafael Machado on 19/05/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHILocationsFilterBannerView.h"
#import "EHILocationsFilterBannerViewModel.h"

@interface EHILocationsFilterBannerView ()
@property (strong, nonatomic) EHILocationsFilterBannerViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *filterTitle;
@property (weak  , nonatomic) IBOutlet UILabel *filtersLabel;
@property (weak  , nonatomic) IBOutlet UIButton *clearButton;
@end

@implementation EHILocationsFilterBannerView

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHILocationsFilterBannerViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHILocationsFilterBannerViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateClearButton:)];
    
    model.bind.map(@{
        source(model.title)   : dest(self, .filterTitle.text),
        source(model.filters) : dest(self, .filtersLabel.text)
    });
}

- (void)invalidateClearButton:(MTRComputation *)computation
{
    BOOL hide = self.viewModel.hideClear;
    
    [self animateBlock:^{
        self.clearButton.alpha   = hide ? 0.0f : 1.0f;
        self.clearButton.enabled = !hide;
    }];
}

- (void)animateBlock:(void (^)())block
{
    [UIView animateWithDuration:0.3f animations:^{
        ehi_call(block)();
    }];
}

# pragma mark - Actions

- (IBAction)didTapClear:(UIButton *)sender
{
    [self ehi_performAction:@selector(filterBannerDidTapClear) withSender:self];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize){
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.filtersLabel.frame) + 8.0f
    };
}

+ (BOOL)isReplaceable
{
    return YES;
}

@end
