//
//  EHIReservationFeesViewModel.m
//  Enterprise
//
//  Created by Ty Cobb on 4/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIReservationFeesViewModel.h"
#import "EHIReservationFeeViewModel.h"
#import "EHIWebViewModel.h"
#import "EHICarClassPriceLineItem.h"

@interface EHIReservationFeesViewModel ()
@property (copy, nonatomic) NSArray *fees;
@property (copy, nonatomic) NSIndexPath *selectedPath;
@property (copy, nonatomic) NSString *feesContent;
@end

@implementation EHIReservationFeesViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title = EHILocalizedString(@"class_details_fees_title", @"TAXES & FEES", @"Title fo the class details taxes & fees modal");
        _confirmationTitle = EHILocalizedString(@"modal_default_confirmation_title", @"GOT IT", @"Default title for modal confirmation button");
    }
    
    return self;
}

- (void)didInitialize
{
    [super didInitialize];
}

- (void)updateWithModel:(NSArray *)fees
{
    [super updateWithModel:fees];
   
    // map the fees into view models
    self.fees = (fees ?: @[]).map(^(EHICarClassPriceLineItem *fee) {
        return [[EHIReservationFeeViewModel alloc] initWithModel:fee];
    })
    // add a vm for the 'Learn More' link
    .concat(@[
        [EHIReservationFeeViewModel learnMoreViewModel]
    ]);
    
    // clear out any previously selected path
    self.selectedPath = nil;
}

# pragma mark - Selection

- (void)selectFeeAtIndex:(NSInteger)index
{
    // only allow the details to be selected
    if(index == self.fees.count - 1) {
        self.selectedPath = self.selectedPath ? nil : [NSIndexPath indexPathForItem:index inSection:0];
    }
}

- (void)setSelectedPath:(NSIndexPath *)selectedPath
{
    [self feeModelAtIndexPath:_selectedPath].isSelected = NO;
    _selectedPath = selectedPath;
    [self feeModelAtIndexPath:selectedPath].isSelected = YES;
}

# pragma mark - Accessors

- (EHIReservationFeeViewModel *)feeModelAtIndexPath:(NSIndexPath *)indexPath
{
    return indexPath && indexPath.item < self.fees.count ? self.fees[indexPath.item] : nil;
}

# pragma mark - Navigation

- (void)dismiss
{
    [EHIAnalytics trackAction:EHIAnalyticsResActionDone handler:nil];
    
    self.router.transition
        .dismiss.start(nil);
}

@end
