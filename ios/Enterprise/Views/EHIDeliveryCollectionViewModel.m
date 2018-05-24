//
//  EHIDeliveryCollectionsViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 6/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationStepViewModel_Subclass.h"
#import "EHIDeliveryCollectionViewModel.h"
#import "EHISectionHeaderModel.h"
#import "EHIFormFieldLabelViewModel.h"
#import "EHIFormFieldTextViewModel.h"
#import "EHIFormFieldTextViewViewModel.h"
#import "EHIUser.h"
#import "EHIServices+Reservation.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHIDeliveryCollectionViewModel () <EHIFormFieldDelegate>
@property (strong, nonatomic) NSDictionary *sectionHeaders;
@property (strong, nonatomic) EHIFormFieldToggleViewModel *sameAddressToggle;
@property (strong, nonatomic) NSArray *deliveryAddressViewModels;
@property (strong, nonatomic) NSArray *collectionAddressViewModels;
@property (nonatomic, readonly) BOOL hasEnabledDelivery;
@property (nonatomic, readonly) BOOL hasEnabledCollection;
@property (nonatomic, readonly) BOOL isUsingDeliveryAddress;
@property (nonatomic, readonly) BOOL isUsingCollectionAddress;
@property (nonatomic, readonly) EHIVehicleLogistics *logistics;
@property (nonatomic, readonly) EHIUserContactProfile *profile;
@property (nonatomic, readonly) BOOL allowsDelivery;
@property (nonatomic, readonly) BOOL allowsCollection;
@end

@implementation EHIDeliveryCollectionViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title           = EHILocalizedString(@"delivery_collections_title", @"Enter Details", @"");
        _saveButtonTitle = EHILocalizedString(@"delivery_collection_save_title", @"SAVE CHANGES", @"");
    }
    
    return self;
}

- (void)didBecomeActive
{
    [super didBecomeActive];

    // configure delivery view models
    if(self.allowsDelivery) {
        NSString *deliveryToggleTitle = EHILocalizedString(@"delivery_collection_i_want_delivered_title", @"I want my rental delivered", @"");
        self.deliveryToggleViewModel = [EHIFormFieldToggleViewModel toggleFieldWithTitle:deliveryToggleTitle input:self.logistics.deliveryInfo != nil];
        self.deliveryToggleViewModel.delegate = self;
        [(EHIFormFieldToggleViewModel *)self.deliveryToggleViewModel setToggleValue:YES];
        
        self.deliveryAddressViewModels = [self constructAddressViewModelsAsDelivery:YES];
        self.deliveryAddressViewModels.each(^(EHIFormFieldViewModel *viewModel) {
            viewModel.delegate = self;
        });
    }
    else {
        NSString *unavailableDeliveryTitle = EHILocalizedString(@"delivery_collection_unavailable_delivery_title", @"Delivery service is not available at your chosen location.", @"");
        self.deliveryToggleViewModel = [EHIFormFieldLabelViewModel viewModelWithTitle:unavailableDeliveryTitle];
    }
    
    // configure collection view models
    if(self.allowsCollection) {
        NSString *collectionToggleTitle = EHILocalizedString(@"delivery_collection_i_want_collected_title", @"I want my rental collected", @"");
        self.collectionToggleViewModel = [EHIFormFieldToggleViewModel toggleFieldWithTitle:collectionToggleTitle input:self.logistics.collectionInfo != nil];
        self.collectionToggleViewModel.delegate = self;
        [(EHIFormFieldToggleViewModel *)self.collectionToggleViewModel setToggleValue:YES];
        
        self.collectionAddressViewModels = [self constructAddressViewModelsAsDelivery:NO];
        self.collectionAddressViewModels.each(^(EHIFormFieldViewModel *viewModel) {
            viewModel.delegate = self;
        });
    }
    else {
        NSString *unavailableCollectionTitle = EHILocalizedString(@"delivery_collection_unavailable_collection_title", @"Collection service is not available at your chosen location.", @"");
        self.collectionToggleViewModel = [EHIFormFieldLabelViewModel viewModelWithTitle:unavailableCollectionTitle];
    }
    
    // configure same D&C toggle
    if(self.allowsDelivery && self.allowsCollection) {
        NSString *collectionSameToggleTitle = EHILocalizedString(@"delivery_collection_address_same_title", @"Same as delivery location", @"");
        self.sameAddressToggle = [EHIFormFieldToggleViewModel toggleFieldWithTitle:collectionSameToggleTitle input:self.logistics.hasSameDeliveryAndCollection];
        self.sameAddressToggle.toggleValue = self.logistics != nil ? self.logistics.hasSameDeliveryAndCollection : YES;
        self.sameAddressToggle.delegate = self;
    }
    
    // expose correct view models
    [self invalidateDeliveryAddressSection];
    [self invalidateCollectionAddressSection];
    [self validateForm:NO];
}

