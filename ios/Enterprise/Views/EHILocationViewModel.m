//
//  EHILocationViewModel.m
//  Enterprise
//
//  Created by mplace on 2/4/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHILocationViewModel.h"
#import "EHILocationDetailsViewModel.h"
#import "EHICity.h"
#import "EHIReservationBuilder+Analytics.h"

@interface EHILocationViewModel ()
@property (strong, nonatomic) EHILocation *location;
@property (copy  , nonatomic) NSAttributedString *title;
@property (copy  , nonatomic) NSString *subtitle;
@property (copy  , nonatomic) NSAttributedString *tagsText;
@property (copy  , nonatomic) NSString *selectButtonTitle;
@property (copy  , nonatomic) NSString *iconImageName;
@property (assign, nonatomic) BOOL hidesIcon;
@property (assign, nonatomic) BOOL hidesSubtitle;
@property (assign, nonatomic) BOOL hidesSelectButton;
@end

@implementation EHILocationViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _selectButtonTitle = EHILocalizedString(@"location_cell_select_location_text", @"SELECT", @"Text in the accessory view of location cells");
    }
    
    return self;
}

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    // model specific updates
    if([model isMemberOfClass:[EHILocation class]]) {
        [self updateWithLocation:model];
    } else if([model isMemberOfClass:[EHICity class]]) {
        [self updateWithCity:model];
    }
}

# pragma mark - Actions

- (void)showLocationDetails
{
    EHILocationDetailsViewModel *viewModel = [[EHILocationDetailsViewModel alloc] initWithModel:self.location];
    
    [EHIAnalytics trackAction:EHIAnalyticsLocActionDetail handler:^(EHIAnalyticsContext *context) {
        [self.builder encodeLocation:self.location context:context];
    }];
    
    self.router.transition.push(EHIScreenLocationDetails).object(viewModel).start(nil);
}

# pragma mark - Model Paths

- (void)updateWithLocation:(EHILocation *)location
{
    self.location = location;
    
    self.title          = [self titleForLocation:location];
    self.subtitle       = location.hidesDetails ? nil : location.address.formattedAddress;
    self.tagsText       = [self tagsForLocation:location];
    self.iconImageName  = [self iconImageNameForLocation:location];
    
    // update hidden content
    self.hidesIcon          = self.iconImageName == nil;
    self.hidesSubtitle      = location.hidesDetails;
    self.hidesSelectButton  = NO;
}

- (void)updateWithCity:(EHICity *)city
{
    self.location = nil;
   
    self.title          = [self titleForText:city.formattedName];
    self.subtitle       = nil;
    self.tagsText       = nil;
    self.iconImageName  = nil;
    
    // update hidden content
    self.hidesIcon          = YES;
    self.hidesSubtitle      = YES;
    self.hidesSelectButton  = YES;
}

//
// Helpers
//

- (NSAttributedString *)titleForLocation:(EHILocation *)location
{
    EHIAttributedStringBuilder *title = [EHIAttributedStringBuilder new]
        .text(location.displayName).fontStyle(EHIFontStyleLight, 23.0f).color(UIColor.ehi_greenColor);
    
    if(location.type == EHILocationTypeAirport && location.airportCode) {
        title.space.appendText(location.airportCode).fontStyle(EHIFontStyleLight, 18.0f).color(UIColor.ehi_blackColor);
    }
    
    return title.string;
}

- (NSAttributedString *)titleForText:(NSString *)text
{
    return [NSAttributedString attributedStringWithString:text font:[UIFont ehi_fontWithStyle:EHIFontStyleLight size:23.0f] color:[UIColor ehi_greenColor]];
}

- (NSAttributedString *)tagsForLocation:(EHILocation *)location
{
    // ensure we're showing the expanded cell
    if(location.hidesDetails) {
        return nil;
    }
   
    // ensure we have the any tags to render
    NSString *distanceTag = location.isNearbyLocation ? location.distanceTag : nil;
    
    if(!distanceTag) {
        return nil;
    }
   
    return EHIAttributedStringBuilder.new
        .fontStyle(EHIFontStyleRegular, 14.0f)
        .color(UIColor.ehi_blackColor)
        .appendText(distanceTag)
        .string;    
}

- (NSString *)iconImageNameForLocation:(EHILocation *)location
{
    if(location.isExotics) {
        return @"icon_exotics";
    }
    
    switch(self.location.brand) {
        case EHILocationBrandAlamo:
            return @"map_pin_alamo";
        case EHILocationBrandNational:
            return @"map_pin_national";
        default: break;
    }

    switch(location.type) {
        case EHILocationTypeAirport:
            return @"icon_airport_green";
        case EHILocationTypeTrain:
            return @"icon_train_01";
        case EHILocationTypePort:
            return @"icon_portofcall_01";
        default: return nil;
    }
}

# pragma mark - Accessors

- (EHIReservationBuilder *)builder
{
    return [EHIReservationBuilder sharedInstance];
}

# pragma mark - MTRReactive

+ (NSArray *)nonreactiveProperties:(EHILocationViewModel *)model
{
    return @[
        @key(model.location),
    ];
}

@end
