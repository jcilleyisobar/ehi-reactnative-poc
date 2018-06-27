//
//  EHIDeliveryCollectionCellViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 6/4/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIReservation.h"

typedef NS_ENUM(NSUInteger, EHIDeliveryCollectionCellType) {
    EHIDeliveryCollectionCellTypeButton,
    EHIDeliveryCollectionCellTypeDelivery,
    EHIDeliveryCollectionCellTypeCollection,
    EHIDeliveryCollectionCellTypeSame,
    EHIDeliveryCollectionCellTypeDeliveryUnsupported,
    EHIDeliveryCollectionCellTypeCollectionUnsupported
};

@interface EHIDeliveryCollectionCellViewModel : EHIViewModel

@property (assign, nonatomic, readonly) EHIDeliveryCollectionCellType type;
@property (copy  , nonatomic, readonly) NSString *title;
@property (copy  , nonatomic, readonly) NSString *subtitle;
@property (copy  , nonatomic, readonly) NSString *buttonTitle;
@property (copy  , nonatomic, readonly) NSAttributedString *addressDetails;
@property (copy  , nonatomic, readonly) NSString *phoneDetails;
@property (copy  , nonatomic, readonly) NSAttributedString *commentDetails;
@property (copy  , nonatomic, readonly) NSString *otherInfoTitle;
@property (assign, nonatomic, readonly) BOOL showTitle;

// computed
@property (copy  , nonatomic, readonly) NSString *accessoryImageName;
@property (assign, nonatomic, readonly) BOOL showsComment;

@end

@interface EHIDeliveryCollectionCellViewModel (Generators)
/** Creates the delivery and collection view models from @c reservation. Returns button view model if no D&C. */
+ (NSArray *)viewModelsForReservationAllowsButton:(EHIReservation *)reservation;
/** Creates the delivery and collection view models from @c reservation. Returns empty array if no D&C. */
+ (NSArray *)viewModelsForReservation:(EHIReservation *)reservation;
@end
