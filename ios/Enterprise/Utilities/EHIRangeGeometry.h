//
//  EHIRangeGeometry.h
//  Enterprise
//
//  Created by Ty Cobb on 1/5/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHISharedGeometry.h"

#define NSRangeZero (NSRange){ }
#define NSRangeNull (NSRange){ .location = NSNotFound }

NS_INLINE BOOL NSRangeEqualToRange(NSRange range1, NSRange range2)
{
    return range1.location == range2.location && range1.length == range2.length;
}

NS_INLINE BOOL NSRangeIsNull(NSRange range)
{
    return NSRangeEqualToRange(range, NSRangeNull);
}

NS_INLINE BOOL NSRangeContains(NSRange range, NSInteger value)
{
    return !NSRangeIsNull(range) && value >= range.location && value <= NSMaxRange(range);
}

NS_INLINE BOOL NSRangeIsIndex(NSRange range, NSInteger index)
{
    return range.location == index && range.length == 0;
}

NS_INLINE NSInteger NSRangeWrap(NSRange range, NSInteger value)
{
    NSInteger minimum = range.location;
    NSInteger maximum = NSMaxRange(range);

    if(value < range.location) {
        return (maximum + 1) + EHISafeMod(value - minimum, range.length);
    } else if(value > maximum) {
        return (minimum - 1) + EHISafeMod(value - maximum, range.length);
    }
    
    return value;
}
