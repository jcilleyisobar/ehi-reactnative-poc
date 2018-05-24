//
//  EHIMenuViewModel.h
//  Enterprise
//
//  Created by Pawel Bragoszewski on 23.03.2015.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIMenuItem.h"

typedef NS_ENUM(NSUInteger, EHIMenuSection) {
    EHIMenuSectionPromotion,
    EHIMenuSectionScreen,
    EHIMenuSectionSecondary
};

@interface EHIMenuViewModel : EHIViewModel <MTRReactive>

/** An array of @c EHIMenuItems corresponding to every row in the menu */
@property (strong, nonatomic, readonly) NSArray *menuItems;
@property (strong, nonatomic, readonly) NSIndexPath *highlightedIndexPath;
@property (assign, nonatomic, readonly) BOOL isVisible;
@property (assign, nonatomic, readonly) BOOL selectedSameIndex;

/** Checks whether selection is permitted for the item at the given index */
- (BOOL)shouldSelectItemAtIndex:(NSInteger)index;
/** Performs selection logic for the menu item at the specified index */
- (void)selectItemAtIndexPath:(NSIndexPath *)indexPath;
/** Shows the promotion screen */
- (void)didTapPromotionGetStarted;
- (EHIMenuItem *)itemForIndexPath:(NSIndexPath *)indexPath;

@end
