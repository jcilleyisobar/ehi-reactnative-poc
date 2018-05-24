//
//  EHIRoundedToggleButton.m
//  Enterprise
//
//  Created by Rafael Ramos on 11/7/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIRoundedToggleButton.h"

@interface EHIRoundedToggleButton ()
@property (strong, nonatomic) CALayer *innerCicle;
@end

@implementation EHIRoundedToggleButton

- (instancetype)initWithFrame:(CGRect)frame
{
    if(self = [super initWithFrame:frame]) {
        [self applyDefaults];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    [self applyDefaults];
}

- (void)applyDefaults
{
    // ignore tint color
    self.tintColor = [UIColor clearColor];
    self.layer.borderColor  = [UIColor ehi_grayColor3].CGColor;
}

- (void)configure
{
    self.layer.cornerRadius = CGRectGetWidth(self.bounds) / 2;
    self.layer.borderWidth  = 1.0f;
    // update inner circler color
    self.innerCicle.backgroundColor = self.selected ? [UIColor ehi_greenColor].CGColor : self.backgroundColor.CGColor;
}

# pragma mark - Selection

- (void)setSelected:(BOOL)selected
{
    [super setSelected:selected];
    [self configure];
}

# pragma mark - Accessors

- (CALayer *)innerCicle
{
    if(!_innerCicle) {
        _innerCicle = [CALayer layer];
        _innerCicle.frame = CGRectInset(self.bounds, 5.f, 5.f);
        _innerCicle.cornerRadius    = CGRectGetWidth(_innerCicle.frame) / 2;
        _innerCicle.backgroundColor = self.backgroundColor.CGColor;
        
        [self.layer addSublayer:_innerCicle];
    }
    
    return _innerCicle;
}

@end
