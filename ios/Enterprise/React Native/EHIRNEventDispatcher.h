//
//  EHIRNEventDispatcher.h
//  Enterprise
//
//  Created by Jeff Cilley on 6/29/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

// CalendarManager.h
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

@interface EHIRNEventDispatcher : RCTEventEmitter <RCTBridgeModule>

- (void)updateAEMEnvironment;

@end
