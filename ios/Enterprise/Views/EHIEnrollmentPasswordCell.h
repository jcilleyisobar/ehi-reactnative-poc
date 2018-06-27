//
//  EHIEnrollmentPasswordCell.h
//  Enterprise
//
//  Created by Rafael Ramos on 8/10/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"

@interface EHIEnrollmentPasswordCell : EHICollectionViewCell

@end

@protocol EHIEnrollmentPasswordCellActions <NSObject>
- (void)passwordCellDidShowNoMatch:(EHIEnrollmentPasswordCell *)cell;
- (void)passwordCellWillDismissKeyboard:(EHIEnrollmentPasswordCell *)cell;
@end