//
//  EHICarClassExtras.h
//  Enterprise
//
//  Created by mplace on 2/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICarClassExtra.h"

@interface EHICarClassExtras : EHIModel

@property (copy, nonatomic, readonly) NSArray<EHICarClassExtra> *equipment;
@property (copy, nonatomic, readonly) NSArray<EHICarClassExtra> *insurance;
@property (copy, nonatomic, readonly) NSArray<EHICarClassExtra> *ancillary;
@property (copy, nonatomic, readonly) NSArray<EHICarClassExtra> *fuel;

// computed properties
@property (nonatomic, readonly) NSArray *all;
@property (nonatomic, readonly) NSArray *selected;
@property (nonatomic, readonly) NSArray *selectedByUser;

@end
