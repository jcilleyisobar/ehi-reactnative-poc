//
//  EHIReservationConfirmationExtrasCell.m
//  Enterprise
//
//  Created by Alex Koller on 4/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationSublistCell.h"
#import "EHIReservationSublistViewModel.h"
#import "EHIReservationSublistSectionHeader.h"
#import "EHIReservationSublistSectionFooter.h"
#import "EHIReservationLineItemCell.h"
#import "EHIReservationExtraCell.h"
#import "EHIListCollectionView.h"
#import "EHIRestorableConstraint.h"

@interface EHIReservationSublistCell () <UICollectionViewDelegateFlowLayout>
@property (strong, nonatomic) EHIReservationSublistViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@end

@implementation EHIReservationSublistCell

- (void)updateWithModel:(EHIReservationSublistViewModel *)model metrics:(EHILayoutMetrics *)metrics
{
    [self.collectionView prepareForReuse];
    
    [super updateWithModel:model metrics:metrics];
    
    [self.contentView setBackgroundColor:metrics.backgroundColor];
    
    model.sections.each(^(EHIReservationSublistSection *data, NSInteger index) {
        EHIReservationLineItemType lineItemType = [((id<EHIReservationLineItemRenderable>)data.models.firstObject) type];
        EHIListDataSourceSection *section       = self.collectionView.sections[index];
        BOOL lastSection                        = index == model.sections.count - 1;
      
        // update the primary section
        section.klass = [self cellClassForType:model.type];
        section.models = data.models;
        section.isDynamicallySized = YES;
        section.metrics = [section.klass defaultMetrics];
        section.metrics.backgroundColor = [self backgroundColorForType:lineItemType];
        
        // create a header from the title
        section.header.klass = EHIReservationSublistSectionHeader.class;
        section.header.model = data.title;
        section.header.metrics = [EHIReservationSublistSectionHeader defaultMetrics];
        section.header.metrics.backgroundColor = [self backgroundColorForType:lineItemType];
        
        section.footer.klass = EHIReservationSublistSectionFooter.class;
        section.footer.model = [EHIModel placeholder];
        section.footer.metrics = [EHIReservationSublistSectionFooter defaultMetrics];
        section.footer.metrics.primaryColor = lastSection ? [UIColor clearColor] : [UIColor ehi_grayColor1];
        section.footer.metrics.backgroundColor = [self backgroundColorForType:lineItemType];
    });
}

//
// Helpers
//

- (Class<EHIListCell>)cellClassForType:(EHIReservationSublistType)type
{
    switch(type) {
        case EHIReservationSublistTypeExtra:
            return [EHIReservationExtraCell class];
        case EHIReservationSublistTypeLineItem:
            return [EHIReservationLineItemCell class];
    }
}

- (UIColor *)backgroundColorForType:(EHIReservationLineItemType)type
{
    switch(type) {
        case EHIReservationLineItemTypeRedemption:
            return [UIColor ehi_grayColor0];
        default:
            return [UIColor clearColor];
    }
}

# pragma mark - UICollectionViewDelegate

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    [self.viewModel selectItemAtIndexPath:indexPath];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = self.collectionView.contentSize.height
    };
}

@end
