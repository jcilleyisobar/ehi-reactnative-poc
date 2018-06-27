//
//  EHICustomerSupportViewModel.h
//  Enterprise
//
//  Created by fhu on 6/25/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHISectionHeader.h"

typedef NS_ENUM(NSUInteger, EHICustomerSupportSection) {
    EHICustomerSupportSectionHeader,
    EHICustomerSupportSectionCall,
    EHICustomerSupportSectionMoreOptions,
};

@interface EHICustomerSupportViewModel : EHIViewModel

@property (copy  , nonatomic) NSString *title;
@property (strong, nonatomic) NSArray *callModels;
@property (strong, nonatomic) NSArray *moreOptionsModels;

- (EHISectionHeaderModel *)headerForSection:(EHICustomerSupportSection)section;
- (void)selectIndexPath:(NSIndexPath *)indexPath;

@end
