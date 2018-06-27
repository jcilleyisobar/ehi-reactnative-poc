//
//  EHIDashboardQuickstartViewModel.m
//  Enterprise
//
//  Created by Ty Cobb on 5/20/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIDashboardQuickstartViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHILocation.h"
#import "EHIReservation.h"
#import "EHIHistoryManager.h"
#import "EHIFavoritesManager.h"
#import "EHILocationMapPinAssetFactory.h"

typedef NS_ENUM(NSInteger, EHIDashboardQuickstartType) {
    EHIDashboardQuickstartTypePast,
    EHIDashboardQuickstartTypeAbandoned,
    EHIDashboardQuickstartTypeFavorite
};

@interface EHIDashboardQuickstartViewModel ()
@property (strong, nonatomic) id model;
@property (assign, nonatomic) EHIDashboardQuickstartType type;
@property (copy  , nonatomic) NSString *title;
@property (copy  , nonatomic) NSString *subtitle;
@property (copy  , nonatomic) NSString *iconName;
@property (copy  , nonatomic) NSString *deletedTitle;
@property (assign, nonatomic) BOOL alignsIconLeft;
@property (assign, nonatomic) BOOL isDeleted;
@end

@implementation EHIDashboardQuickstartViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _undoTitle = EHILocalizedString(@"standard_undo_button", @"UNDO", @"");
    }
    
    return self;
}

- (void)didResignActive
{
    [super didResignActive];
    
    // commit deletion when view is removed
    if(self.isDeleted) {
        switch(self.type) {
            case EHIDashboardQuickstartTypePast:
                [[EHIHistoryManager sharedInstance] deletePastRental:self.model];
            case EHIDashboardQuickstartTypeAbandoned:
                [[EHIHistoryManager sharedInstance] deleteAbandonedRental:self.model];
            case EHIDashboardQuickstartTypeFavorite:
                [[EHIFavoritesManager sharedInstance] updateLocation:self.model isFavorited:NO];
            default:
                break;
        }
    }
    
    // reset
    self.isDeleted = NO;
}

# pragma mark - Model Binding

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];

    self.model = model;
    
    if([model isKindOfClass:[EHILocation class]]) {
        [self updateWithFavorite:model];
    } else if([model isKindOfClass:[EHIReservation class]]) {
        [self updateWithReservation:model];
    }
}

- (void)updateWithFavorite:(EHILocation *)location
{
    self.type     = EHIDashboardQuickstartTypeFavorite;
    self.title    = location.displayName;
    self.subtitle = nil;
    self.iconName = @"icon_favorites_02";
    self.deletedTitle = EHILocalizedString(@"delete_quickstart_favorite_title", @"Unfavorited", @"");
    self.alignsIconLeft = NO;
}

- (void)updateWithReservation:(EHIReservation *)reservation
{
    self.type     = reservation.isPast ? EHIDashboardQuickstartTypePast : EHIDashboardQuickstartTypeAbandoned;
    self.title    = reservation.pickupLocation.displayName;
    self.subtitle = [self subtitleForReservation:reservation];
    self.iconName = reservation.isPast ? [EHILocationMapPinAssetFactory assetForLocation:reservation.pickupLocation] : @"icon_search";
    self.deletedTitle = EHILocalizedString(@"delete_quickstart_reservation_title", @"Removed", @"");
    
    // align icon left for the map pins, to deal with the shadow
    self.alignsIconLeft = reservation.isPast;
}

//
// Helpers
//

- (NSString *)subtitleForReservation:(EHIReservation *)reservation
{
    NSString *result = nil;
    
    // use the address for past rentals
    if(reservation.isPast) {
        result = reservation.pickupLocation.address.formattedAddress;
    }
    // otherwise format a pickup/return dates string
    else {
        NSString *template   = @"MMM dd";
        NSString *pickupDate = [reservation.pickupTime ehi_stringForTemplate:template];
        NSString *returnDate = [reservation.returnTime ehi_stringForTemplate:template];
        result = [[NSString alloc] initWithFormat:@"%@ - %@", pickupDate, returnDate];
    }

    return result;
}

# pragma mark - Actions

- (void)deleteQuickstart
{
    self.isDeleted = YES;
}

- (void)undoDelete
{
    self.isDeleted = NO;
}

# pragma mark - Accessors

- (NSString *)typeName
{
    switch(self.type) {
        case EHIDashboardQuickstartTypeAbandoned:
            return EHILocalizedString(@"quickstart_abandoned_type", @"REUSE SEARCH", @"Name for the abandoned rental quickstart type");
        case EHIDashboardQuickstartTypePast:
            return EHILocalizedString(@"quickstart_past_type", @"PAST LOCATION", @"Name for the past rental quickstart type");
        case EHIDashboardQuickstartTypeFavorite:
            return EHILocalizedString(@"quickstart_favorite_type", @"FAVORITE", @"Name for the favorite quickstart type");
    }
}

@end
