//
//  EHIDateTimeProviderProtocol.h
//  Enterprise
//
//  Created by Rafael Ramos on 5/18/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

@protocol EHIDateTimeProviderProtocol <NSObject>
- (NSDate *)pickupDate;
- (NSDate *)pickupTime;
- (NSDate *)returnDate;
- (NSDate *)returnTime;
@end
