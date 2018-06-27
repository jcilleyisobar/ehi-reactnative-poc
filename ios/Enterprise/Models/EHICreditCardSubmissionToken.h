//
//  EHICreditCardSubmissionToken.h
//  Enterprise
//
//  Created by Rafael Ramos on 9/28/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIModel.h"
#import "EHICreditCardPaymentContext.h"

@interface EHICreditCardSubmissionToken : EHIModel
@property (copy  , nonatomic, readonly) NSString *cardSubmissionKey;
@property (strong, nonatomic, readonly) EHICreditCardPaymentContext *context;
@end
