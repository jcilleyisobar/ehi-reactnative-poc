//
//  EHIAirlineSearchViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 6/22/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIAirlineSearchViewModel.h"
#import "EHIAirlineSearchResultViewModel.h"
#import "EHIAirline.h"
#import "EHIViewModel_Subclass.h"
#import "EHIReservationBuilder.h"

#define EHISearchingDelay 0.3

@interface EHIAirlineSearchViewModel ()
@property (copy  , nonatomic) NSArray *pickupAirlines;
@property (copy  , nonatomic) NSArray *airports;
@property (strong, nonatomic) EHIAirlineSearchResultViewModel *otherModel;
@end

@implementation EHIAirlineSearchViewModel

- (instancetype)init
{
    if(self = [super init]) {
        [self filterAirlineWithQuery:nil];
    }
    
    return self;
}

- (void)didResignActive
{
    [super didResignActive];
    
    self.handler = nil;
}

# pragma mark - Searching

- (void)filterAirlineWithQuery:(NSString *)query
{
    BOOL validQuery = query && query.length;
    if(!validQuery) {
        self.resultModels = self.airports;
    } else {
        __weak typeof(self) welf = self;
        dispatch_after_seconds(EHISearchingDelay, ^{
            NSArray *result = self.airports.select(^(EHIAirlineSearchResultViewModel *model){
                return [model contains:query];
            }).sort;

            welf.resultModels = [welf appendOtherModel:result];
        });
    }
}

- (NSArray *)appendOtherModel:(NSArray *)array
{
    return [array ehi_safelyAppend:self.otherModel];
}

- (EHIAirlineSearchResultViewModel *)otherModel
{
    if(!_otherModel) {
        EHIAirline *other = self.pickupAirlines.find(^(EHIAirline *airline){
            return airline.isOther;
        });
        _otherModel = [EHIAirlineSearchResultViewModel initWithAirline:other];
    }
    
    return _otherModel;
}

# pragma mark - Actions

- (void)selectAirlineAtIndexPath:(NSIndexPath *)indexPath
{
    EHIAirlineSearchResultViewModel *model = [self.resultModels ehi_safelyAccess:indexPath.row];
    ehi_call(self.handler)(model.airline);
    
    self.router.transition.pop(1).start(nil);
}

# pragma mark - Accessors

- (NSArray *)airports
{
    if(!_airports) {
        _airports = self.pickupAirlines.map(^(EHIAirline *airline){
            return [EHIAirlineSearchResultViewModel initWithAirline:airline];
        });
        _airports = _airports.reject(^(EHIAirlineSearchResultViewModel *model) {
            return model.airline.isWalkIn || model.airline.isOther;
        });
    }
    
    return _airports;
}

- (NSArray *)pickupAirlines
{
    if(!_pickupAirlines) {
        _pickupAirlines = [(self.builder.reservation.pickupLocation.airlines ?: @[]) copy];
    }
    
    return _pickupAirlines;
}

# pragma mark - Passthrough

- (EHIReservationBuilder *)builder
{
    return [EHIReservationBuilder sharedInstance];
}

@end
