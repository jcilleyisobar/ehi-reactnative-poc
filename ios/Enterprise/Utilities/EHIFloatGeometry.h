//
//  EHIFloatGeometry.h
//  Enterprise
//
//  Created by Ty Cobb on 1/5/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHISharedGeometry.h"

# pragma mark - Constants

#define M_2PI (2 * M_PI)

#define EHIFloatRangeZero  (EHIFloatRange){ }
#define EHIFloatTupleZero  (EHIFloatTuple){ }
#define EHIFloatVectorZero (EHIFloatVector){ }
#define EHIFloatValueNil   (MAXFLOAT)

typedef struct { CGFloat location, length; } EHIFloatRange;
typedef struct { CGFloat minimum, maximum; } EHIFloatTuple;
typedef struct { CGFloat x, y, z; } EHIFloatVector;
typedef struct { EHIFloatRange x, y, z; } EHIFloatRangeVector;

# pragma mark - EHIFloatRange

NS_INLINE CGFloat EHIFloatRangeMax(EHIFloatRange range)
{
    return range.location + range.length;
}

NS_INLINE EHIFloatRange EHIFloatRangeInvert(EHIFloatRange range)
{
    return (EHIFloatRange){ .location = range.length, .length = -range.length };
}

NS_INLINE CGFloat EHIFloatRangeInterpolate(EHIFloatRange range, CGFloat value)
{
    return range.location + range.length * value;
}

NS_INLINE CGFloat EHIFloatRangeNormalize(EHIFloatRange range, CGFloat value)
{
    return range.length ? EHIClamp((value - range.location) / range.length, 0.0f, 1.0f) : 0.0f;
}

NS_INLINE CGFloat EHIFloatRangeSample(EHIFloatRange range)
{
    return range.location + range.length * EHIRandomFloat();
}

NS_INLINE CGFloat EHIFloatRangeCenter(EHIFloatRange range)
{
    return range.location + range.length / 2.0f;
}

NS_INLINE EHIFloatRange EHIFloatRangeScale(EHIFloatRange range, CGFloat scale)
{
    return (EHIFloatRange){ .location = range.location * scale, .length = range.length * scale };
}

NS_INLINE EHIFloatRange EHIFloatRangeFromEndpoints(CGFloat minimum, CGFloat maximum)
{
    return (EHIFloatRange){ .location = minimum, .length = maximum - minimum };
}

NS_INLINE EHIFloatRange EHIFloatRangeFromCenter(CGFloat center, CGFloat length)
{
    return (EHIFloatRange){ .location = center - length / 2.0, .length = length };
}

NS_INLINE CGFloat EHIFloatRangeDelta(EHIFloatRange range, CGFloat value)
{
    if(value < range.location) {
        return value - range.location;
    }
    
    CGFloat maximum = EHIFloatRangeMax(range);
    if(value > maximum) {
        return value - maximum;
    }
    
    return 0.0f;
}

NS_INLINE BOOL EHIFloatRangeContains(EHIFloatRange range, CGFloat value)
{
    return value >= range.location && value <= EHIFloatRangeMax(range);
}

# pragma mark - Tuple

NS_INLINE CGFloat EHIFloatTupleLength(EHIFloatTuple tuple)
{
    return tuple.maximum - tuple.minimum;
}

NS_INLINE CGFloat EHIFloatTupleCenter(EHIFloatTuple tuple)
{
    return tuple.minimum + (tuple.maximum - tuple.minimum) / 2.0f;
}

NS_INLINE EHIFloatTuple EHIFloatTupleWithValue(CGFloat value)
{
    return (EHIFloatTuple){ .minimum = value, .maximum = value };
}

# pragma mark - Vector

NS_INLINE CATransform3D CATransform3DVectorTranslate(CATransform3D transform, EHIFloatVector translation)
{
    return CATransform3DTranslate(transform, translation.x, translation.y, translation.z);
}

NS_INLINE CATransform3D CATransform3DMakeVectorTranslation(EHIFloatVector translation)
{
    return CATransform3DVectorTranslate(CATransform3DIdentity, translation);
}

NS_INLINE EHIFloatVector EHIFloatVectorScale(EHIFloatVector vector, CGFloat scale)
{
    EHIFloatVector result;
    
    result.x = vector.x * scale;
    result.y = vector.y * scale;
    result.z = vector.z * scale;
    
    return result;
}

NS_INLINE EHIFloatVector EHIFloatVectorInvert(EHIFloatVector vector)
{
    return EHIFloatVectorScale(vector, -1.0f);
}

# pragma mark - Range Vector

NS_INLINE EHIFloatRangeVector EHIFloatRangeVectorFromVector(EHIFloatVector vector)
{
    return (EHIFloatRangeVector) {
        .x = (EHIFloatRange){ .length = vector.x },
        .y = (EHIFloatRange){ .length = vector.y },
        .z = (EHIFloatRange){ .length = vector.z }
    };
}

NS_INLINE EHIFloatRangeVector EHIFloatRangeVectorInvert(EHIFloatRangeVector vector)
{
    return (EHIFloatRangeVector) {
        .x = EHIFloatRangeInvert(vector.x),
        .y = EHIFloatRangeInvert(vector.y),
        .z = EHIFloatRangeInvert(vector.z)
    };
}
