//
//  EHIActivityButton.h
//  Enterprise
//
//  Created by Ty Cobb on 5/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIButton.h"
#import "EHILoadable.h"
#import "EHIActivityIndicator.h"

@interface EHIActivityButton : EHIButton <EHILoadable>
@property (assign, nonatomic) BOOL isLoading;
@property (assign, nonatomic) BOOL isDisabledWhileLoading;
@property (assign, nonatomic) EHIActivityIndicatorType indicatorType;
@end
