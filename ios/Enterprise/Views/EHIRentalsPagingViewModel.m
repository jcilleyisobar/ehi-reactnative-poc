//
//  EHIRentalsPagingViewModel.m
//  Enterprise
//
//  Created by Ty Cobb on 7/7/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIRentalsPagingViewModel.h"
#import "EHIServices+Rentals.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHIRentalsPagingViewModel ()
@property (assign, nonatomic) BOOL isLoading;
@end

@implementation EHIRentalsPagingViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _loadMoreTitle = EHILocalizedString(@"rentals_load_more_button", @"LOAD MORE", @"Title for the 'My Rentals' 'Load More' button");
    }
    
    return self;
}

# pragma mark - Paging

- (void)loadMoreRentalsWithHandler:(nullable EHIRentalsPagingHandler)handler
{
    if(self.isLoading) {
        return;
    }
    
    self.isLoading = YES;
    [[EHIServices sharedInstance] fetchUpcomingRentalsWithHandler:^(EHIUserRentals *rentals, EHIServicesError *error) {
        self.isLoading = NO;
        ehi_call(handler)(error.internalError);
    }];
}

@end

NS_ASSUME_NONNULL_END
