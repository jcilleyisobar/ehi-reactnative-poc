//
//  EHIPolicyInfoDetailsViewModel.h
//  Enterprise
//
//  Created by Pawel Bragoszewski on 26.02.2015.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIPolicyDetailViewModel : EHIViewModel
@property (copy, nonatomic, readonly) NSString *title;
@property (copy, nonatomic, readonly) NSString *name;
@property (copy, nonatomic, readonly) NSString *details;
@end
