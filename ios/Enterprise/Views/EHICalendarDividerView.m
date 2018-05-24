//
//  EHICalendarDividerView.m
//  Enterprise
//
//  Created by Ty Cobb on 3/25/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICalendarDividerView.h"

@implementation EHICalendarDividerView

- (instancetype)initWithFrame:(CGRect)frame
{
    if(self = [super initWithFrame:frame]) {
        self.backgroundColor = [UIColor ehi_grayColor2];
    }
    
    return self;
}

@end
