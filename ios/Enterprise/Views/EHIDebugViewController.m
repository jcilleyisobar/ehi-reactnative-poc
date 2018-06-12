//
//  EHIDebugViewController.m
//  Enterprise
//
//  Created by Alex Koller on 11/24/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIDebugViewController.h"
#import "EHIDebugViewModel.h"
#import "EHIListCollectionView.h"
#import "EHIDebugOptionCell.h"

@interface EHIDebugViewController () <EHIListCollectionViewDelegate>
@property (strong, nonatomic) EHIDebugViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@end

@implementation EHIDebugViewController

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIDebugViewModel new];
    }
    
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.collectionView.section.klass = EHIDebugOptionCell.class;
    self.collectionView.section.isDynamicallySized = YES;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIDebugViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.title)      : dest(self, .title),
        source(model.viewModels) : dest(self, .collectionView.section.models),
    });
}

# pragma mark - EHIListCollectionViewDelegate

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    [self.viewModel selectItem:indexPath.item];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenDebug;
}

# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    [super prepareToUpdateAnalyticsContext];
    [EHIAnalytics changeScreen:EHIScreenDebug state:nil];
}

- (void)didUpdateAnalyticsContext
{
    [super didUpdateAnalyticsContext];
    [EHIAnalytics trackState:nil];
}

@end
