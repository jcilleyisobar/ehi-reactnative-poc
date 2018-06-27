//
//  EHISharedGeometry.h
//  Enterprise
//
//  Created by Ty Cobb on 1/5/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@import UIKit;

#define EHIClamp(_value, _minimum, _maximum) MIN(MAX((_value), (_minimum)), (_maximum))

NS_INLINE CGFloat EHIRandomFloat()
{
    return (CGFloat)(arc4random() % ((unsigned)RAND_MAX + 1)) / RAND_MAX;
}

NS_INLINE NSInteger EHISafeMod(NSInteger value, NSInteger modulus)
{
    return value < 0 ? -(labs(value) % modulus) : value & modulus;
}
