//
//  EHIMenuViewController.h
//  Enterprise
//
//  Created by Ty Cobb on 1/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewController.h"

@interface EHIMenuViewController : EHIViewController

@end

// used to properly track menu with analytics
@protocol EHIMenuActions <NSObject>
- (void)menuSelectedSameIndex;
@end
