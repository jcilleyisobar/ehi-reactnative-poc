//
//  EHIMenuCell.m
//  Enterprise
//
//  Created by Ty Cobb on 3/30/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIMenuCell.h"
#import "EHIView.h"
#import "EHIMenuScreenCell.h"
#import "EHIMenuSecondaryCell.h"
#import "EHIMenuAnimationProgress.h"
#import "EHIMenuPromotionCell.h"

@interface EHIMenuCell () <EHIMenuAnimationProgressListener>
@property (strong, nonatomic) EHIMenuItem *model;
@property (assign, nonatomic) CGRect contentFrame;
@property (weak  , nonatomic) IBOutlet UIImageView *iconView;
@property (weak  , nonatomic) IBOutlet EHIView *headerView;
@property (weak  , nonatomic) IBOutlet UIView *bottomDivider;
@end

@implementation EHIMenuCell

- (void)awakeFromNib
{
    [super awakeFromNib];
   
    // substitute the default background color into the content view
    self.contentView.backgroundColor = self.backgroundColor;
    self.backgroundColor = [UIColor clearColor];
}

- (void)didMoveToWindow
{
    [super didMoveToWindow];
    
    // wait to listen to progress until this point, so that the content view is sized properly
    [[EHIMenuAnimationProgress sharedInstance] addListener:self];
}

- (void)layoutSubviews
{
    [super layoutSubviews];
    
    // re-set the frame we rightly deserve if it gets changed
    CGSize size    = self.contentView.frame.size;
    CGPoint origin = self.contentView.frame.origin;
    
    BOOL equalsOrigin = CGPointEqualToPoint(origin, self.contentFrame.origin);
    BOOL isSizeZero   = CGSizeEqualToSize(size, CGSizeZero);
    BOOL shouldUpdateFrame = !equalsOrigin && !isSizeZero;
    
    if(shouldUpdateFrame) {
        CGRect newRect = CGRectMake(self.contentFrame.origin.x, self.contentFrame.origin.y, size.width, size.height);
        self.contentView.frame = newRect;
    }
}

- (void)prepareForReuse
{
    [super prepareForReuse];
    
    self.titleLabel.text = nil;
    self.titleLabel.attributedText = nil;
}

# pragma mark - Accessibility

- (void)registerAccessibilityIdentifiers
{
    [super registerAccessibilityIdentifiers];
    
    self.accessibilityIdentifier = EHIMenuRowKey;
}

# pragma mark - EHIListCell

+ (Class<EHIListCell>)subclassForModel:(EHIMenuItem *)model
{
    switch(model.type) {
        case EHIMenuItemTypePromotion:
            return [EHIMenuPromotionCell class];
        case EHIMenuItemTypeScreen:
            return [EHIMenuScreenCell class];
        case EHIMenuItemTypeSecondary:
            return [EHIMenuSecondaryCell class];
    }
}

- (void)updateWithModel:(EHIMenuItem *)model metrics:(EHILayoutMetrics *)metrics
{
    [super updateWithModel:model metrics:metrics];

    self.model = model;
    self.titleLabel.text = model.title;
    self.iconView.ehi_imageName = model.iconName;
    
    NSAttributedString *attributedTitle = model.attributedTitle;
    if(attributedTitle) {
        self.titleLabel.attributedText = attributedTitle;
    }
    
    [self updateHeaderWithModel:model];
    [self updateBottomDividerWithModel:model];
}

- (void)updateHeaderWithModel:(EHIMenuItem *)model
{
    [self.headerView updateWithModel:model.headerTitle];
    
    BOOL showHeader = model.showHeader;
    
    MASLayoutPriority priority = showHeader ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    
    [self.headerView mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0).priority(priority);
    }];
}

- (void)updateBottomDividerWithModel:(EHIMenuItem *)model
{
    BOOL hideDivider = model.hideDivider;
    
    MASLayoutPriority priority = hideDivider ? MASLayoutPriorityRequired : MASLayoutPriorityDefaultLow;
    
    [self.bottomDivider mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0).priority(priority);
    }];
}

# pragma mark - Animation

- (void)menuAnimationDidUpdate:(EHIMenuAnimationProgress *)progress
{
    // tranlsate each cell by its width to the left when the animation is at 0%
    CGFloat translation = -self.contentView.bounds.size.width;
    // update the translation based on the how far along we are in the animation
    translation *= 1.0f - progress.percentComplete;
    // as the animation completes, parallax the translation by the some constant * row
    translation *= 1.0f + self.model.row * 0.08f;

    CGRect frame = self.contentView.frame;
    frame.origin.x = (int)translation;

    self.contentView.frame = frame;
    self.contentView.alpha = progress.percentComplete;
    
    // store the frame in case collection view tries to reset our state
    self.contentFrame = frame;
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = EHIMenuCellDefaultHeight
    };
}

- (CGSize)titleBasedContentSize
{
    CGFloat titleY = CGRectGetMaxY(self.titleLabel.frame);
    CGFloat height = titleY + EHILightPadding;
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = height
    };
}

@end
