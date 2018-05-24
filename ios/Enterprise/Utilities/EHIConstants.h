//
//  EHIConstants.h
//  Enterprise
//
//  Created by Ty Cobb on 4/13/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#define EHIMockEnabled 0

#define EHILightPadding    (15.0f)
#define EHIMediumPadding   (20.0f)
#define EHIHeavyPadding    (30.0f)
#define EHIHeaviestPadding (40.0f)

/** @c nil values to use as in structs to indicate unused values */
#define EHILayoutValueNil      (UIViewNoIntrinsicMetric)
#define EHILayoutSizeNil       ((CGSize){ .width = EHILayoutValueNil, .height = EHILayoutValueNil })
#define EHILayoutOffsetNil     ((UIOffset){ .horizontal = EHILayoutValueNil, .vertical = EHILayoutValueNil })
#define EHILayoutEdgeInsetsNil ((UIEdgeInsets){ .top = 0 , .left = 0, .bottom = 0, .right = 0 })
