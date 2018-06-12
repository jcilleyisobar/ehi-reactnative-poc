//
//  EHIDeliveryCollectionCellViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 6/4/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIDeliveryCollectionCellViewModel.h"
#import "EHIReservationBuilder.h"
#import "EHIPhoneNumberFormatter.h"

@interface EHIDeliveryCollectionCellViewModel ()
@property (assign, nonatomic) EHIDeliveryCollectionCellType type;
@property (copy  , nonatomic) NSString *subtitle;
@property (copy  , nonatomic) NSString *title;
@property (copy  , nonatomic) NSAttributedString *addressDetails;
@property (copy  , nonatomic) NSString *phoneDetails;
@property (copy  , nonatomic) NSAttributedString *commentDetails;
@property (copy  , nonatomic) NSString *otherInfoTitle;
@property (assign, nonatomic) BOOL showTitle;
@end

@implementation EHIDeliveryCollectionCellViewModel

+ (instancetype)viewModelWithType:(EHIDeliveryCollectionCellType)type
{
    EHIDeliveryCollectionCellViewModel *viewModel = [EHIDeliveryCollectionCellViewModel new];
    viewModel.type = type;
    
    return viewModel;
}

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _buttonTitle = EHILocalizedString(@"reservation_review_delivery_collection_button_title", @"USE DELIVERY & COLLECTION", @"");
    };
    
    return self;
}

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    if([model isKindOfClass:[EHIDeliveryCollectionInfo class]]) {
        [self updateWithDeliveryCollectionInfo:model];
    }
}
       
//
// Helpers
//

- (void)updateWithDeliveryCollectionInfo:(EHIDeliveryCollectionInfo *)info
{
    self.addressDetails = [self formattedStringForText:[info.address formattedAddress:YES]];
    self.phoneDetails   = [EHIPhoneNumberFormatter format:info.phone.number countryCode:[NSLocale ehi_region]];
    self.commentDetails = [self formattedStringForText:info.comments];
}

# pragma mark - Setters

- (void)setType:(EHIDeliveryCollectionCellType)type
{
    _type = type;
    
    self.subtitle = type == EHIDeliveryCollectionCellTypeDelivery
        ? EHILocalizedString(@"delivery_collection_row_delivery_details_title", @"DELIVERY DETAILS", @"")
        : EHILocalizedString(@"delivery_collection_row_collection_details_title", @"COLLECTION DETAILS", @"");
    
    self.otherInfoTitle = [self otherInfoTitleForType:type];
}

//
// Helpers
//

- (NSAttributedString *)formattedStringForText:(NSString *)text
{
    if(!text) {
        return nil;
    }
    
    return EHIAttributedStringBuilder.new.size(18).lineSpacing(4)
        .text(text).string;
}

- (NSString *)otherInfoTitleForType:(EHIDeliveryCollectionCellType)type
{
    switch (type) {
        case EHIDeliveryCollectionCellTypeCollectionUnsupported:
            return EHILocalizedString(@"delivery_collection_unavailable_collection_title", @"Collection service is not available at your chosen location.", @"");
            case EHIDeliveryCollectionCellTypeDeliveryUnsupported:
            return EHILocalizedString(@"delivery_collection_unavailable_delivery_title", @"Delivery service is not available at your chosen location.", @"");
            case EHIDeliveryCollectionCellTypeSame:
            return EHILocalizedString(@"delivery_collection_same_as_delivery_title", @"Same as delivery details.", @"");
        default: return nil;
    }
}

# pragma mark - Computed

- (NSString *)accessoryImageName
{
    return @"arrow_smgreen";
}

- (BOOL)showsComment
{
    return self.commentDetails.length != 0;
}

- (NSString *)title
{
    return self.showTitle ? EHILocalizedString(@"reservation_review_delivery_collection_section_title", @"DELIVERY & COLLECTION", @"") : nil;
}

@end

@implementation EHIDeliveryCollectionCellViewModel (Generators)

+ (NSArray *)viewModelsForReservationAllowsButton:(EHIReservation *)reservation;
{
    // if no delivery or collection info, allow user to add
    if(!reservation.vehicleLogistics.deliveryInfo && !reservation.vehicleLogistics.collectionInfo) {
        EHIDeliveryCollectionCellViewModel *model = [EHIDeliveryCollectionCellViewModel viewModelWithType:EHIDeliveryCollectionCellTypeButton];
        model.showTitle = YES;
        return @[model];
    }
    
    return [self viewModelsForReservation:reservation];
}

+ (NSArray *)viewModelsForReservation:(EHIReservation *)reservation;
{
    // generate view models from delivery and/or collection
    EHIVehicleLogistics *logistics = reservation.vehicleLogistics;
    EHIDeliveryCollectionCellType collectionType = logistics.hasSameDeliveryAndCollection ? EHIDeliveryCollectionCellTypeSame : EHIDeliveryCollectionCellTypeCollection;
    EHIDeliveryCollectionCellViewModel *deliveryViewModel = logistics.deliveryInfo ? [EHIDeliveryCollectionCellViewModel viewModelWithType:EHIDeliveryCollectionCellTypeDelivery] : nil;
    EHIDeliveryCollectionCellViewModel *collectionViewModel = logistics.collectionInfo ? [EHIDeliveryCollectionCellViewModel viewModelWithType:collectionType] : nil;
    
    // if the location does not allow collection but delivery has been selected, show the default unsupported message
    if(!reservation.allowsCollection && logistics.deliveryInfo) {
        collectionViewModel = [EHIDeliveryCollectionCellViewModel viewModelWithType:EHIDeliveryCollectionCellTypeCollectionUnsupported];
    }
    
    // if the location does not allow delivery but collection has been selected, show the default unsupported message
    if(!reservation.allowsDelivery && logistics.collectionInfo) {
        deliveryViewModel = [EHIDeliveryCollectionCellViewModel viewModelWithType:EHIDeliveryCollectionCellTypeDeliveryUnsupported];
    }
    
    // update view models
    [deliveryViewModel updateWithModel:logistics.deliveryInfo];
    [collectionViewModel updateWithModel:logistics.collectionInfo];

    // gather models that exist
    NSArray *models = [NSMutableArray.new.push(deliveryViewModel).push(collectionViewModel) copy];
    
    return models.each(^(EHIDeliveryCollectionCellViewModel *model, int index) {
        if(index == 0) {
            model.showTitle = YES;
        }
    });
}

@end