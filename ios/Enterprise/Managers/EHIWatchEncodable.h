//
//  EHIWatchEncodable.h
//  Enterprise
//
//  Created by Michael Place on 10/19/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol EHIWatchEncodable <NSObject>

- (NSDictionary *)encodeForWatch;

@end
