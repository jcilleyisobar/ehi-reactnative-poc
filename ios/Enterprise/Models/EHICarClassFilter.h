//
//  EHICarClassFilter.h
//  Enterprise
//
//  Created by mplace on 6/15/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHIFilterType.h"

@interface EHICarClassFilter : EHIModel
@property (copy  , nonatomic) NSString *title;
@property (assign, nonatomic) EHIFilterType type;
@property (assign, nonatomic) NSInteger code;
@end

EHIAnnotatable(EHICarClassFilter)

@interface EHICarClassFilters : EHIModel
@property (copy  , nonatomic) NSString *title;
@property (assign, nonatomic) EHIFilterType type;
@property (copy  , nonatomic) NSArray<EHICarClassFilter> *filterValues;
@end

EHIAnnotatable(EHICarClassFilters)