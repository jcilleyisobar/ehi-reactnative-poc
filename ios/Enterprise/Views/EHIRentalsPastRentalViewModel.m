//
//  EHIRentalsPastRentalViewModel.m
//  Enterprise
//
//  Created by fhu on 4/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIRentalsPastRentalViewModel.h"
#import "EHIUserRental.h"
#import "EHIPriceFormatter.h"
#import "EHIInvoiceViewModel.h"
#import "EHIViewModel_Subclass.h"

@interface EHIRentalsPastRentalViewModel ()
@property (strong, nonatomic) EHIUserRental *userRental;
@end

@implementation EHIRentalsPastRentalViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _totalTitle   = EHILocalizedString(@"past_rentals_total", @"Total", @"");
        _receiptTitle = EHILocalizedString(@"past_rentals_view_receipt_title", @"View Receipt", @"");
    }
    
    return self;
}

- (void)updateWithModel:(EHIUserRental *)model
{
    [super updateWithModel:model];
    
    if([model isKindOfClass:[EHIUserRental class]]) {
        [self updateWithRentalDetails:model];
    }
}

- (void)updateWithRentalDetails:(EHIUserRental *)model
{
    self.userRental = model;
    
    self.reservationTime = [self displayTitleRental:model];
    
    self.total = [EHIPriceFormatter format:model.priceSummary].size(EHIPriceFontSizeSmall).omitCurrencyCode(YES).attributedString;
    
    if ([model.pickupLocation.uid isEqualToString:model.returnLocation.uid]) {
        self.location = model.pickupLocation.displayName;
    } else {
        self.location = [NSString stringWithFormat:@"%@ to %@", model.pickupLocation.displayName, model.returnLocation.displayName];
    }
}

- (void)displayInvoice
{
    EHIInvoiceViewModel *model = [[EHIInvoiceViewModel alloc] initFetchingRental:self.userRental];
    self.router.transition.present(EHIScreenInvoiceDetails).object(model).start(nil);
}

# pragma mark - Accessors

- (NSString *)confirmationText
{
    NSString *confirmationNumber = self.userRental.invoiceNumber
        ?: self.userRental.ticketNumber
        ?: self.userRental.confirmationNumber;
    NSString *confirmationTitle  = EHILocalizedString(@"past_rentals_rental_agreement", @"Rental Agreement #", @"");
    
    return [NSString stringWithFormat:@"%@\t%@", confirmationTitle, confirmationNumber];
}

- (NSString *)vehicleText
{
    EHICarClass *carDescription = self.userRental.carClassDetails;
    NSMutableArray *carDescriptionComponents = [NSMutableArray new];
    [carDescriptionComponents addObject:carDescription.make];
    [carDescriptionComponents ehi_safelyAppend:carDescription.model];
    
    NSString *vehicleName  = [carDescriptionComponents componentsJoinedByString:@" "];
    NSString *vehicleTitle = EHILocalizedString(@"past_rentals_vehicle_drive", @"Vehicle Driven", @"");
    
    return [NSString stringWithFormat:@"%@\t%@", vehicleTitle, vehicleName];
}

//
// Helpers
//

- (NSString *)displayTitleRental:(EHIUserRental *)rental
{
    if(!rental.pickupDate && !!rental.returnDate) {
        return nil;
    }
    
    NSString *dayString = [rental.pickupDate ehi_stringForTemplate:@"MMM dd"];
    NSString *dateString = [rental.returnDate ehi_stringForTemplate:@"MMM dd, yyyy"];
    
    NSString *format = EHILocalizedString(@"past_rental_display_time", @"#{day} - #{date}", @"date/time display title for pickup & return dates");
    
    return [[format ehi_applyReplacementMap:@{
        @"day" : dayString ?: @"",
        @"date" : dateString ?: @""
    }] uppercaseString];
}

@end
