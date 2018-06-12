//
//  EHITerminalDirectionsViewModel.h
//  Enterprise
//
//  Created by Pawel Bragoszewski on 02.03.2015.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHISectionHeaderModel.h"

@interface EHILocationWayfindingViewModel : EHIViewModel
@property (strong, nonatomic, readonly) NSString *title;
@property (strong, nonatomic, readonly) NSArray *wayfindings;
@end
