//
//  EHIDateTimeComponentMapView.h
//  Enterprise
//
//  Created by Rafael Ramos on 5/3/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIView.h"

@interface EHIDateTimeComponentMapView : EHIView

@end

@protocol EHIDateTimeComponentMapViewActions <NSObject>
- (void)dateTimeComponentDidTapOnSection:(NSNumber *)section;
- (void)dateTimeComponentDidTapClear;
- (void)dateTimeComponentDidTap:(EHIDateTimeComponentMapView *)sender;
@end
