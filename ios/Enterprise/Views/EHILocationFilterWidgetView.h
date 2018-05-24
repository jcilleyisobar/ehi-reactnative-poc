//
//  EHILocationFilterWidgetView.h
//  Enterprise
//
//  Created by Rafael Machado on 18/05/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIView.h"

typedef NS_ENUM(NSInteger, EHILocationFilterWidgetLayout) {
    EHILocationFilterWidgetLayoutRegular,
    EHILocationFilterWidgetLayoutInsideContainer,
};

@interface EHILocationFilterWidgetView : EHIView

@end

@protocol EHILocationFilterWidgetViewActions <NSObject>
- (void)locationFilterWidgetTapped:(EHILocationFilterWidgetView *)sender;
@end
