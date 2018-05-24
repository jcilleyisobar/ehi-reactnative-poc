//
//  EHIDashboardHeroImageViewModel.m
//  Enterprise
//
//  Created by fhu on 9/14/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIDashboardHeroImageViewModel.h"
#import "EHIViewModel_Subclass.h"

@implementation EHIDashboardHeroImageViewModel

- (instancetype)init
{
    if(self = [super init]) {
        _headerText = EHILocalizedString(@"dashboard_image_caption", @"More than 7,000 locations worldwide.", @"");
    }
    
    return self;
}

- (void)didBecomeActive
{
    [super didBecomeActive];
    
    [self generateNewImage];
}

- (void)generateNewImage
{
    switch (arc4random_uniform(12)) {
        case 0:
            _locationText = EHILocalizedString(@"dashboard_image_martinique_title", @"DUBROVNIK, CROATIA", @"");
            _imageName = @"01_france";
            break;
        case 1:
            _locationText = EHILocalizedString(@"dashboard_image_london_title", @"DUBROVNIK, CROATIA", @"");
            _imageName = @"04_london";
            break;
        case 2:
            _locationText = EHILocalizedString(@"dashboard_image_dubrovnik_title", @"DUBROVNIK, CROATIA", @"");
            _imageName = @"05_croatia";
            break;
        case 3:
            _locationText = EHILocalizedString(@"dashboard_image_majorca_title", @"DUBROVNIK, CROATIA", @"");
            _imageName = @"06_spain";
            break;
        case 4:
            _locationText = EHILocalizedString(@"dashboard_image_santa_monica_title", @"DUBROVNIK, CROATIA", @"");
            _imageName = @"07_california";
            break;
        case 5:
            _locationText = EHILocalizedString(@"dashboard_image_istanbul_title", @"DUBROVNIK, CROATIA", @"");
            _imageName = @"08_istanbul";
            break;
        case 6:
            _locationText = EHILocalizedString(@"dashboard_image_isle_of_wight_title", @"DUBROVNIK, CROATIA", @"");
            _imageName = @"09_england";
            break;
        case 7:
            _locationText = EHILocalizedString(@"dashboard_image_archorage_title", @"DUBROVNIK, CROATIA", @"");
            _imageName = @"10_alaska";
            break;
        case 8:
            _locationText = EHILocalizedString(@"dashboard_image_geneva_title", @"DUBROVNIK, CROATIA", @"");
            _imageName = @"11_geneva";
            break;
        case 9:
            _locationText = EHILocalizedString(@"dashboard_image_durango_title", @"DUBROVNIK, CROATIA", @"");
            _imageName = @"12_colorado";
            break;
        case 10:
            _locationText = EHILocalizedString(@"dashboard_image_aberdeen_title", @"DUBROVNIK, CROATIA", @"");
            _imageName = @"13_scotland";
            break;
        case 11:
            _locationText = EHILocalizedString(@"dashboard_image_tirana_title", @"DUBROVNIK, CROATIA", @"");
            _imageName = @"14_albania";
            break;
    }
}

@end
