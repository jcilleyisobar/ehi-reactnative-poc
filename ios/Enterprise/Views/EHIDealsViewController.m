//
//  EHIDealsViewController.m
//  Enterprise
//
//  Created by Rafael Machado on 19/06/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIDealsViewController.h"
#import "EHIDealsViewModel.h"
#import "EHIListCollectionView.h"
#import "EHIDealLabelCell.h"
#import "EHIDealCardCell.h"
#import "EHIDealHeaderCell.h"

@interface EHIDealsViewController () <EHIListCollectionViewDelegate>
@property (strong, nonatomic) EHIDealsViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@end

@implementation EHIDealsViewController

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIDealsViewModel new];
    }

    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [self.collectionView.sections construct:@{
        @(EHIDealsSectionWeekendSpecial) : EHIDealCardCell.class,
        @(EHIDealsSectionLocal)          : EHIDealCardCell.class,
        @(EHIDealsSectionInternacional)  : EHIDealLabelCell.class,
        @(EHIDealsSectionOther)          : EHIDealLabelCell.class
    }];
    
    for(EHIListDataSourceSection *section in self.collectionView.sections) {
        section.isDynamicallySized = [self isDynamicallySized:section.index];
        
        section.header.klass = EHIDealHeaderCell.class;
        section.header.model = [self.viewModel headerForSection:section.index];
        section.header.isDynamicallySized = YES;
    }
    
    self.collectionView.sections[EHIDealsSectionWeekendSpecial].metrics = [self cardMetrics];
    self.collectionView.sections[EHIDealsSectionLocal].metrics          = [self cardMetrics];
}

- (EHILayoutMetrics *)cardMetrics
{
    EHILayoutMetrics *metrics = EHIDealCardCell.metrics.copy;
    metrics.fixedSize = (CGSize){ .width = EHILayoutValueNil, .height = 250.f };
        
    return metrics;
}

- (BOOL)isDynamicallySized:(EHIDealsSection)section
{
    return !(section == EHIDealsSectionLocal || section == EHIDealsSectionWeekendSpecial);
}

# pragma mark - Reactions

- (void)registerReactions:(EHIDealsViewModel *)model
{
    [super registerReactions:model];
    
    EHIListDataSourceSection *weekendSpecial = self.collectionView.sections[EHIDealsSectionWeekendSpecial];
    EHIListDataSourceSection *local          = self.collectionView.sections[EHIDealsSectionLocal];
    EHIListDataSourceSection *internacional  = self.collectionView.sections[EHIDealsSectionInternacional];
    EHIListDataSourceSection *other          = self.collectionView.sections[EHIDealsSectionOther];
    
    model.bind.map(@{
        source(model.title)              : dest(self, .title),
        source(model.weekendSpecial)     : dest(weekendSpecial, .model),
        source(model.localDeals)         : dest(local, .models),
        source(model.internacionalDeals) : dest(internacional, .models),
        source(model.otherDeals)         : dest(other, .models),
    });
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenDeals;
}

@end
