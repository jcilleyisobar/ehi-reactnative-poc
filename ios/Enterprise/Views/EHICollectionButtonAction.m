//
//  EHICollectionButtonAction.m
//  Enterprise
//
//  Created by Ty Cobb on 2/4/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICollectionButtonAction.h"

@implementation EHICollectionButtonAction

- (instancetype)init
{
    if(self = [super init]) {
        // default to center;
        _alignment = UIControlContentHorizontalAlignmentCenter;
    }
    
    return self;
}

@end
