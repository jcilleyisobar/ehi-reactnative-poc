//
//  EHIMenuScreenCell.m
//  Enterprise
//
//  Created by Ty Cobb on 3/31/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIMenuScreenCell.h"

@implementation EHIMenuScreenCell

- (void)setSelected:(BOOL)selected
{
    [super setSelected:selected];
    
    self.contentView.backgroundColor = selected ? [UIColor ehi_darkGreenColor] : [UIColor ehi_greenColor];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return [self titleBasedContentSize];
}

@end
