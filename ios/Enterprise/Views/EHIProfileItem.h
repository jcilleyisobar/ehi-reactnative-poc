//
//  EHIProfileBasicItem.h
//  Enterprise
//
//  Created by fhu on 5/18/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel.h"

typedef NS_ENUM(NSUInteger, EHIProfileCellType) {
    EHIProfileCellTypeDefault,
    EHIProfileCellTypePhone,
};

@interface EHIProfileItem : EHIModel

@property (copy, nonatomic) NSString *title;
@property (strong, nonatomic) id data;
@property (assign, nonatomic) EHIProfileCellType type;

+ (NSArray *)memberInfoItems;

+ (NSArray *)driverLicenseItems;

@end
