//
//  EHILocationCalloutView.m
//  Enterprise
//
//  Created by Ty Cobb on 1/30/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationsMapCalloutView.h"
#import "EHILocationsMapListCell.h"
#import "EHILocationsMapListActions.h"
#import "EHIAnalytics.h"

@interface EHILocationsMapCalloutView () <EHILocationsMapListActions>
@property (weak, nonatomic) EHILocationsMapListCell *contentView;
@end

@implementation EHILocationsMapCalloutView

- (void)awakeFromNib
{
    [super awakeFromNib];

    EHILocationsMapListCell *contentView = [EHILocationsMapListCell ehi_instanceFromNib];
    contentView.translatesAutoresizingMaskIntoConstraints = NO;
    
    [self insertSubview:contentView atIndex:0];
    [self setContentView:contentView];
    
    [contentView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.equalTo(self);
    }];
}

# pragma mark - EHIUpdatable

- (void)updateWithModel:(id)model
{
    [self updateWithModel:model metrics:[self.class defaultMetrics]];
}

- (void)updateWithModel:(id)model metrics:(EHILayoutMetrics *)metrics
{
    [self.contentView updateWithModel:model metrics:metrics];
    
    // force because view starts hidden
    [self.contentView registerReactions:self.contentView.viewModel];
}

# pragma mark - EHILayoutable

- (CGSize)intrinsicContentSize
{
    return self.contentView.intrinsicContentSize;
}

# pragma mark - EHILocationMapBubbleViewActions

- (void)locationsMapDidTapSelect:(EHILocationsMapListCell *)sender
{
    [self ehi_performAction:@selector(calloutViewDidTapSelect:) withSender:self];
}

- (void)locationsMapDidTapLocationTitle:(EHILocationsMapListCell *)sender
{
    [self ehi_performAction:@selector(calloutViewDidTapLocationTitle:) withSender:self];
}

- (void)locationsMapDidTapChangeState:(EHILocationsMapListCell *)sender
{
    void (^senderUpdates)() = ^{
        [sender invalidateIntrinsicContentSize];
        [sender setNeedsUpdateConstraints];
        [sender setNeedsLayout];
        [sender layoutIfNeeded];
    };
    
    void (^selfUpdates)() = ^{
        [self invalidateIntrinsicContentSize];
        [self setNeedsUpdateConstraints];
        [self setNeedsLayout];
        [self layoutIfNeeded];
    };
    
    senderUpdates();
    selfUpdates();
    senderUpdates();
    
    [self ehi_performAction:@selector(calloutViewDidTapChangeState:) withSender:self];
}

- (BOOL)respondsToSelector:(SEL)aSelector
{
    if(aSelector == @selector(locationsMapDidTapSelect:)
    || aSelector == @selector(locationsMapDidTapLocationTitle:)
    || aSelector == @selector(locationsMapDidTapChangeState:)) {
        return YES;
    }
    
    return [super respondsToSelector:aSelector];
}

@end
