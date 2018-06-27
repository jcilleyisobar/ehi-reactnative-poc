//
//  EHILocationFilterMiscCellViewModel.h
//  Enterprise
//
//  Created by mplace on 2/27/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHILocationFilterMiscCellViewModel : EHIViewModel <MTRReactive>
/** Title of the miscellaneous filter */
@property (copy, nonatomic) NSString *title;
@end
