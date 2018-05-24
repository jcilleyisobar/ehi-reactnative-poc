//
//  EHIProfileBasicViewModel.h
//  Enterprise
//
//  Created by fhu on 5/18/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIProfileBasicViewModel : EHIViewModel <MTRReactive>

@property (copy  , nonatomic) NSString *title;
@property (copy  , nonatomic) NSAttributedString *attributedText;

@end
