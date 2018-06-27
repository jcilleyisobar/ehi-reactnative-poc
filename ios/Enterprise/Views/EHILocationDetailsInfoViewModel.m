//
//  EHILocationDetailsInfoViewModel.m
//  Enterprise
//
//  Created by Ty Cobb on 2/25/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationDetailsInfoViewModel.h"
#import "EHIFavoritesManager.h"
#import "EHIAnalytics.h"
#import "EHIViewModel_Subclass.h"
#import "EHILocationDetailsAfterHoursModalViewModel.h"
#import "EHILocationConflictDataProvider.h"

@interface EHILocationDetailsInfoViewModel ()
@property (strong, nonatomic) EHILocation *location;
@property (strong, nonatomic) EHILocationConflictDataProvider *provider;
@property (copy  , nonatomic) NSAttributedString *title;
@property (copy  , nonatomic) NSString *address;
@property (copy  , nonatomic) NSString *phoneNumber;
@property (copy  , nonatomic) NSString *favoritesTitle;
@property (copy  , nonatomic) NSAttributedString *afterHoursTitle;
@property (assign, nonatomic) BOOL hideAfterHours;
@property (assign, nonatomic) BOOL isFavorited;
@property (assign, nonatomic) BOOL hasWayfindingDirections;
@end

@implementation EHILocationDetailsInfoViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _directionsTitle = EHILocalizedString(@"location_details_get_directions_title", @"GET DIRECTIONS", @"");
        _wayfindingTitle = EHILocalizedString(@"terminal_directions_header_title", @"DIRECTIONS FROM TERMINAL TITLE", @"");
    }
    
    return self;
}

- (void)updateWithModel:(EHILocation *)location
{
    [super updateWithModel:location];
    
    if([location isKindOfClass:[EHILocation class]]) {
        [self updateWithLocation:location];
    }
}

- (void)updateWithLocation:(EHILocation *)location
{
    self.location = location;
    
    self.title       = [self attributedTitleForLocation:location];
    self.address     = [location.address formattedAddress:YES];
    self.phoneNumber = location.formattedPhoneNumber;
    self.isFavorited = location.isFavorited;
    self.hasWayfindingDirections = [self locationSupportsWayfinding:location];
    self.afterHoursTitle = [self afterHoursTitleForLocation:location];
    self.hideAfterHours  = self.afterHoursTitle == nil;
}

- (NSAttributedString *)attributedTitleForLocation:(EHILocation *)location
{
    if(!location.displayName) {
        return nil;
    }
    
    // default title is the display name
    EHIAttributedStringBuilder *builder = EHIAttributedStringBuilder.new
        .text(location.displayName).fontStyle(EHIFontStyleLight, 24.0f);
    
    // if its an airport, append the airport code
    if(location.type == EHILocationTypeAirport) {
        builder.space
            .appendText(location.airportCode).fontStyle(EHIFontStyleLight, 18.0f).color(UIColor.ehi_grayColor4);
    }
    
    return builder.string;
}

- (BOOL)locationSupportsWayfinding:(EHILocation *)location
{
    return location.type == EHILocationTypeAirport
        && location.isOnBrand
        && location.wayfindings.count;
}

# pragma mark - Terminal Directions

- (NSArray *)wayfindings
{
    return self.location.wayfindings;
}

# pragma mark - Favorites

- (void)toggleIsFavorited
{
    [EHIAnalytics trackAction:EHIAnalyticsLocActionFavorite handler:nil];
    // attempt to update the stored favorited state
    [[EHIFavoritesManager sharedInstance] updateLocation:self.location isFavorited:!self.isFavorited];
    // and then use whatever state the location says it is
    self.isFavorited = self.location.isFavorited;
}

- (void)setIsFavorited:(BOOL)isFavorited
{
    _isFavorited = isFavorited;
    // keep the title in sync
    self.favoritesTitle = [self titleForFavoritedState:isFavorited];
}

- (NSString *)titleForFavoritedState:(BOOL)isFavorited
{
    if(isFavorited) {
        return EHILocalizedString(@"location_details_favorited_title", @"FAVORITE\nLOCATION", @"Title for favorite button 'favorited' state");
    } else {
        return EHILocalizedString(@"location_details_not_favorited_title", @"ADD\nFAVORITE", @"Title for favorite button 'not favorited' state");
    }
}

# pragma mark - Actions

- (void)showDirections
{
    [EHIAnalytics trackAction:EHIAnalyticsActionGetDirections handler:nil];
    
    [UIApplication ehi_promptDirectionsForLocation:self.location];
}

- (void)showDirectionsFromTerminal
{
    self.router.transition.push(EHIScreenLocationWayfinding).object(self.wayfindings).start(nil);
}

- (void)callLocation
{
    [EHIAnalytics trackAction:EHIAnalyticsLocActionCallUs handler:nil];
    
    [UIApplication ehi_promptPhoneCall:self.location.formattedPhoneNumber];
}

- (NSAttributedString *)afterHoursTitleForLocation:(EHILocation *)location
{
    self.provider = EHILocationConflictDataProvider.new.location(location).oneWay(self.isOneWay);

    __weak typeof(self) welf = self;
    self.provider.afterHoursBlock = ^{
        [welf showAfterHoursModal];
    };
    
    return self.provider.afterHours;
}

# pragma mark - Accessors

- (BOOL)hideExotics
{
    return !self.location.isExotics;
}

- (BOOL)isOnBrand
{
    return self.location.isOnBrand;
}

//
// Helpers
//

- (void)showAfterHoursModal
{
	EHILocationDetailsAfterHoursModalViewModel *modal = [EHILocationDetailsAfterHoursModalViewModel new];
    [modal present:^BOOL(NSInteger index, BOOL canceled) {
        return YES;
    }];
}

# pragma mark - MTRReactive

+ (NSArray *)nonreactiveProperties:(EHILocationDetailsInfoViewModel *)model
{
    return @[
        @key(model.location),
    ];
}

@end
