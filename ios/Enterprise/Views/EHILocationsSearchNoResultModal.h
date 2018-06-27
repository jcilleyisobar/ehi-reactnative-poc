//
//  EHILocationsSearchNoResultModal.h
//  Enterprise
//
//  Created by Rafael Ramos on 27/07/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIInfoModalViewModel.h"
#import "EHILocationFilterQuery.h"

typedef void (^EHILocationsSearchNoResultModalHandler)(BOOL);

@interface EHILocationsSearchNoResultModal : EHIInfoModalViewModel
- (instancetype)initWithFilterQuery:(EHILocationFilterQuery *)filterQuery;
- (void)presentWithCompletion:(EHILocationsSearchNoResultModalHandler)handler;
@end
