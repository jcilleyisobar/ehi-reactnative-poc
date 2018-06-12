//
//  EHIRestorableConstraint.m
//  EHIius
//
//  Created by Ty Cobb on 7/31/14.
//  Copyright (c) 2014 Isobar. All rights reserved.
//

#import "EHIRestorableConstraint.h"

@interface EHIRestorableConstraint ()
@property (assign, nonatomic) CGFloat restorableValue;
@end

@implementation EHIRestorableConstraint

- (void)setIsDisabled:(BOOL)isDisabled
{
    if(_isDisabled != isDisabled) {
        [self setConstant:isDisabled ? 0.0f : EHIRestorableConstant isDisabled:isDisabled];
    }
}

- (void)setConstant:(CGFloat)constant
{
    [self setConstant:constant isDisabled:self.isDisabled];
}

- (void)setConstant:(CGFloat)constant isDisabled:(BOOL)isDisabled
{
    // capture the restorable value if we haven't already
    if(!self.isDisabled && !self.restorableValue) {
        // the restorable value gets captured implicity by the accessor D:
    }
   
    _isDisabled = isDisabled;
    
    // filter the constant value
    if(isDisabled) {
        constant = 0.0f;
    } else if(constant == EHIRestorableConstant) {
        constant = self.restorableValue;
    }
    
    [super setConstant:constant];
}

- (void)setOffset:(CGFloat)constant
{
    self.constant = self.restorableValue + constant;
}

- (CGFloat)restorableValue
{
    // lazy-load the restorable value
    if(!_restorableValue) {
        _restorableValue = self.constant;
    }
    
    return _restorableValue;
}

@end
