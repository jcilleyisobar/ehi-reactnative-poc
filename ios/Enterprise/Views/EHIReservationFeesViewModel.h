//
//  EHIReservationFeesViewModel.h
//  Enterprise
//
//  Created by Ty Cobb on 4/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIReservationFeesViewModel : EHIViewModel <MTRReactive>

/** The title for the screen */
@property (copy, nonatomic, readonly) NSString *title;
/** The list of fees of type @c EHICarClassPriceLineItem to render */
@property (copy, nonatomic, readonly) NSArray *fees;
/** The title for the modal confirmation button */
@property (copy, nonatomic, readonly) NSString *confirmationTitle;
/** The index path for the expanded fee cell */
@property (copy, nonatomic, readonly) NSIndexPath *selectedPath;

/** Marks the fee at the given index as selected */
- (void)selectFeeAtIndex:(NSInteger)index;
/** Closes the fees modal */
- (void)dismiss;

@end
