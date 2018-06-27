//
//  CALayer+Shadow.h
//  Enterprise
//
//  Created by Ty Cobb on 2/13/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@interface CALayer (Shadow)
/** Computed property which enables the standard shadow for the layer */
@property (assign, nonatomic) BOOL ehi_showsShadow;
/** Recalculates the layer's shadow path to fit its bounds */
- (void)ehi_recalculateShadowPath;
@end
