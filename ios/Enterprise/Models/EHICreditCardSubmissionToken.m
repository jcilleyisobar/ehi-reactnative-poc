//
//  EHICreditCardSubmissionToken.m
//  Enterprise
//
//  Created by Rafael Ramos on 9/28/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHICreditCardSubmissionToken.h"

@implementation EHICreditCardSubmissionToken

+ (NSDictionary *)mappings:(EHICreditCardSubmissionToken *)model
{
    return @{
        @"card_submission_key"  : @key(model.cardSubmissionKey),
        @"payment_context_data" : @key(model.context)
    };
}

@end
