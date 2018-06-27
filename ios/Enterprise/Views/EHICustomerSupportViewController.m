//
//  EHICustomerSupportViewController.m
//  Enterprise
//
//  Created by fhu on 6/25/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICustomerSupportViewController.h"
#import "EHIListCollectionView.h"
#import "EHICustomerSupportViewModel.h"
#import "EHICustomerSupportHeaderCell.h"
#import "EHICustomerSupportSelectionCell.h"

@interface EHICustomerSupportViewController ()<EHIListCollectionViewDelegate>
@property (strong, nonatomic) EHICustomerSupportViewModel *viewModel;
@property (weak, nonatomic) IBOutlet EHIListCollectionView *collectionView;
@end

@implementation EHICustomerSupportViewController

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHICustomerSupportViewModel new];
    }
    
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [self.collectionView.sections construct:@{
        @(EHICustomerSupportSectionHeader)        : EHICustomerSupportHeaderCell.class,
        @(EHICustomerSupportSectionCall)          : EHICustomerSupportSelectionCell.class,
        @(EHICustomerSupportSectionMoreOptions)   : EHICustomerSupportSelectionCell.class,
    }];

    // configure cells
    for(EHIListDataSourceSection *section in self.collectionView.sections) {
        section.isDynamicallySized = YES;
        section.header.klass = EHISectionHeader.class;
        section.header.model = [self.viewModel headerForSection:section.index];
    }
    
    self.collectionView.sections[EHICustomerSupportSectionHeader].model = [EHIModel placeholder];
}

# pragma mark - Reactions

- (void)registerReactions:(EHICustomerSupportViewModel *)model
{
    [super registerReactions:model];
    
    EHIListDataSourceSection *call = self.collectionView.sections[EHICustomerSupportSectionCall];
    EHIListDataSourceSection *moreOptions = self.collectionView.sections[EHICustomerSupportSectionMoreOptions];
    
    model.bind.map(@{
        source(model.title)             : dest(self, .title),
        source(model.callModels)        : dest(call, .models),
        source(model.moreOptionsModels) : dest(moreOptions, .models)
    });
}

# pragma mark -  UICollectionView

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    [self.viewModel selectIndexPath:indexPath];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenCustomerSupport;
}

# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenCustomerSupport state:nil];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

@end
