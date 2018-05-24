//
//  EHILocationFilterWidgetView.m
//  Enterprise
//
//  Created by Rafael Machado on 18/05/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHILocationFilterWidgetView.h"
#import "EHIRestorableConstraint.h"

@interface EHILocationFilterWidgetView ()
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIView *containerView;
@property (weak, nonatomic) IBOutlet EHIRestorableConstraint *titleLeadingConstraint;
@end

@implementation EHILocationFilterWidgetView

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.titleLabel.text = EHILocalizedString(@"location_filter_button_title", @"FILTER", @"");
}

- (void)setBackgroundColor:(UIColor *)backgroundColor
{
    [super setBackgroundColor:backgroundColor];
    
    [self.containerView setBackgroundColor:backgroundColor];
}

# pragma mark - Actions

- (IBAction)didTapOnView:(UIControl *)sender
{
    [self ehi_performAction:@selector(locationFilterWidgetTapped:) withSender:self];
}

# pragma mark - Reactions

- (void)updateWithModel:(id)model metrics:(EHILayoutMetrics *)metrics
{
    [super updateWithModel:model metrics:metrics];
    
    EHILocationFilterWidgetLayout layout = [model integerValue];
    self.titleLeadingConstraint.isDisabled = layout == EHILocationFilterWidgetLayoutInsideContainer;
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.containerView.frame)
    };
}

+ (BOOL)isReplaceable
{
    return YES;
}

@end
