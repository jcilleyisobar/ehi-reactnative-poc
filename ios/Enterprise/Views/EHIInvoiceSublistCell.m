//
//  EHIInvoiceSublistCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 7/20/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIInvoiceSublistCell.h"
#import "EHIInvoiceSublistViewModel.h"
#import "EHIListCollectionView.h"
#import "EHIInvoiceSublistItemCell.h"
#import "EHIReservationSublistSection.h"
#import "EHIInvoiceSublistSectionHeader.h"
#import "EHIReservationSublistSectionFooter.h"

@interface EHIInvoiceSublistCell ()
@property (strong, nonatomic) EHIInvoiceSublistViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@end

@implementation EHIInvoiceSublistCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIInvoiceSublistViewModel new];
    }

    return self;
}

- (void)updateWithModel:(EHIInvoiceSublistViewModel *)model metrics:(EHILayoutMetrics *)metrics
{
    [self.collectionView prepareForReuse];
    
    [super updateWithModel:model metrics:metrics];

    model.sections.each(^(EHIReservationSublistSection *sublistSection, NSInteger index){
        EHIListDataSourceSection *section = self.collectionView.sections[index];
        BOOL lastSection = index == model.sections.count - 1;
        
        // update the primary section
        section.klass  = EHIInvoiceSublistItemCell.class;
        section.models = sublistSection.models;
        section.isDynamicallySized = YES;
        
        // create a header from the title
        section.header.klass = EHIInvoiceSublistSectionHeader.class;
        section.header.model = sublistSection.title;
        section.header.isDynamicallySized = YES;
        section.header.metrics.backgroundColor = [UIColor clearColor];
    
        if (model.sections.count > 1){
            section.footer.klass   = EHIReservationSublistSectionFooter.class;
            section.footer.model   = [EHIModel placeholder];
            EHILayoutMetrics *metrics = [EHIReservationSublistSectionFooter defaultMetrics];
            metrics.fixedSize = (CGSize) { .width = EHILayoutValueNil, .height = EHILightPadding};
            section.footer.metrics = metrics;
            if(!lastSection){
                section.footer.metrics.primaryColor = [UIColor ehi_grayColor1];
            }
        }
    });
 }

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = self.collectionView.contentSize.height + EHILightPadding
    };
}

@end
