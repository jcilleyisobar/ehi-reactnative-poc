//
//  EHIAboutEnterprisePlusFooterViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 12/23/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIAboutEnterprisePlusFooterViewModel : EHIViewModel <MTRReactive>
@property (copy, nonatomic, readonly) NSString *title;

- (void)showDetails;

@end
