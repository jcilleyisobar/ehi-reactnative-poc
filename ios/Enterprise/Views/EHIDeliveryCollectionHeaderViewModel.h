//
//  EHIDeliveryCollectionHeaderViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 6/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIDeliveryCollectionHeaderViewModel : EHIViewModel <MTRReactive>

@property (copy, nonatomic, readonly) NSString *title;
@property (copy, nonatomic, readonly) NSAttributedString *detailsTitle;
@property (copy, nonatomic, readonly) NSAttributedString *chargesTitle;

@end
