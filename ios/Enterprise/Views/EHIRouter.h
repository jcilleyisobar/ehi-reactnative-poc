//
//  EHIRouter.h
//  Enterprise
//
//  Created by Alex Koller on 12/8/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "NAVRouter.h"
#import "EHIViewControllers.h"
#import "EHIAccessibilityIdentifiers.h"

@interface EHIRouter : NAVRouter

+ (EHIRouter *)currentRouter;

@end
