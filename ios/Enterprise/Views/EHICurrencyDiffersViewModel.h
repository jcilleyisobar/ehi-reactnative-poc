//
//  EHICurrencyDiffersViewModel.h
//  Enterprise
//
//  Created by Rafael Machado on 06/10/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHICurrencyDiffersViewModel : EHIViewModel <MTRReactive>
@property (copy, nonatomic, readonly) NSAttributedString *title;
@end