//
// Helpers
//

- (NSArray *)constructAddressViewModelsAsDelivery:(BOOL)delivery
{
    EHIDeliveryCollectionInfo *info;
    
    // use delivery info
    if(delivery) {
        info = self.logistics.deliveryInfo;
    }
    // prefill as collection if not same as delivery
    else if (!self.logistics.hasSameDeliveryAndCollection) {
        info = self.logistics.collectionInfo;
    }
    
    NSString *streetPlaceholder = delivery
        ? EHILocalizedString(@"delivery_collection_street_field_delivery_placeholder", @"Add Delivery Address", @"")
        : EHILocalizedString(@"delivery_collection_street_field_collection_placeholder", @"Add Collection Address", @"");
    
    NSString *postalTitle = EHILocalizedString(@"delivery_collection_postal_field_title", @"ZIP CODE", @"");
    NSString *commentTitle = EHILocalizedString(@"delivery_collection_comment_field_title", @"ANY ADDITIONAL INSTRUCTIONS?", @"");
    
    EHIFormFieldTextViewModel *streetOne = [EHIFormFieldTextViewModel new];
    streetOne.title       = EHILocalizedString(@"delivery_collection_street_field_title", @"STREET ADDRESS", @"");
    streetOne.isRequired  = YES;
    streetOne.placeholder = streetPlaceholder;
    streetOne.inputValue  = [info.address.addressLines ehi_safelyAccess:0];
    [streetOne validates:EHIFormFieldValidationNotEmpty];
    
    EHIFormFieldTextViewModel *streetTwo = [EHIFormFieldTextViewModel new];
    streetTwo.inputValue = [info.address.addressLines ehi_safelyAccess:1];
    
    EHIFormFieldTextViewModel *city = [EHIFormFieldTextViewModel new];
    city.title       = EHILocalizedString(@"delivery_collection_city_field_title", @"CITY", @"");
    city.isRequired  = YES;
    city.placeholder = EHILocalizedString(@"delivery_collection_city_field_placeholder", @"Add City Name", @"");
    city.inputValue  = info.address.city;
    [city validates:EHIFormFieldValidationNotEmpty];
    
    EHIFormFieldTextViewModel *postalCode = [EHIFormFieldTextViewModel new];
    postalCode.attributedTitle = [self optionalTitleWithFieldName:postalTitle];
    postalCode.placeholder     = EHILocalizedString(@"delivery_collection_postal_field_placeholder", @"Add Zip Code if Available", @"");
    postalCode.inputValue      = info.address.postalCode;
    
    EHIFormFieldTextViewModel *phone = [EHIFormFieldTextViewModel new];
    phone.title = EHILocalizedString(@"delivery_collection_phone_field_title", @"PHONE NUMBER", @"");
    phone.isRequired   = YES;
    phone.placeholder  = EHILocalizedString(@"delivery_collection_phone_field_placeholder", @"(123) 456-7890", @"");
    phone.isPhoneField = YES;
    phone.inputValue   = info.phone.number ?: ((EHIPhone *)[self.profile.phones firstObject]).number;
    [phone validates:EHIFormFieldValidationNotEmpty];
    
    EHIFormFieldTextViewViewModel *comments = [EHIFormFieldTextViewViewModel new];
    comments.attributedTitle = [self optionalTitleWithFieldName:commentTitle];
    comments.placeholder     = EHILocalizedString(@"delivery_collection_comments_field_placeholder", @"Optional comments (200 char. max.)", @"");
    comments.isLastInGroup   = YES;
    comments.maxLength       = 200;
    comments.inputValue      = info.comments;
    
    return @[streetOne, streetTwo, city, postalCode, phone, comments];
}

