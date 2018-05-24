//
//  EHIInfoModalModelable.h
//  Enterprise
//
//  Created by Ty Cobb on 7/1/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICarClassExtra.h"
#import "EHILocationPolicy.h"
#import "EHIPromotionContract.h"
#import "EHIUserPaymentMethod.h"

/** 
 Conformance to this protocol allows models to be bound to the @c EHIInfoModalViewModel 
 using @c -updateWithModel:
 
 Attempting to update an instance of @c EHIInfoModalViewModel with any data source not
 conforming to this protocol has no effect
*/

@protocol EHIInfoModalModelable <NSObject>

/** The title text to render in the info modal */
@property (copy, nonatomic, readonly) NSString *infoTitle;
/** The details text to render in the info modal */
@property (copy, nonatomic, readonly) NSString *infoDetails;

@optional
/** An optional id that uniquely identifies the data element */
@property (copy, nonatomic, readonly) NSString *infoId;

@end

//
// Default implementations of EHIInfoModalModelable for typical types
//

@interface EHICarClassExtra (InfoModal) <EHIInfoModalModelable> @end
@interface EHILocationPolicy (InfoModal) <EHIInfoModalModelable> @end
@interface EHIPromotionContract (InfoModal) <EHIInfoModalModelable> @end
@interface EHIUserPaymentMethod (InfoModal) <EHIInfoModalModelable> @end
