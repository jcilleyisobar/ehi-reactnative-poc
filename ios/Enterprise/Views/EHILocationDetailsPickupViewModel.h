//
//  EHILocationDetailsPickupViewModel.h
//  Enterprise
//
//  Created by Ty Cobb on 2/27/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHILocationDetailsPickupViewModel : EHIViewModel
@property (copy  , nonatomic, readonly) NSString *title;
@property (copy  , nonatomic, readonly) NSString *details;
@property (assign, nonatomic, readonly) NSRange highlightRange;
@end
