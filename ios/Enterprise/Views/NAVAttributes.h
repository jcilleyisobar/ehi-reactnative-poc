//
//  NAVAttributes.h
//  Enterprise
//
//  Created by Alex Koller on 11/16/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NAVAttributes : NSObject
/** A user object to deliver along with the transition (optional) */
@property (strong, nonatomic) id userObject;
/** A callback to deliver along with the transition (optional) */
@property (copy, nonatomic) id handler;
@end
