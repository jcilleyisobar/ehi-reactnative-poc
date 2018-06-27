//
//  EHILocationsSearchNoResultModal.m
//  Enterprise
//
//  Created by Rafael Ramos on 27/07/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHILocationsSearchNoResultModal.h"
#import "EHIViewModel_Subclass.h"

@interface EHILocationsSearchNoResultModal ()
@property (strong, nonatomic) EHILocationFilterQuery *filterQuery;
@property (copy  , nonatomic) EHILocationsSearchNoResultModalHandler handler;
@property (assign, nonatomic) BOOL wantsEdit;
@end

@implementation EHILocationsSearchNoResultModal

- (instancetype)initWithFilterQuery:(EHILocationFilterQuery *)filterQuery
{
    if(self = [super init]) {
        self.filterQuery = filterQuery;
    }
    
    return self;
}

- (void)didBecomeActive
{
    [EHIAnalytics changeScreen:EHIScreenLocationSearchNoResult state:EHIScreenLocationSearchNoResult];
    [EHIAnalytics trackState:nil];
}

- (NSString *)title
{
    return EHILocalizedString(@"no_locations_modal_title", @"No Locations found", @"");
}

- (NSString *)details
{
    return EHILocalizedString(@"no_locations_modal_details", @"We didn’t find any locations that matched your search filters", @"");
}

- (NSString *)firstButtonTitle
{
    return EHILocalizedString(@"no_locations_modal_edit_filters", @"EDIT FILTERS", @"");
}

- (NSString *)secondButtonTitle
{
    return EHILocalizedString(@"no_locations_modal_clear_all", @"CLEAR ALL FILTERS", @"");
}

- (BOOL)hidesCloseButton
{
    return YES;
}

- (EHIInfoModalButtonLayout)buttonLayout
{
    return EHIInfoModalButtonLayoutSecondaryDismiss;
}

- (void)dismissWithCompletion:(void (^)(void))completion
{
    [super dismissWithCompletion:completion];
    
    ehi_call(self.handler)(self.wantsEdit);
}

- (void)presentWithCompletion:(EHILocationsSearchNoResultModalHandler)handler
{
    self.handler = handler;
    [self present:^BOOL(NSInteger index, BOOL canceled) {
        BOOL wantsEdit = index == 0;
        if(wantsEdit) {
            [EHIAnalytics trackAction:EHIAnalyticsLocSearchNoResultActionEdit handler:self.encodeFilterQuery];
        } else {
            [EHIAnalytics trackAction:EHIAnalyticsLocSearchNoResultActionClearFilters handler:self.encodeFilterQuery];
        }
        
        self.wantsEdit = wantsEdit;
        
        return YES;
    }];
}

//
// Helpers
//

- (void (^)(EHIAnalyticsContext *))encodeFilterQuery
{
    __weak typeof(self) welf = self;
    return ^(EHIAnalyticsContext *context) {
        [context encode:[EHILocationFilterQuery class] encodable:welf.filterQuery];
    };
}

@end
