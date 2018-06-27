//
//  EHIRentalsFooterCellViewModel.h
//  Enterprise
//
//  Created by fhu on 4/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIRentalsMode.h"

@interface EHIRentalsFooterViewModel : EHIViewModel <MTRReactive>

@property (copy  , nonatomic) NSString *startRentalButtonText;
@property (copy  , nonatomic) NSString *contactButtonText;
@property (copy  , nonatomic) NSString *cannotFindButtonText;
@property (copy  , nonatomic) NSString *lookupButtonText;
@property (copy  , nonatomic) NSString *contactButtonImageName;
@property (assign, nonatomic) BOOL hidesFindButton;
@property (assign, nonatomic) BOOL hidesLookupButton;

+ (instancetype)viewModelWithMode:(EHIRentalsMode)mode;

- (void)startRental;
- (void)lookupRental;
- (void)callHelpNumber;
- (void)cannotFindRental;

@end