- (NSAttributedString *)optionalTitleWithFieldName:(NSString *)fieldName
{
    NSString *optionalTitle = EHILocalizedString(@"form_title_optional_field", @"(Optional)", @"");
    
    return EHIAttributedStringBuilder.new.fontStyle(EHIFontStyleBold, 14.0)
        .text(fieldName)
        .space.appendText(optionalTitle).fontStyle(EHIFontStyleLight, 14.0).string;
}

# pragma mark - Accessors

- (EHIRequiredInfoViewModel *)requiredModel
{
    return [EHIRequiredInfoViewModel modelForInfoType:EHIRequiredInfoTypeReservation];
}

- (id)headerForSection:(EHIDeliveryCollectionSection)section
{
    return self.sectionHeaders[@(section)];
}

- (NSDictionary *)sectionHeaders
{
    if(_sectionHeaders) {
        return _sectionHeaders;
    }
    
    NSString *deliveryToggleTitle = EHILocalizedString(@"delivery_collection_delivery_details_section_title", @"DELIVERY DETAILS", @"");
    NSString *deliveryAddressTitle = EHILocalizedString(@"delivery_collection_delivery_address_section_title", @"Where should we meet you to deliver your rental?", @"");
    NSString *collectionToggleTitle = EHILocalizedString(@"delivery_collection_collection_details_section_title", @"COLLECTION DETAILS", @"");
    NSString *collectionAddressTitle = EHILocalizedString(@"delivery_collection_collection_address_section_title", @"Where should we meet you to collect your rental?", @"");
    
    _sectionHeaders = @{
        @(EHIDeliveryCollectionSectionDeliveryToggle)    : [EHISectionHeaderModel modelWithTitle:deliveryToggleTitle],
        @(EHIDeliveryCollectionSectionRequiredInfo)      : deliveryAddressTitle,
        @(EHIDeliveryCollectionSectionCollectionToggle)  : [EHISectionHeaderModel modelWithTitle:collectionToggleTitle],
        @(EHIDeliveryCollectionSectionCollectionAddress) : collectionAddressTitle,
    };
    
    return _sectionHeaders;
}

# pragma mark - EHIFormFieldDelegate

- (void)formFieldViewModelDidChangeValue:(EHIFormFieldViewModel *)viewModel
{
    // conditionally show delivery address section
    if([viewModel isEqual:self.deliveryToggleViewModel]) {
        [self invalidateDeliveryAddressSection];
        
        // reset 'same as delivery' when delivery enabled changes
        self.sameAddressToggle.toggleValue = NO;
        
        // show/hide 'same as delivery' toggle
        if(self.hasEnabledCollection) {
            [self invalidateCollectionAddressSection];
        }
    }
    
    // conditionally show all or parts of collection address section
    else if([viewModel isEqual:self.collectionToggleViewModel] || [viewModel isEqual:self.sameAddressToggle]) {
        [self invalidateCollectionAddressSection];
    }

    [self validateForm:NO];
}

//
// Helpers
//

