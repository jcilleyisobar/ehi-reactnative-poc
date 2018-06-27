//
//  EHIKeyFactsViewModel.h
//  Enterprise
//
//  Created by fhu on 11/4/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIKeyFactsSectionContentViewModel.h"

typedef NS_ENUM(NSUInteger, EHIKeyFactsSection) {
    EHIKeyFactsSectionHeader,
    EHIKeyFactsSectionContent,
    EHIKeyFactsSectionFooter
};

@interface EHIKeyFactsViewModel : EHIViewModel <MTRReactive>

@property (copy  , nonatomic, readonly) NSString *title;
@property (strong, nonatomic, readonly) NSArray *contentList;
@property (strong, nonatomic, readonly) EHIReservation *reservation;

@end
