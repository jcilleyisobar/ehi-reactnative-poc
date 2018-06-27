//
//  EHIDataStoreRequest.h
//  Enterprise
//
//  Created by Ty Cobb on 3/9/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"

typedef NS_ENUM(NSInteger, EHIDataStoreRequestType) {
    EHIDataStoreRequestTypeFind,
    EHIDataStoreRequestTypeFirst,
    EHIDataStoreRequestTypeSave,
    EHIDataStoreRequestTypeDelete,
};

@interface EHIDataStoreRequest : NSObject

/** The @c type of request to make; defaults to @c find */
@property (assign, nonatomic) EHIDataStoreRequestType type;
/** The @c model to save; required for @c save and @c delete; NOTE: saves a copy */
@property (copy  , nonatomic) EHIModel *model;
/** The @c klass of the model; optional, inferred if @c model is set */
@property (strong, nonatomic) Class<EHIModel> klass;
/** The @c collection backing this request; required, inferred from klass */
@property (strong, nonatomic) EHICollection *collection;
/** @c YES if this request is a `read` type (@c find / @c first) */
@property (nonatomic, readonly) BOOL isRead;

@end

@interface EHIDataStoreRequest (Convenience)

/** Creates a find request for the given collection */
+ (EHIDataStoreRequest *)find:(EHICollection *)collection;
/** Creates a find request for the given collection, limited to 1 */
+ (EHIDataStoreRequest *)first:(EHICollection *)collection;
/** Create a save request for the given model */
+ (EHIDataStoreRequest *)save:(EHIModel *)model;
/** Creates a delete request for the given model */
+ (EHIDataStoreRequest *)remove:(EHIModel *)model;
/** Creates a delete request for the given collection */
+ (EHIDataStoreRequest *)purge:(EHICollection *)collection;

/** Factory for generating a request with the given type */
+ (EHIDataStoreRequest *)requestWithType:(EHIDataStoreRequestType)type;

@end
