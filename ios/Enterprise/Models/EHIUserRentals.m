//
//  EHIUserRentals.m
//  Enterprise
//
//  Created by Ty Cobb on 7/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIModel_Subclass.h"
#import "EHIUserRentals.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHIUserRentals ()
@property (assign, nonatomic) NSInteger pagingOffset;
@property (copy  , nonatomic) NSArray<EHIUserRental> *all;
@property (copy  , nonatomic, nullable) NSArray<EHIUserRental> *currentPage;
@end

@implementation EHIUserRentals

- (instancetype)initWithDictionary:(NSDictionary *)dictionary
{
    if(self = [super initWithDictionary:dictionary]) {
        _pagingOffset = 1;
    }
    
    return self;
}

- (void)parseDictionary:(NSMutableDictionary *)dictionary
{
    [super parseDictionary:dictionary];
   
    // if we have pre-write tickets, join them to our new page
    NSArray *prewriteTickets = dictionary[@"pre_write_tickets"];
    if(prewriteTickets) {
        [dictionary ehi_transform:@key(self.currentPage) block:^(NSArray *currentPage) {
            return @[].concat(currentPage).concat(prewriteTickets);
        }];
    }
}

# pragma mark - Hooks

- (EHIUserRentals *)appendPage:(NSDictionary *)dictionary
{
    [self updateWithDictionary:dictionary];
   
    // append the current page (if any) and destroy the page, since we don't need it anymore
    self.all = (id)(self.all ?: @[]).concat(self.currentPage);
    self.currentPage = nil;

    // update the paging offset
    self.pagingOffset += EHIUserRentalsPageSize;
    
    return self;
}

- (EHIUserRentals *)markAsCurrent
{
    for(EHIUserRental *rental in self.all) {
        rental.isCurrent = YES;
    }

    return self;
}

- (EHIUserRentals *)sort
{
    self.all = (id)(self.all ?: @[]).sortBy(^(EHIUserRental *rental) {
        return rental.pickupDate;
    });
    
    return self;
}

- (EHIUserRentals *)reverseSort
{
    self.all = (id)(self.all ?: @[]).sortBy(^(EHIUserRental *rental) {
        return rental.pickupDate;
    }).reverse;
    
    return self;
}

# pragma mark - Accessors

- (NSInteger)count
{
    return self.all.count;
}

- (nullable EHIUserRental *)firstRental
{
    return self.all.firstObject;
}

# pragma mark - Mappings

+ (NSDictionary *)mappings:(EHIUserRentals *)model
{
    return @{
        @"more_records_available" : @key(model.hasMoreAvailable),
        @"trip_summaries"         : @key(model.currentPage),
        @"upcoming_reservations"  : @key(model.currentPage),
    };
}

@end

NS_ASSUME_NONNULL_END
