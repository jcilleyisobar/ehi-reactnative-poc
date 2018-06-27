//
//  EHILocationsMapListActions.h
//  Enterprise
//
//  Created by Rafael Ramos on 6/21/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

@class EHILocationsMapListCell;
@protocol EHILocationsMapListActions <NSObject> @optional
- (void)locationsMapDidTapSelect:(EHILocationsMapListCell *)sender;
- (void)locationsMapDidTapLocationTitle:(EHILocationsMapListCell *)sender;
- (void)locationsMapDidTapChangeState:(EHILocationsMapListCell *)sender;
@end
