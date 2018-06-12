//
//  EHILocationsMapOffbrandBanner.m
//  Enterprise
//
//  Created by Ty Cobb on 4/27/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationsMapOffbrandBanner.h"

@interface EHILocationsMapOffbrandBanner ()
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@end

@implementation EHILocationsMapOffbrandBanner

- (void)setTitle:(NSString *)title
{
    self.titleLabel.text = title;
}

- (NSString *)title
{
    return self.titleLabel.text;
}

@end
