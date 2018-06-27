//
//  EHITerminalDirectionsViewController.m
//  Enterprise
//
//  Created by Pawel Bragoszewski on 02.03.2015.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationWayfindingViewController.h"
#import "EHILocationWayfindingViewModel.h"
#import "EHIListCollectionView.h"
#import "EHISectionHeader.h"
#import "EHILocationWayfindingStepCell.h"

@interface EHILocationWayfindingViewController () <EHIListCollectionViewDelegate>
@property (strong, nonatomic) EHILocationWayfindingViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@end


@implementation EHILocationWayfindingViewController

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        self.viewModel = [EHILocationWayfindingViewModel new];
    }
    
    return self;
}

# pragma mark - View Lifecycle

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    EHIListDataSourceSection *section = self.collectionView.section;
    section.klass = [EHILocationWayfindingStepCell class];
    section.isDynamicallySized = YES;
}

# pragma mark - Reactions

- (void)registerReactions:(EHILocationWayfindingViewModel *)model
{
    [super registerReactions:model];
    
    EHIListDataSourceSection *section = self.collectionView.section;
    
    model.bind.map(@{
        source(model.title)       : dest(self, .title),
        source(model.wayfindings) : dest(section, .models),
    });
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenLocationWayfinding;
}

@end
