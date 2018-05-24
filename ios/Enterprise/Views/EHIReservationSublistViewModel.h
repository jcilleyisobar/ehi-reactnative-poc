//
//  EHIReservationSublistViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 4/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIReservationSublistSection.h"
#import "EHICarClassExtras.h"
#import "EHICarClass.h"

typedef NS_ENUM(NSInteger, EHIReservationSublistType) {
    EHIReservationSublistTypeExtra,
    EHIReservationSublistTypeLineItem,
};

@interface EHIReservationSublistViewModel : EHIViewModel <UICollectionViewDelegate>

/** The type of views to render for this sublist */
@property (assign, nonatomic) EHIReservationSublistType type;
/** The header for the sublist */
@property (copy  , nonatomic) NSString *title;
/** The list of sections in this sublist */
@property (strong, nonatomic) NSArray *sections;

/** Generates a sublist view model for the specified @c carClass */
+ (instancetype)sublistModelForCarClass:(EHICarClass *)carClass prepay:(BOOL)prepay;
/** Selects the sublist item at the @c indexPath */
- (void)selectItemAtIndexPath:(NSIndexPath *)indexPath;

@end

@interface EHIReservationSublistViewModel (Generators)
/** Generates a list of bonus line items for extras that need to be kludged into the list */
+ (NSArray *)lineItemExtrasForClassClass:(EHICarClass *)carClass prepay:(BOOL)prepay;
@end
