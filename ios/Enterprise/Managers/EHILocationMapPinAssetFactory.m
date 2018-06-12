//
//  EHILocationMapPinAssetFactory.m
//  Enterprise
//
//  Created by Rafael Ramos on 5/22/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHILocationMapPinAssetFactory.h"

@implementation EHILocationMapPinAssetFactory

+ (NSString *)assetForLocation:(EHILocation *)location
{
	NSString *asset = [self computeLocation:location];
	BOOL hasConflicts = location.hasConflicts;
	return hasConflicts ? [self appendClosedTo:asset] : asset;
}

+ (NSString *)computeLocation:(EHILocation *)location
{
	if(location.isExotics) {
		return [self assetWithType:EHILocationMapPinAssetTypeExotics];
	}

	switch(location.brand) {
		case EHILocationBrandAlamo:
			return [self assetWithType:EHILocationMapPinAssetTypeAlamo];
		case EHILocationBrandNational:
			return [self assetWithType:EHILocationMapPinAssetTypeNational];
		default: break;
	}

	if(location.isFavorited) {
		return [self assetWithType:EHILocationMapPinAssetTypeFavorite];
	}

	if(location.hasMotorcycles) {
		return [self assetWithType:EHILocationMapPinAssetTypeMotorcycle];
	}

	switch(location.type) {
		case EHILocationTypeAirport:
			return [self assetWithType:EHILocationMapPinAssetTypeAirport];
		case EHILocationTypePort:
			return [self assetWithType:EHILocationMapPinAssetTypePort];
		case EHILocationTypeTrain:
			return [self assetWithType:EHILocationMapPinAssetTypeTrain];
		default:
			break;
	}

	return [self assetWithType:EHILocationMapPinAssetTypeEnterprise];
}

+ (NSString *)assetForLocation:(EHILocation *)location selected:(BOOL)selected
{
	NSString *asset = [self assetForLocation:location];
	return selected ? [self appendSelectedTo:asset] : asset;
}

+ (NSString *)assetWithType:(EHILocationMapPinAssetType)type
{
	switch(type) {
		case EHILocationMapPinAssetTypeEnterprise: return @"map_pin_standard";
		case EHILocationMapPinAssetTypeAlamo: return @"map_pin_alamo";
		case EHILocationMapPinAssetTypeNational: return @"map_pin_national";
		case EHILocationMapPinAssetTypeTrain: return @"map_pin_rail";
		case EHILocationMapPinAssetTypePort: return @"map_pin_port";
		case EHILocationMapPinAssetTypeAirport: return @"map_pin_airports";
		case EHILocationMapPinAssetTypeMotorcycle: return @"map_pin_motorcycles";
		case EHILocationMapPinAssetTypeExotics: return @"map_pin_exotics";
		case EHILocationMapPinAssetTypeFavorite: return @"map_pin_fav";
	}
}

+ (NSString *)assetWithType:(EHILocationMapPinAssetType)type selected:(BOOL)selected
{
	NSString *asset = [self assetWithType:type];
	return selected ? [self appendSelectedTo:asset] : asset;
}

+ (NSString *)appendSelectedTo:(NSString *)string
{
	return [string stringByAppendingString:@"_selected"];
}

+ (NSString *)appendClosedTo:(NSString *)string
{
	return [string stringByAppendingString:@"_closed"];
}

@end
