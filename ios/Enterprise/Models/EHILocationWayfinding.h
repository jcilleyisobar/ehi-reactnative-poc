//
//  EHILocationWayfinding.h
//  Enterprise
//
//  Created by Pawel Bragoszewski on 02.03.2015.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHIWatchEncodable.h"

typedef NS_ENUM(NSInteger, EHILocationDirectionType) {
    EHILocationDirectionForward,
    EHILocationDirectionLeft,
    EHILocationDirectionRight,
    EHILocationDirectionEscalator,
    EHILocationDirectionSteps,
    EHILocationDirectionArrive
};

@interface EHILocationWayfinding : EHIModel <EHIWatchEncodable>
@property (copy, nonatomic, readonly) NSString *text;
@property (copy, nonatomic, readonly) NSString *iconUrl;
@end

EHIAnnotatable(EHILocationWayfinding)
