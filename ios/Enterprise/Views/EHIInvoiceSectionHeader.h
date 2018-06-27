//
//  EHIInvoiceSectionHeader.h
//  Enterprise
//
//  Created by Rafael Ramos on 7/26/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHICollectionViewCell.h"

@interface EHIInvoiceSectionHeader : EHICollectionViewCell

@end

@protocol EHIInvoiceSectionHeaderActions <NSObject>
- (void)invoiceSectionHeaderDidTapActionButton:(EHIInvoiceSectionHeader *)sectionHeader;
@end