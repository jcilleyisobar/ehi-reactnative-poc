//
//  EHIDateTimeComponentFilterCell.h
//  Enterprise
//
//  Created by Rafael Ramos on 5/4/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"

@interface EHIDateTimeComponentFilterCell : EHICollectionViewCell

@end

@protocol EHIDateTimeComponentFilterCellActions <NSObject>
- (void)dateTimeComponentDidTapOnSection:(NSNumber *)section;
@optional
- (void)dateTimeComponentDidTapOnCleanSection:(NSNumber *)section;
@end
