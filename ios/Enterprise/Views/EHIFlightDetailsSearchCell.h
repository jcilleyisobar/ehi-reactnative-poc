//
//  EHIFlightDetailsSearchCell.h
//  Enterprise
//
//  Created by Rafael Ramos on 6/23/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"
#import "EHITextField.h"

@interface EHIFlightDetailsSearchCell : EHICollectionViewCell
@property (weak  , nonatomic, readonly) UILabel *titleLabel;
@property (weak  , nonatomic, readonly) EHITextField *searchField;

@end

@protocol EHIFlightDetailsSearchActions <NSObject> @optional
- (void)searchCellDidTap;
@end
