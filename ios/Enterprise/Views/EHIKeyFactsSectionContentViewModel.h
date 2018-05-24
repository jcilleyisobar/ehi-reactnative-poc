//
//  EHIKeyFactsSectionContentViewModel.h
//  Enterprise
//
//  Created by fhu on 11/4/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIKeyFactsContentViewModel.h"
#import "EHIReservation.h"

typedef NS_ENUM(NSUInteger, EHIKeyFactsSectionContentType) {
    EHIKeyFactsSectionContentTypeDamageLiability,
    EHIKeyFactsSectionContentTypeProtection,
    EHIKeyFactsSectionContentTypeEquipment,
    EHIKeyFactsSectionContentTypeMinimumRequirements,
    EHIKeyFactsSectionContentTypeAdditionalPolicies,
    EHIKeyFactsSectionContentTypeAdditionalLiabilities,
    EHIKeyFactsSectionContentTypeVehicleReturnAndDamages
};

@interface EHIKeyFactsSectionContentViewModel : EHIViewModel <MTRReactive>

@property (assign, nonatomic) BOOL hidesTopThickDivider;
@property (assign, nonatomic) BOOL hidesBottomThickDivider;
// the header is a container which can be tapped to expand the cell
@property (copy  , nonatomic) NSString *headerText;
@property (assign, nonatomic) BOOL isSelected;
// the subheader replaces the header, thus replacing any tap to expand functionality
@property (copy  , nonatomic) NSString *subHeaderText;
@property (copy  , nonatomic) NSString *subHeaderDetailsText;
// the attributed content text to be displayed below the header
@property (copy  , nonatomic) NSAttributedString *contentAttributedText;
// the content list shows inside a collection cell, replacing the content text
@property (strong, nonatomic) NSArray *contentList;

+ (instancetype)modelForType:(EHIKeyFactsSectionContentType)type withReservation:(EHIReservation *)reservation;

@end
    
EHIAnnotatable(EHIKeyFactsSectionContentViewModel)
