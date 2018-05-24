//
//  EHIConfirmationCarClassViewModel.m
//  Enterprise
//
//  Created by mplace on 6/5/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIConfirmationCarClassViewModel.h"
#import "EHICarClass.h"

@interface EHIConfirmationCarClassViewModel ()
@property (strong, nonatomic) EHICarClass *carClass;
@end

@implementation EHIConfirmationCarClassViewModel

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    if([model isKindOfClass:[EHICarClass class]]) {
        [self setCarClass:model];
    }
}

- (void)setCarClass:(EHICarClass *)carClass
{
    _carClass = carClass;
    
    self.headerTitle       = EHILocalizedString(@"reservation_review_car_class_section_title", @"", @"");
    self.carClassNameTitle = carClass.name;
    self.makeModelTitle    = carClass.makeModelOrSimilar;
    self.transmissionTitle = carClass.transmission;
    self.isAutomatic       = carClass.isAutomaticTransmission;
}

@end
