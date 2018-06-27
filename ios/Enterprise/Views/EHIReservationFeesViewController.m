//
//  EHIReservationFeesViewController.m
//  Enterprise
//
//  Created by Ty Cobb on 4/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationFeesViewController.h"
#import "EHIReservationFeesViewModel.h"
#import "EHIReservationFeeCell.h"
#import "EHIListCollectionView.h"
#import "EHIButton.h"

@interface EHIReservationFeesViewController () <EHIListCollectionViewDelegate>
@property (strong, nonatomic) EHIReservationFeesViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet EHIButton *actionButton;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@end

@implementation EHIReservationFeesViewController

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        self.viewModel = [EHIReservationFeesViewModel new];
    }
    
    return self;
}

# pragma mark - View Lifecycle

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.collectionView.section.klass = [EHIReservationFeeCell class];
    self.collectionView.section.isDynamicallySized = YES;
}

- (UIColor *)backgroundColor
{
    return [UIColor clearColor];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIReservationFeesViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.fees) : dest(self, .collectionView.section.models),
        source(model.title) : dest(self, .titleLabel.text),
        source(model.confirmationTitle) : dest(self, .actionButton.ehi_title),
        source(model.selectedPath) : ^(NSIndexPath *indexPath) {
            [self.collectionView ehi_invalidateLayoutAnimated:YES];
        }
    });
}

# pragma mark - UICollectionView

//
// UICollectionViewDelegate
//

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    [self.collectionView deselectItemAtIndexPath:indexPath animated:NO];
    [self.viewModel selectFeeAtIndex:indexPath.item];
}

# pragma mark - Interface Actions

- (IBAction)didTapCloseButton:(UIButton *)button
{
    [self.viewModel dismiss];
}

- (IBAction)didTapActionButton:(UIButton *)button
{
    [self.viewModel dismiss];
}

# pragma mark - Accessors

- (EHIReservationFeeCell *)feeCellAtIndexPath:(NSIndexPath *)indexPath
{
    return (EHIReservationFeeCell *)[self.collectionView cellForItemAtIndexPath:indexPath];
}

# pragma mark - EHIViewController

- (EHIModalTransitionStyle)customModalTransitionStyle
{
    return EHIModalTransitionStyleOverlayFullscreen;
}

# pragma mark - Analytics

- (void)updateAnalyticsContext:(EHIAnalyticsContext *)context
{
    [super updateAnalyticsContext:context];

    context.routerState = EHIScreenReservationFees;
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenReservationFees;
}

@end
