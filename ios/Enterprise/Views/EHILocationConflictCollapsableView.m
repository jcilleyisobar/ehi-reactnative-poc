//
//  EHILocationConflictCollapsableView.m
//  Enterprise
//
//  Created by Rafael Ramos on 6/19/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHILocationConflictCollapsableView.h"
#import "EHILocationConflictCollapsableLayer.h"

@implementation EHILocationConflictCollapsableView

- (void)layoutSubviews
{
    [super layoutSubviews];
    
    self.conflictLayer.lineWidth   = 2.0f;
    self.conflictLayer.fillColor   = [UIColor ehi_yellowWarningColor2].CGColor;
    self.conflictLayer.strokeColor = [UIColor ehi_yellowWarningColor].CGColor;
}

+ (Class)layerClass
{
    return EHILocationConflictCollapsableLayer.class;
}

# pragma mark - Accessors

- (EHILocationConflictCollapsableLayer *)conflictLayer
{
    return (EHILocationConflictCollapsableLayer *)self.layer;
}

- (void)setArrowHeight:(CGFloat)arrowHeight
{
    self.conflictLayer.arrowHeight = arrowHeight;
    
    [self setNeedsLayout];
    [self layoutIfNeeded];
}

- (void)setPadding:(CGFloat)padding
{
    self.conflictLayer.padding = padding;
    
    [self setNeedsLayout];
    [self layoutIfNeeded];
}

@end
