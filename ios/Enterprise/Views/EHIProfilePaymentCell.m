//
//  EHIProfilePaymentCell.m
//  Enterprise
//
//  Created by fhu on 5/19/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIProfilePaymentCell.h"
#import "EHIProfilePaymentViewModel.h"
#import "EHIListCollectionView.h"
#import "EHIProfilePaymentItemCell.h"
#import "EHIProfilePaymentStatusCell.h"

@interface EHIProfilePaymentCell()
@property (strong, nonatomic) EHIProfilePaymentViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@end

@implementation EHIProfilePaymentCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if (self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIProfilePaymentViewModel new];
    }
    
    return self;
}

- (void)updateWithModel:(EHIProfilePaymentViewModel *)model metrics:(EHILayoutMetrics *)metrics
{
    [super updateWithModel:model metrics:metrics];
    
    [self.collectionView.sections construct:@{
        @(EHIProfilePaymentSectionMethods) : EHIProfilePaymentItemCell.class,
        @(EHIProfilePaymentSectionStatus)   : EHIProfilePaymentStatusCell.class,
    }];
    
    EHIListDataSourceSection *payments = self.collectionView.sections[EHIProfilePaymentSectionMethods];
    payments.models = self.viewModel.paymentMethodsModel;
    
    EHIListDataSourceSection *status = self.collectionView.sections[EHIProfilePaymentSectionStatus];
    status.model = self.viewModel.statusModel;

    for(EHIListDataSourceSection *section in self.collectionView.sections) {
        section.isDynamicallySized = YES;
    }
}

#pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = self.collectionView.contentSize.height
    };
}

@end
