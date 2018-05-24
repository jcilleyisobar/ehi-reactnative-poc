//
//  EHIDataStoreRecord.h
//  Enterprise
//
//  Created by Ty Cobb on 3/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"

@interface EHIDataStoreRecord : EHIEncodableObject

@property (strong, nonatomic, readonly) EHIModel *model;
@property (strong, nonatomic, readonly) NSDate *createdAt;

- (instancetype)initWithModel:(EHIModel *)model;

@end
