//
//  EHIMenuCell.h
//  Enterprise
//
//  Created by Ty Cobb on 3/30/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"
#import "EHIMenuItem.h"

static const CGFloat EHIMenuCellDefaultHeight = 58.0;

@interface EHIMenuCell : EHICollectionViewCell
/** The menu item corresponding to this cell */
@property (strong, nonatomic, readonly) EHIMenuItem *model;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;

- (CGSize)titleBasedContentSize;

@end

/** An informal delegate protocol whose messages are sent up the responder chain */
@protocol EHIMenuCellActions <NSObject> @optional
/** Called by cells if an internal UI event should trigger their action */
- (void)didTriggerActionForCell:(EHIMenuCell *)cell;
@end
