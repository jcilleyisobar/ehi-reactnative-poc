//
//  EHIDealContentViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 08/06/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIDealContentViewModel : EHIViewModel <MTRReactive>
@property (copy, nonatomic, readonly) NSAttributedString *contentText;
@end
