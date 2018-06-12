//
//  EHILocationsFilterListView.m
//  Enterprise
//
//  Created by Rafael Ramos on 5/4/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHILocationsFilterListView.h"
#import "EHILocationsFilterListViewModel.h"
#import "EHIListCollectionView.h"
#import "EHIDateTimeComponentMapView.h"
#import "EHILocationsFilterBannerView.h"
#import "EHILocationsMapFiltersTipView.h"
#import "EHILocationFilterWidgetView.h"
#import "EHIRestorableConstraint.h"

@interface EHILocationsFilterListView () <EHIDateTimeComponentMapViewActions, EHILocationsFilterBannerViewActions, EHILocationsMapTipViewActions>
@property (strong, nonatomic) EHILocationsFilterListViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIDateTimeComponentMapView *dateTimeComponent;
@property (weak  , nonatomic) IBOutlet EHILocationsFilterBannerView *bannerView;
@property (weak  , nonatomic) IBOutlet EHILocationsMapFiltersTipView *tipView;

@property (weak  , nonatomic) IBOutlet UIView *filterContainerView;
@property (weak  , nonatomic) IBOutlet EHILocationFilterWidgetView *filterView;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *filterViewTop;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *filterDividerHeight;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *filterWidgetBottomConstraint;

@property (weak  , nonatomic) IBOutlet UIView *widgetContainerView;

@end

@implementation EHILocationsFilterListView

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHILocationsFilterListViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    [self drawFilterTipArrow];
}

- (void)drawFilterTipArrow
{
    CGFloat arrowHeight = 10.0f;
    CGFloat padding     = (CGRectGetWidth(self.filterView.frame)/2.0f) + arrowHeight;
    
    self.tipView.arrowHeight = arrowHeight;
    self.tipView.padding     = padding;
}

# pragma mark - Reactions

- (void)registerReactions:(EHILocationsFilterListViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateDateComponent:)];
    [MTRReactor autorun:self action:@selector(invalidateBanner:)];
    [MTRReactor autorun:self action:@selector(invalidateFilterTip:)];
    [MTRReactor autorun:self action:@selector(invalidateFilterContainer:)];
    
    model.bind.map(@{
        source(model.dateTimeModel) : dest(self, .dateTimeComponent.viewModel),
        source(model.bannerModel)   : dest(self, .bannerView.viewModel),
    });
}

- (void)invalidateDateComponent:(MTRComputation *)computation
{
    BOOL hide = self.viewModel.hideDateComponent;
    
    [self animateView:self.dateTimeComponent hide:hide];
    
    [self.dateTimeComponent invalidateIntrinsicContentSize];
}

- (void)invalidateBanner:(MTRComputation *)computation
{
    BOOL hide = self.viewModel.hideFiltersBanner;

    [self animateView:self.bannerView hide:hide];
}

- (void)animateView:(UIView *)view hide:(BOOL)hide
{
    [view setNeedsLayout];
    [self setNeedsLayout];
    
    MASLayoutPriority priority = hide ? MASLayoutPriorityRequired : MASLayoutPriorityDefaultLow;
    [view mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0).priority(priority);
    }];

    [self setNeedsUpdateConstraints];
    
    [UIView animateWithDuration:0.3f
                          delay:0.0f
         usingSpringWithDamping:0.8f
          initialSpringVelocity:0.0f
                        options:UIViewAnimationOptionCurveEaseInOut animations:^{
        view.alpha  = hide ? 0.0f : 1.0f;
        [view layoutIfNeeded];
        [self layoutIfNeeded];
    } completion:nil];
}

- (void)invalidateFilterTip:(MTRComputation *)computation
{
    BOOL show = self.viewModel.showFilterTip;
    
    self.tipView.hidden = !show;
    
    MASLayoutPriority priority = show ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    [self.tipView mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0).priority(priority);
    }];
}

- (void)invalidateFilterContainer:(MTRComputation *)computation
{
    BOOL isShowingFilters = self.viewModel.isShowingFilters;
    
    self.filterContainerView.backgroundColor = isShowingFilters ? [UIColor whiteColor] : [UIColor clearColor];
    self.filterViewTop.isDisabled       = isShowingFilters;
    self.filterDividerHeight.isDisabled = !isShowingFilters;
    self.filterWidgetBottomConstraint.isDisabled = isShowingFilters || self.viewModel.showFilterTip;
    
    EHILocationFilterWidgetLayout layout = isShowingFilters
        ? EHILocationFilterWidgetLayoutInsideContainer
        : EHILocationFilterWidgetLayoutRegular;
    [self.filterView updateWithModel:@(layout) metrics:nil];
    
    CALayer *apply  = isShowingFilters ? self.layer : self.filterView.layer;
    CALayer *remove = isShowingFilters ? self.filterView.layer : self.layer;
    self.applyShadow(apply);
    self.removeShadow(remove);
}

- (void (^)(CALayer *))applyShadow
{
    return ^(CALayer *layer) {
        layer.shadowOffset  = CGSizeMake(0, 3);
        layer.shadowRadius  = 3;
        layer.shadowOpacity = 0.10;
    };
}

- (void (^)(CALayer *))removeShadow
{
    return ^(CALayer *layer) {
        layer.shadowOpacity = 0.0f;
    };
}

# pragma mark - EHIDateTimeComponentMapViewActions

- (void)dateTimeComponentDidTapOnSection:(NSNumber *)section
{
    [self ehi_performAction:@selector(filterListDidTapOnSection:) withSender:section];
}

- (void)dateTimeComponentDidTapClear
{
    [self ehi_performAction:@selector(filterListDidClearDates) withSender:self];
}

- (void)dateTimeComponentDidTap:(EHIDateTimeComponentMapView *)sender
{
    [self ehi_performAction:@selector(filterListDidTap:) withSender:self];
}

# pragma mark - EHILocationsFilterBannerViewActions

- (void)filterBannerDidTapClear
{
    [self ehi_performAction:@selector(filterListDidClearFilters) withSender:self];
}

# pragma mark - EHILocationsMapTipView

- (void)filterTipDidTapClose:(EHILocationsMapFiltersTipView *)sender
{
    [UIView animateWithDuration:0.3 animations:^{
        self.tipView.alpha = 0.0f;
    }];
    
    [self.viewModel closeTip];
}

+ (BOOL)isReplaceable
{
    return YES;
}

@end
