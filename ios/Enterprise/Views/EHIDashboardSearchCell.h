//
//  EHIDashboardSearchCell.h
//  Enterprise
//
//  Created by Ty Cobb on 1/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"
#import "EHITextField.h"

@interface EHIDashboardSearchCell : EHICollectionViewCell

/** The title label for the cell; used to animate the cell's title during the locations transition. */
@property (weak  , nonatomic, readonly) UILabel *titleLabel;
/** The search field for the cell; used to animate the cell's search field during the locations transition. */
@property (weak  , nonatomic, readonly) EHITextField *searchField;

- (void)setBorderOpacity:(CGFloat)opacity;

@end

@protocol EHIDashboardSearchActions <NSObject>
/** Called by the search cell when the scroll indicator button is tapped. */
- (void)searchCellDidTapScrollButton:(EHIDashboardSearchCell *)searchCell;
@end
