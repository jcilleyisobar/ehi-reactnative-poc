//
//  EHIEnrollTerms.h
//  Enterprise
//
//  Created by Rafael Ramos on 8/19/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIModel.h"

@interface EHIEnrollTerms : EHIModel
@property (assign, nonatomic) BOOL acceptDecline;
@property (copy  , nonatomic) NSString *acceptDeclineVersion;
@property (copy  , nonatomic) NSDate *acceptDeclineDate;
@end
