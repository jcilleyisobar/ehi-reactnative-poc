//
//  EHIDashboardPromotionCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 3/29/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIDashboardPromotionCell.h"
#import "EHIDashboardPromotionCellViewModel.h"
#import "EHIPromotionView.h"

@interface EHIDashboardPromotionCell ()
@property (strong, nonatomic) EHIPromotionView *promotionView;
@property (weak  , nonatomic) IBOutlet UIView *headerDivider;
@property (weak  , nonatomic) IBOutlet UIView *footerDivider;
@end

@implementation EHIDashboardPromotionCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIDashboardPromotionCellViewModel new];
    }

    return self;
}

# pragma mark - View Lifecycle

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    [self addPromotionSubview];
}

- (void)addPromotionSubview
{
    [self.contentView insertSubview:self.promotionView atIndex:0];

    [self.promotionView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.headerDivider.mas_top);
        make.left.equalTo(self.contentView.mas_left);
        make.right.equalTo(self.contentView.mas_right);
        make.bottom.equalTo(self.footerDivider.mas_bottom);
    }];
}

# pragma mark - Layout

+ (EHILayoutMetrics *)defaultMetrics
{
    EHILayoutMetrics *metrics = [super defaultMetrics];
    metrics.fixedSize = (CGSize) { .width = EHILayoutValueNil, .height = 160.f };
    
    return metrics;
}

# pragma mark - Accessors

- (EHIPromotionView *)promotionView
{
    if(!_promotionView) {
        _promotionView = [EHIPromotionView.class ehi_instanceFromNib];
        _promotionView.translatesAutoresizingMaskIntoConstraints = NO;
    }
    return _promotionView;
}

@end