- (void)invalidateDeliveryAddressSection
{
    // show delivery address fields if user is using delivery
    self.deliveryAddressSectionViewModels = self.hasEnabledDelivery ? self.deliveryAddressViewModels : nil;
}

- (void)invalidateCollectionAddressSection
{
    // hide collection section if user is not using collection
    if(!self.hasEnabledCollection) {
        self.collectionAddressSectionViewModels = nil;
        return;
    }
    
    NSArray *viewModels = @[];

    if(self.hasEnabledDelivery) {
        // append same address field, if it exists
        viewModels = [viewModels ehi_safelyAppend:self.sameAddressToggle];
    }
    
    // append collection address fields if needed
    if(!self.sameAddressToggle.toggleValue) {
        viewModels = [viewModels arrayByAddingObjectsFromArray:self.collectionAddressViewModels];
    }

    self.collectionAddressSectionViewModels = viewModels;
}

- (void)validateForm:(BOOL)showErrors
{
    NSMutableArray *fieldsToValidate = [NSMutableArray new];
    
    // include delivery address if in use
    if(self.isUsingDeliveryAddress) {
        [fieldsToValidate addObjectsFromArray:self.deliveryAddressViewModels];
    }
    
    // include collection address if in use
    if(self.isUsingCollectionAddress) {
        [fieldsToValidate addObjectsFromArray:self.collectionAddressSectionViewModels];
    }
    
    // run validation check on all visible fields
    __block BOOL validForm = YES;
    
    for(EHIFormFieldViewModel *viewModel in fieldsToValidate) {
        validForm &= [viewModel validate:showErrors];
    }
    
    self.invalidForm = !validForm;
}

- (BOOL)isUsingDeliveryAddress
{
    return self.hasEnabledDelivery || (self.sameAddressToggle.toggleValue && self.hasEnabledCollection);
}

- (BOOL)isUsingCollectionAddress
{
    return self.hasEnabledCollection && !self.sameAddressToggle.toggleValue;
}

# pragma mark - Actions

- (void)commitDeliveryCollection
{
    // show errors if needed
    if(self.invalidForm) {
        [self validateForm:YES];
        return;
    }
    
    self.isLoading = YES;
    
    EHIDeliveryCollectionInfo *delivery = [self constructDeliveryInfo];
    EHIDeliveryCollectionInfo *collection = [self constructCollectionInfo];
    
    EHIReservationHandler handler = ^(EHIReservation *reservation, EHIServicesError *error) {
        self.isLoading = NO;
        
        if(!error.hasFailed) {
            self.router.transition
                .pop(1).start(nil);
        }
    };
    
    if(self.isModify) {
        [[EHIServices sharedInstance] modifyDelivery:delivery collection:collection onReservation:self.builder.reservation handler:handler];
    } else {
        [[EHIServices sharedInstance] addDelivery:delivery collection:collection toReservation:self.builder.reservation handler:handler];
    }
}

//
// Helpers
//

- (nullable EHIDeliveryCollectionInfo *)constructDeliveryInfo
{
    // nothing if delivery is not allowed
    if(!self.allowsDelivery || !self.hasEnabledDelivery) {
        return nil;
    }
    // construct from fields
    else if(self.hasEnabledDelivery) {
        return [self createDeliveryCollectionAsDelivery:YES];
    }
    // clear value
    else {
        return [EHIDeliveryCollectionInfo new];
    }
}

- (nullable EHIDeliveryCollectionInfo *)constructCollectionInfo
{
    // nothing if collection is not allowed
    if(!self.allowsCollection || !self.hasEnabledCollection) {
        return nil;
    }
    // construct from correct fields
    else if(self.hasEnabledCollection) {
        EHIDeliveryCollectionInfo *collection = [self createDeliveryCollectionAsDelivery:self.sameAddressToggle.toggleValue];
        
        // don't copy comments if same as delivery
        if(self.sameAddressToggle.toggleValue) {
            collection.comments = nil;
        }
        
        return collection;
    }
    // clear value
    else {
        return [EHIDeliveryCollectionInfo new];
    }
}

