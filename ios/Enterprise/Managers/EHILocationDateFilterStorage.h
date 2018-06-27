//
//  EHILocationDateFilterStorage.h
//  Enterprise
//
//  Created by Rafael Ramos on 5/30/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHILocationDateFilterStore.h"

@interface EHILocationDateFilterStorage : NSObject

+ (EHILocationDateFilterStorage *)storage;

@property (strong, nonatomic, readonly) EHILocationDateFilterStore *dateStore;

@end
