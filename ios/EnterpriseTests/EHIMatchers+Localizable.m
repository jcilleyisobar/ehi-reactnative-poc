//
//  EHIMatchers+Localizable.m
//  Enterprise
//
//  Created by Ty Cobb on 2/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIMatchers+Localizable.h"
#import "EHILocalization.h"
#import "NSString+Formatting.h"

typedef NSString *(^EHIFailureBlock)(void);

EXPMatcherImplementationBegin(localizeFromMap, (NSString *key, NSDictionary *map))

// contruct the expected string
NSString *expected = EHILocalizedString(key, nil, nil);
if(map) {
    expected = [expected ehi_applyReplacementMap:map];
}

if([expected isEqualToString:key]) {
    expected = nil;
}

// define the matching requirements
prerequisite(^BOOL {
    return actual && key;
});

match(^BOOL {
    return [actual isEqualToString:expected];
});

// define the failure messages
EHIFailureBlock(^failures)(EHIFailureBlock) = ^EHIFailureBlock(EHIFailureBlock block) {
    return ^NSString * {
        if(!actual)
            return @"the actual value is nil/null";
        else if(!expected)
            return @"the localized string was nil/null";
        return block();
    };
};

failureMessageForTo(failures(^{
    return [NSString stringWithFormat:@"expected: the localized string %@, got: %@", expected, actual];
}));

failureMessageForNotTo(failures(^{
    return [NSString stringWithFormat:@"expected: not the localized string %@, got: %@", expected, actual];
}));

EXPMatcherImplementationEnd
