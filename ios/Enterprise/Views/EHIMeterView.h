//
//  EHIMeterView.h
//  Enterprise
//
//  Created by Rafael Ramos on 1/9/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

//#import "EHIView.h"

#import "EHIGaugeStructure.h"

@interface EHIMeterView : UIView
@property (assign, nonatomic) CGFloat fill;
@property (assign, nonatomic) EHIMeterData meterData;
@end
