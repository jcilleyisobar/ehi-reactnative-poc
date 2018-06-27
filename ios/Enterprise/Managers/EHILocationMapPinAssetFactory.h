//
//  EHILocationMapPinAssetFactory.h
//  Enterprise
//
//  Created by Rafael Ramos on 5/22/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

typedef NS_ENUM(NSInteger, EHILocationMapPinAssetType) {
	EHILocationMapPinAssetTypeEnterprise,
	EHILocationMapPinAssetTypeAlamo,
	EHILocationMapPinAssetTypeNational,
	EHILocationMapPinAssetTypeTrain,
	EHILocationMapPinAssetTypePort,
	EHILocationMapPinAssetTypeAirport,
	EHILocationMapPinAssetTypeMotorcycle,
	EHILocationMapPinAssetTypeExotics,
	EHILocationMapPinAssetTypeFavorite,
};

@interface EHILocationMapPinAssetFactory : NSObject
+ (NSString *)assetForLocation:(EHILocation *)location;
+ (NSString *)assetForLocation:(EHILocation *)location selected:(BOOL)selected;
+ (NSString *)assetWithType:(EHILocationMapPinAssetType)type;
+ (NSString *)assetWithType:(EHILocationMapPinAssetType)type selected:(BOOL)selected;
@end
