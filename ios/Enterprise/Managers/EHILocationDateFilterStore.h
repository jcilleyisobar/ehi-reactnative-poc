//
//  EHILocationDateFilterStore.h
//  Enterprise
//
//  Created by Rafael Ramos on 5/30/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

@interface EHILocationDateFilterStore : NSObject
@property (strong, nonatomic) NSDate *pickupDate;
@property (strong, nonatomic) NSDate *pickupTime;
@property (strong, nonatomic) NSDate *returnDate;
@property (strong, nonatomic) NSDate *returnTime;

- (void)clearReturnTime;
- (void)clearPickupTime;

@end
