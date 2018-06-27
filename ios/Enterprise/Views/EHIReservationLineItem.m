//
//  EHIReservationLineItem.m
//  Enterprise
//
//  Created by fhu on 7/17/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationLineItem.h"
#import "EHICarClassMileage.h"
#import "EHICarClassExtra.h"
#import "EHIWebViewModel.h"
#import "EHIWebContent.h"

@interface EHIReservationLineItem()
@property (assign, nonatomic) EHIReservationLineItemType type;
@property (copy  , nonatomic) NSString *formattedTitle;
@property (copy  , nonatomic) NSString *formattedRate;
@property (copy  , nonatomic) NSString *formattedTotal;
@property (assign, nonatomic) NSInteger quantity;
@property (assign, nonatomic) void (^action)(void);
@property (assign, nonatomic) BOOL isLearnMore;
@property (assign, nonatomic) BOOL isCharged;
@end

@implementation EHIReservationLineItem

+ (instancetype)lineItemForLearnMoreButton
{
    EHIReservationLineItem *lineItem = [EHIReservationLineItem new];
    lineItem.formattedTitle = EHILocalizedString(@"class_details_fees_learn_more", @"Learn More", @"");
    lineItem.formattedTotal = @"";
    lineItem.type = EHIReservationLineItemTypeFee;
    lineItem.isLearnMore = YES;
    lineItem.action = ^{
        [[[EHIWebViewModel alloc] initWithType:EHIWebContentTypeTaxes] push];
    };
    
    return lineItem;
}

- (BOOL)hasDetails
{
    return self.action ? YES : NO;
}

- (NSInteger)quantity
{
    return 0;
}

- (EHIPrice *)viewPrice
{
    return nil;
}

@end
