//
//  EHIReservationDriverInfoCellViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 4/3/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationDriverInfoCellViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHIDriverInfoViewModel.h"
#import "EHIDriverInfo.h"
#import "EHIPhoneNumberFormatter.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHIReservationDriverInfoCellViewModel ()
@property (strong, nonatomic, nullable) EHIDriverInfo *driverInfo;
@property (copy  , nonatomic) NSString *name;
@property (copy  , nonatomic) NSString *email;
@property (copy  , nonatomic) NSString *phone;
@end

@implementation EHIReservationDriverInfoCellViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _actionButtonTitle = EHILocalizedString(@"reservation_review_add_driver_info_button_title", @"ADD DRIVER INFORMATION", @"text of button to add driver information on reservation review screen");
        _title = EHILocalizedString(@"reservation_driver_info_title", @"DRIVER INFO", @"");
    }
    
    return self;
}

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    if([model isKindOfClass:[EHIDriverInfo class]]) {
        [self updateWithDriverInfo:model];
    }
}

//
// Helpers
//

- (void)updateWithDriverInfo:(EHIDriverInfo *)driverInfo
{
    self.driverInfo = driverInfo;

    [self configureWithDriverInfo:driverInfo];
}

- (void)configureWithDriverInfo:(EHIDriverInfo *)driverInfo
{
    // append first and last name
    EHIAttributedStringBuilder *builder = [EHIAttributedStringBuilder new].appendText(driverInfo.firstName);
    
    if (builder.string.length != 0) {
        builder = builder.space;
    }
    
    builder.appendText(driverInfo.lastName);
    self.name  = [builder.string string];
    self.phone = [self phoneForDriverInfo:driverInfo];
    self.email = [self emailForDriverInfo:driverInfo];
}

- (NSString *)phoneForDriverInfo:(EHIDriverInfo *)driverInfo
{
    NSString *phoneNumber = NSString.new;
    BOOL isMasked = driverInfo.phone.number.ehi_isMasked || driverInfo.phone.maskedNumber != nil;
    if(isMasked) {
        phoneNumber = driverInfo.phone.maskedNumber ?: driverInfo.phone.number;
    } else {
        phoneNumber = [EHIPhoneNumberFormatter format:driverInfo.phone.number countryCode:[NSLocale ehi_region]];
    }

    return [NSString stringWithFormat:@"%@: %@",
            EHILocalizedString(@"enroll_phone_number_title", @"PHONE", @""),
            phoneNumber];
}

- (NSString *)emailForDriverInfo:(EHIDriverInfo *)driverInfo
{
    NSString *email = NSString.new;
    BOOL isMasked = driverInfo.email.ehi_isMasked || driverInfo.maskedEmail != nil;
    if(isMasked) {
        email = driverInfo.maskedEmail ?: driverInfo.email;
    } else {
        email = driverInfo.email;
    }

    return [NSString stringWithFormat:@"%@: %@",
            EHILocalizedString(@"enroll_email_title", @"EMAIL", @""),
            email];
}

# pragma mark - Computed

- (BOOL)shouldShowDriverInfo
{
    return self.driverInfo != nil;
}

# pragma mark - Actions

- (void)addDriverInfo
{
    EHIDriverInfoViewModel *viewModel = [EHIDriverInfoViewModel new];
    viewModel.isEditing = YES;
    
    self.router.transition
        .push(EHIScreenReservationDriverInfo).object(viewModel).start(nil);
}

@end

NS_ASSUME_NONNULL_END
