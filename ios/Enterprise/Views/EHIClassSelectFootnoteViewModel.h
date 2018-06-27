//
//  EHIClassSelectFootnoteViewModel.h
//  Enterprise
//
//  Created by mplace on 6/2/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIClassSelectFootnoteViewModel : EHIViewModel
@property (copy  , nonatomic, readonly) NSAttributedString *makeModelTitle;
@property (copy  , nonatomic) NSAttributedString *sourceCurrencyTitle;
@property (assign, nonatomic) BOOL hidesSourceCurrencyTitle;
@end
