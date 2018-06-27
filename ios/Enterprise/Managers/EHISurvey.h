//
//  EHISurvey.h
//  Enterprise
//
//  Created by Rafael Ramos on 12/2/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHISurveyKeys.h"
#import "EHISurveySession.h"

#ifdef DEBUG
#define EHISurveyLogging 0
#else
#define EHISurveyLogging 0
#endif

typedef void (^EHISurveyCustomerDetailsValidationHandler)(BOOL isInvalid);

@interface EHISurvey : NSObject
@property (strong, nonatomic, readonly) EHISurveySession *session;
@property (strong, nonatomic, readonly) NSURL *policiesURL;
/** Debug */
@property (assign, nonatomic) BOOL skipPoolingCheck;

+ (instancetype)sharedInstance;

+ (void)prepareToLaunch;
+ (void)showInviteIfNeeded;

- (void)requestSurveyWithCustomerDetails:(NSString *)phoneOrEmail validation:(EHISurveyCustomerDetailsValidationHandler)validationBlock;

/** Debug */
-(void)resetSurveyState;

@end
