//
//  EHIRNFactory.h
//  Enterprise
//
//  Created by Jeff Cilley on 6/27/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <React/RCTRootView.h>
#import "EHIRNFactory.h"

@interface EHIRNManager : NSObject

+ (EHIRNManager *)sharedInstance;

@property(readonly) EHIRNFactory *factory;

- (void)bindViewConstraints:(UIView *)view toSuperView:(UIView *)superview;

@end
