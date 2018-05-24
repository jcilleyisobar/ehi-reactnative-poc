//
//  EHIMatchers+Localizable.h
//  Enterprise
//
//  Created by Ty Cobb on 2/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import <Expecta/Expecta.h>

#define localizeFrom(_key) localizeFromMap(_key, nil)
EXPMatcherInterface(localizeFromMap, (NSString *key, NSDictionary *map));
