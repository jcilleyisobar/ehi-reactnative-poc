//
//  EHITimePickerMaskView.m
//  Enterprise
//
//  Created by Michael Place on 3/29/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHITimePickerMaskView.h"
#import "EHITimePickerActiveTimeCell.h"

@interface EHITimePickerMaskView ()
@property (weak, nonatomic) IBOutlet UICollectionView *collectionView;
@end

@implementation EHITimePickerMaskView

- (void)awakeFromNib
{
    [super awakeFromNib];

    // add the mask
    [self addMask];
}

- (void)layoutSubviews
{
    [super layoutSubviews];
    
    // update the mask rect
    [self invalidateMask];
}

# pragma mark - Masking

- (void)addMask
{
    // set the mask of the view.
    CALayer *mask = [CALayer layer];
    mask.backgroundColor = [UIColor blackColor].CGColor;
    
    self.layer.mask = mask;
    
    // update the mask rect
    [self invalidateMask];
}

- (void)invalidateMask
{
    self.layer.mask.frame = [self maskFrame];
}

- (CGRect)maskFrame
{
    CGSize itemSize = [EHITimePickerActiveTimeCell sizeForContainerSize:self.collectionView.bounds.size];
    CGFloat yOffset = (self.frame.size.height / 2) - (itemSize.height / 2);
    CGRect maskRect = CGRectMake(0, yOffset, self.bounds.size.width, itemSize.height);
    return maskRect;
}

@end
