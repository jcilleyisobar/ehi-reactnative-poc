//
//  EHIDeliveryCollectionsViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 6/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationStepViewModel.h"
#import "EHIFormFieldToggleViewModel.h"
#import "EHIRequiredInfoViewModel.h"

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSUInteger, EHIDeliveryCollectionSection) {
    EHIDeliveryCollectionSectionInformation,
    EHIDeliveryCollectionSectionDeliveryToggle,
    EHIDeliveryCollectionSectionRequiredInfo,
    EHIDeliveryCollectionSectionDeliveryAddress,
    EHIDeliveryCollectionSectionCollectionToggle,
    EHIDeliveryCollectionSectionCollectionAddress
};

typedef NS_ENUM(NSUInteger, EHIDeliveryCollectionAddressRow) {
    EHIDeliveryCollectionAddressRowStreetOne,
    EHIDeliveryCollectionAddressRowStreetTwo,
    EHIDeliveryCollectionAddressRowCity,
    EHIDeliveryCollectionAddressRowPostal,
    EHIDeliveryCollectionAddressRowPhone,
    EHIDeliveryCollectionAddressRowComments
};

@interface EHIDeliveryCollectionViewModel : EHIReservationStepViewModel <MTRReactive>

@property (copy  , nonatomic) NSString *title;
@property (strong, nonatomic) EHIFormFieldViewModel *deliveryToggleViewModel;
@property (copy  , nonatomic, nullable) NSArray *deliveryAddressSectionViewModels;
@property (strong, nonatomic) EHIFormFieldViewModel *collectionToggleViewModel;
@property (copy  , nonatomic, nullable) NSArray *collectionAddressSectionViewModels;
@property (strong, nonatomic, readonly) EHIRequiredInfoViewModel *requiredModel;

@property (copy  , nonatomic) NSString *saveButtonTitle;
@property (assign, nonatomic) BOOL invalidForm;
@property (assign, nonatomic) BOOL isLoading;

- (id)headerForSection:(EHIDeliveryCollectionSection)section;
- (void)commitDeliveryCollection;

@end

NS_ASSUME_NONNULL_END
