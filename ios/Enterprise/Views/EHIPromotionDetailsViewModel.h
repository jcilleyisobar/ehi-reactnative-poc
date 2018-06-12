//
//  EHIPromotionDetailsViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 3/28/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIPromotionDetailsImageCellViewModel.h"
#import "EHIPromotionDetailsTitleCellViewModel.h"
#import "EHIPromotionDetailsBulletItemCellViewModel.h"
#import "EHIPromotionDetailsActionCellViewModel.h"
#import "EHIPromotionDetailsPolicyCellViewModel.h"

typedef NS_ENUM(NSUInteger, EHIPromotionDetailsSection) {
    EHIPromotionDetailsSectionImage,
    EHIPromotionDetailsSectionTitle,
    EHIPromotionDetailsSectionBullets,
    EHIPromotionDetailsSectionAction,
    EHIPromotionDetailsSectionPolicy
};

@interface EHIPromotionDetailsViewModel : EHIViewModel <MTRReactive>
@property (copy, nonatomic, readonly) NSString *navigationTitle;
@property (strong, nonatomic) EHIPromotionDetailsImageCellViewModel *imageModel;
@property (strong, nonatomic) EHIPromotionDetailsTitleCellViewModel *titleModel;
@property (strong, nonatomic) NSArray *bulletModels;
@property (strong, nonatomic) EHIPromotionDetailsActionCellViewModel *actionModel;
@property (strong, nonatomic) EHIPromotionDetailsPolicyCellViewModel *policyModel;

- (void)didTapWeekendSpecialStartReservation;

@end
