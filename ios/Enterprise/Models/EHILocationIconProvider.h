//
//  EHILocationIconProvider.h
//  Enterprise
//
//  Created by Rafael Ramos on 03/10/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

@class EHILocation;
@interface EHILocationIconProvider : NSObject
+ (NSString *)iconForLocation:(EHILocation *)location;
@end
