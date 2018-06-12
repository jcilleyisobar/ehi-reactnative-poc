//
//  EHINetworkCancelable.h
//  Enterprise
//
//  Created by Ty Cobb on 1/14/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@protocol EHINetworkCancelable <NSObject>

/**
 @brief Cancels this network event
 
 If the event has already been canceled, this method does nothing.
 
 @return @c YES is this item was successfuly canceled or has already been canceled.
*/

- (BOOL)cancel;

@end

@interface EHINetworkCancelableGroup : NSObject <EHINetworkCancelable>
/** Adds a cancelable entity to this group. When @c -cancel is called, all items in the group are canceled */
- (void)addCancelable:(id<EHINetworkCancelable>)cancelable;
@end
