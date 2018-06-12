//
//  EHIDashboardHistoryViewModel.m
//  Enterprise
//
//  Created by mplace on 5/18/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIDashboardHistoryViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHISettings.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHIDashboardHistoryViewModel ()
@property (nonatomic, readonly) BOOL saveSearchHistory;
@end

@implementation EHIDashboardHistoryViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        if(self.saveSearchHistory) {
            [self initializeTrackingEnabled];
        } else {
            [self initializeTrackingDisabled];
        }
    }
    
    return self;
}

//
// Helpers
//

- (void)initializeTrackingEnabled
{
    NSString *details = EHILocalizedString(@"dashboard_history_cell_enabled_details", @"We want to make booking super easy for you. Here we’ll put your past searches and rentals to make your future ones quicker", @"details text for the dashboard history cell with tracking enabled");
    
    _iconImageName = @"icon_clock-1";
    _title = EHILocalizedString(@"dashboard_history_cell_enabled_title", @"Save Time", @"title for the dashboard history cell with tracking enabled");
    _details = [self formattedDetails:details];
}

- (void)initializeTrackingDisabled
{
    NSString *details = EHILocalizedString(@"dashboard_history_cell_disabled_details", @"For quicker reservations, turn your history tracking on to see your recent activity, like past searches and rentals or favorite locations.", @"details text for the dashboard history cell with tracking disabled");
    
    _iconImageName = @"icon_book";
    _details = [self formattedDetails:details];
    _trackButtonTitle = EHILocalizedString(@"dashboard_history_cell_track_button_title", @"TURN HISTORY TRACKING ON", @"track button title for the dashboard history cell");
}

- (NSAttributedString *)formattedDetails:(NSString *)text
{
    return EHIAttributedStringBuilder.new.fontStyle(EHIFontStyleLight, 18).paragraph(4, NSTextAlignmentCenter).text(text).string;
}

# pragma mark - Actions

- (void)enableTracking
{
    self.router.transition
        .root(EHIScreenSettings).start(nil);
}

# pragma mark - Computed

- (BOOL)hidesTitle
{
    return self.title == nil;
}

- (BOOL)hidesTrackButton
{
    return self.trackButtonTitle == nil;
}

# pragma mark - Passthrough

- (BOOL)saveSearchHistory
{
    return [EHISettings sharedInstance].saveSearchHistory;
}

@end

NS_ASSUME_NONNULL_END