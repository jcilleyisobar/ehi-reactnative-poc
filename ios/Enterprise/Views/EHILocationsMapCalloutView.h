//
//  EHILocationCalloutView.h
//  Enterprise
//
//  Created by Ty Cobb on 1/30/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIView.h"

@interface EHILocationsMapCalloutView : EHIView

@end

@protocol EHILocationsMapCalloutViewActions <NSObject>
- (void)calloutViewDidTapSelect:(EHILocationsMapCalloutView *)sender;
- (void)calloutViewDidTapLocationTitle:(EHILocationsMapCalloutView *)sender;
- (void)calloutViewDidTapChangeState:(EHILocationsMapCalloutView *)sender;
@end
