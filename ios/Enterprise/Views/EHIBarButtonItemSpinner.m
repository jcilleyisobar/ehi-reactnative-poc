//
//  EHIBarButtonItemSpinner.m
//  Enterprise
//
//  Created by Bruno Fernandes Campos on 7/19/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIBarButtonItemSpinner.h"
#import "EHIActivityIndicator.h"

@interface EHIBarButtonItemSpinner ()
@property (strong, nonatomic) EHIActivityIndicator *activityIndicator;
@end

@implementation EHIBarButtonItemSpinner

+ (EHIBarButtonItemSpinner *)create
{
    EHIActivityIndicator *indicator = [[EHIActivityIndicator alloc] initWithFrame:CGRectMake(0, 0, 20, 20) type:EHIActivityIndicatorTypeSmallWhite];
    
    return [[EHIBarButtonItemSpinner alloc] initWithCustomView:indicator];
}

- (instancetype)initWithCustomView:(UIView *)customView
{
    if(self = [super initWithCustomView:customView]) {
        if([customView isKindOfClass:EHIActivityIndicator.class]) {
            self.activityIndicator = (EHIActivityIndicator *)customView;
        }
    }
    
    return self;
}

- (void)setIsAnimating:(BOOL)isAnimating
{
    [self.activityIndicator setIsAnimating:isAnimating];
}

@end
