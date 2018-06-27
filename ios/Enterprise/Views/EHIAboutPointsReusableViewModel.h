//
//  EHIAboutPointsHistoryViewModel.h
//  Enterprise
//
//  Created by frhoads on 1/13/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

//@class EHIAboutPointsItemViewModel;

@interface EHIAboutPointsReusableViewModel : EHIViewModel

typedef NS_ENUM(NSUInteger, EHIAboutPointsModelType) {
    EHIAboutPointsModelTypeHistory,
    EHIAboutPointsModelTypeTransfer,
    EHIAboutPointsModelTypeLostPoints
};

@property (strong, nonatomic, readonly) NSString *imageName;
@property (strong, nonatomic, readonly) NSString *titleText;
@property (strong, nonatomic, readonly) NSString *subtitleText;
@property (strong, nonatomic, readonly) NSString *buttonText;

- (void)promptPhoneCall;
+ (NSArray *)all;

@end
