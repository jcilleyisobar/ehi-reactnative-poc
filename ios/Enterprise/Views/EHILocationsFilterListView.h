//
//  EHILocationsFilterListView.h
//  Enterprise
//
//  Created by Rafael Ramos on 5/4/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIView.h"

@interface EHILocationsFilterListView : EHIView

@end

@protocol EHILocationsFilterListActions <NSObject>
- (void)filterListDidTapOnSection:(NSNumber *)section;
- (void)filterListDidTap:(EHILocationsFilterListView *)sender;
- (void)filterListDidClearDates;
- (void)filterListDidClearFilters;
@end
