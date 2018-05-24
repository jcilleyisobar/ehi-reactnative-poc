//
//  EHITransitionManager.h
//  Enterprise
//
//  Created by Alex Koller on 11/30/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

@interface EHITransitionManager : NSObject

+ (void)transitionToScreen:(NSString *)screen asModal:(BOOL)modal;

+ (void)transitionToScreen:(NSString *)screen object:(id)object asModal:(BOOL)modal;

@end
