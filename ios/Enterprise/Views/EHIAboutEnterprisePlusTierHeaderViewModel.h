//
//  EHIAboutEnterprisePlusTierHeaderViewModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 12/23/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"

@interface EHIAboutEnterprisePlusTierHeaderViewModel : EHIViewModel <MTRReactive>
@property (copy, nonatomic, readonly) NSString *title;
@property (copy, nonatomic, readonly) NSString *firstLine;
@property (copy, nonatomic, readonly) NSString *secondLine;
@end