- (EHIDeliveryCollectionInfo *)createDeliveryCollectionAsDelivery:(BOOL)delivery
{
    // retrieve all street lines with text
    NSArray *streetAddresses = @[];
    streetAddresses = [streetAddresses ehi_safelyAppend:[self inputValueForAddressRow:EHIDeliveryCollectionAddressRowStreetOne asDelivery:delivery]];
    streetAddresses = [streetAddresses ehi_safelyAppend:[self inputValueForAddressRow:EHIDeliveryCollectionAddressRowStreetTwo asDelivery:delivery]];
    
    EHIAddress *address = [EHIAddress modelWithDictionary:@{
        @key(address.addressLines) : streetAddresses,
        @key(address.city)         : [self inputValueForAddressRow:EHIDeliveryCollectionAddressRowCity asDelivery:delivery]
    }];

    EHILocation *location = delivery || !self.builder.isOneWayReservation ? self.builder.reservation.pickupLocation : self.builder.reservation.returnLocation;
    NSString *countryCode = location.address.countryCode;
	
	if(countryCode != nil) {
		[address updateWithDictionary:@{ @key(address.countryCode) : countryCode }];
	}

    // optional
    NSString *postal = [self inputValueForAddressRow:EHIDeliveryCollectionAddressRowPostal asDelivery:delivery];
    if(postal) {
        [address updateWithDictionary:@{ @key(address.postalCode) : postal }];
    }
    
    EHIPhone *phone = [EHIPhone modelWithDictionary:@{
        @key(phone.number) : [self inputValueForAddressRow:EHIDeliveryCollectionAddressRowPhone asDelivery:delivery]
    }];
    
    EHIDeliveryCollectionInfo *deliveryLocation = [EHIDeliveryCollectionInfo new];
    deliveryLocation.address = address;
    deliveryLocation.phone = phone;
    deliveryLocation.comments = [self inputValueForAddressRow:EHIDeliveryCollectionAddressRowComments asDelivery:delivery];
    
    return deliveryLocation;
}

- (id)inputValueForAddressRow:(EHIDeliveryCollectionAddressRow)row asDelivery:(BOOL)delivery
{
    NSArray *addressViewModels = delivery ? self.deliveryAddressViewModels : self.collectionAddressViewModels;
    EHIFormFieldViewModel *viewModel = addressViewModels[row];
    
    return viewModel.inputValue;
}

# pragma mark - Computed

- (BOOL)hasEnabledDelivery
{
    // always NO if toggle has been replaced with unavailable alert
    if(![self.deliveryToggleViewModel isKindOfClass:[EHIFormFieldToggleViewModel class]]) {
        return NO;
    }
    
    EHIFormFieldToggleViewModel *toggle = (EHIFormFieldToggleViewModel *)self.deliveryToggleViewModel;
    return toggle.toggleValue;
}

- (BOOL)hasEnabledCollection
{
    // always NO is toggle has been replaced with unavailable alert
    if(![self.collectionToggleViewModel isKindOfClass:[EHIFormFieldToggleViewModel class]]) {
        return NO;
    }
    
    EHIFormFieldToggleViewModel *toggle = (EHIFormFieldToggleViewModel *)self.collectionToggleViewModel;
    return toggle.toggleValue;
}

# pragma mark - Passthrough

- (EHIVehicleLogistics *)logistics
{
    return self.builder.reservation.vehicleLogistics;
}

- (EHIUserContactProfile *)profile
{
    return [EHIUser currentUser].contact;
}

- (BOOL)allowsDelivery
{
    return self.builder.reservation.allowsDelivery;
}

- (BOOL)allowsCollection
{
    return self.builder.reservation.allowsCollection;
}

@end

NS_ASSUME_NONNULL_END
