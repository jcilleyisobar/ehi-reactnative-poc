//
//  EHIUserEmailPreferences.m
//  Enterprise
//
//  Created by mplace on 2/24/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIUserEmailPreferences.h"
#import "EHIModel_Subclass.h"

@implementation EHIUserEmailPreferences

- (void)updateWithDictionary:(NSDictionary *)dictionary forceDeletions:(BOOL)forceDeletions
{
    [super updateWithDictionary:dictionary forceDeletions:forceDeletions];
    
    NSNumber *specialOffers = dictionary[@"special_offers"];
    if(specialOffers) {
        _specialOffers = specialOffers.boolValue ? EHIOptionalBooleanTrue : EHIOptionalBooleanFalse;
    }
}

+ (NSDictionary *)mappings:(EHIUserEmailPreferences *)model
{
    return @{
        @"rental_receipts" : @key(model.rentalReceipts),
        @"special_offers"  : @key(model.specialOffers),
        @"partner_offers"  : @key(model.partnerOffers),
        @"subscriber_preferences_url" : @key(model.subscriberPreferencesUrl),
    };
}

# pragma mark - EHINetworkEncodable

- (void)encodeWithRequest:(EHINetworkRequest *)request
{
    request[@"rental_receipts"] = EHIStringifyFlag(self.rentalReceipts);
    request[@"special_offers"]  = [[self.class transformerForKey:@key(self.specialOffers)] reverseTransformedValue:@(self.specialOffers)];
    request[@"partner_offers"]  = EHIStringifyFlag(self.partnerOffers);
}

+ (void)registerTransformers:(EHIUserEmailPreferences *)model
{
    [super registerTransformers:model];

    [self key:@key(model.specialOffers) registerTransformer:EHIOptionalBooleanTransformer()];
}

@end
