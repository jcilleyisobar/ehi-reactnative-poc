//
//  EHIClassDetailsAttributesViewModel.m
//  Enterprise
//
//  Created by mplace on 3/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIClassDetailsAttributesViewModel.h"
#import "EHICarClass.h"

@interface EHIClassDetailsAttributesViewModel ()
@property (copy, nonatomic) NSString *title;
@end

@implementation EHIClassDetailsAttributesViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _passengerInfoModel = [EHIClassDetailsTitledInfoModel new];
        _passengerInfoModel.title = EHILocalizedString(@"class_select_details_passengers_title", @"Passengers", @"text appearing above passenger information on the class select details screen.");
        _passengerInfoModel.imageName = @"icon_passengers";
        
        _luggageInfoModel = [EHIClassDetailsTitledInfoModel new];
        _luggageInfoModel.title = EHILocalizedString(@"class_select_details_luggage_title", @"Luggage", @"text appearing above luggage information on the class select details screen.");
        _luggageInfoModel.imageName = @"icon_luggage";
        
        _makeModelDisclaimer = EHILocalizedString(@"class_details_make_model_annotation", @"or similar model with comparable features", @"text that explains to the user that they are renting either the car pictured or something similar and comparable");
    }
    
    return self;
}

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    if([model isKindOfClass:[EHICarClass class]]) {
        [self updateWithCarClass:model];
    }
}

- (void)updateWithCarClass:(EHICarClass *)carClass
{
    self.passengerInfoModel.info = @(carClass.passengerCapacity).description;
    self.luggageInfoModel.info = @(carClass.luggageCapacity).description;
}

@end
