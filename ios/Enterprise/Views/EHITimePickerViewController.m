//
//  EHITimePickerViewController.m
//  Enterprise
//
//  Created by mplace on 3/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHITimePickerViewController.h"
#import "EHITimePickerViewModel.h"
#import "EHIListCollectionView.h"
#import "EHITimePickerMaskView.h"
#import "EHITimePickerActiveTimeCell.h"
#import "EHITimePickerTimeCell.h"
#import "EHIButton.h"
#import "EHIActivityIndicator.h"

@interface EHITimePickerViewController () <UICollectionViewDataSource, UIScrollViewDelegate, UICollectionViewDelegate>
@property (strong, nonatomic) EHITimePickerViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *contentContainer;
@property (weak  , nonatomic) IBOutlet UICollectionView *timeCollectionView;
@property (weak  , nonatomic) IBOutlet UICollectionView *selectionCollectionView;
@property (weak  , nonatomic) IBOutlet UIImageView *timeIconImageView;
@property (weak  , nonatomic) IBOutlet EHIButton *timeSelectionButton;
@property (weak  , nonatomic) IBOutlet EHIButton *timeInfoButton;
@property (weak  , nonatomic) IBOutlet EHIActivityIndicator *activityIndicator;
@property (weak  , nonatomic) IBOutlet EHITimePickerMaskView *maskedCollectionViewContainer;
@property (weak  , nonatomic) IBOutlet NSLayoutConstraint *contentLeading;

@property (weak  , nonatomic) IBOutlet UIView *lastReturnTimeBanner;
@property (weak  , nonatomic) IBOutlet UILabel *lastReturnTimeTitleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *lastReturnTimeTextLabel;

@property (weak  , nonatomic) IBOutlet UIView *closedLocationBanner;
@property (weak  , nonatomic) IBOutlet UILabel *needLocationOpenLabel;
@property (weak  , nonatomic) IBOutlet EHIButton *searchForLocationsButton;
@end

@implementation EHITimePickerViewController

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHITimePickerViewModel new];
    }
    
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    // style the time selection button
    self.timeSelectionButton.titleLabel.numberOfLines = 1;
    self.timeSelectionButton.imageHorizontalAlignment = UIControlContentHorizontalAlignmentRight;
    
    // style the time info button
    self.timeInfoButton.imageHorizontalAlignment = UIControlContentHorizontalAlignmentRight;
    self.timeInfoButton.titleLabel.numberOfLines = 1;
    
    // set the deceleration rates for our collection views
    self.timeCollectionView.decelerationRate = UIScrollViewDecelerationRateFast;
    self.selectionCollectionView.decelerationRate = UIScrollViewDecelerationRateFast;
    
    self.searchForLocationsButton.titleLabel.numberOfLines = 0;
}

# pragma mark - Reactions

- (void)registerReactions:(EHITimePickerViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(reloadCollectionView:)];
    [MTRReactor autorun:self action:@selector(invalidateTimeInfoButtonStyling:)];
    [MTRReactor autorun:self action:@selector(invalidateLocationType:)];
    [MTRReactor autorun:self action:@selector(animateSelectionTransition:)];
    
    model.bind.map(@{
        source(model.title)                         : dest(self, .title),
        source(model.isLoading)                     : dest(self, .activityIndicator.isAnimating),
        source(model.selectionButtonTitle)          : dest(self, .timeSelectionButton.ehi_title),
        source(model.infoButtonIsHidden)            : dest(self, .timeInfoButton.hidden),
        source(model.infoButtonIsSelectable)        : dest(self, .timeInfoButton.enabled),
        source(model.needLocationOpenLabelTitle)    : dest(self, .needLocationOpenLabel.text),
        source(model.searchForLocationsButtonTitle) : dest(self, .searchForLocationsButton.ehi_title),
        source(model.lastReturnTimeTitle)           : dest(self, .lastReturnTimeTitleLabel.text),
        source(model.lastReturnTimeText)            : dest(self, .lastReturnTimeTextLabel.text),
    });
}

- (void)reloadCollectionView:(MTRComputation *)computation
{
    // accessing the .times property to trigger the reaction
    if(self.viewModel.times.count) {
        // reload both collection views
        [self.timeCollectionView reloadData];
        [self.selectionCollectionView reloadData];
      
        // if the container is hidden, run the initial animations
        if(!self.maskedCollectionViewContainer.alpha) {
            // alpha in the collection views
            UIView.animate(YES).duration(0.3).transform(^{
                self.maskedCollectionViewContainer.alpha = 1.0f;
                self.timeCollectionView.alpha = 1.0f;
            }).start(nil);
        }
        
        // run a spring animation on the intial path
        UIViewAnimationOptions options = UIViewAnimationOptionCurveLinear | UIViewAnimationOptionAllowUserInteraction;
        UIView.animate(YES).duration(0.6).damping(0.8).options(options).transform(^{
            [self.selectionCollectionView scrollToItemAtIndexPath:self.viewModel.initialIndexPath atScrollPosition:UICollectionViewScrollPositionCenteredVertically animated:NO];
        }).start(^(BOOL finished) {
            [self didFinishScrolling];
        });
    }
}

- (void)animateSelectionTransition:(MTRComputation *)computation
{
    // don't animate on first run
    if(computation.isFirstRun) {
        return;
    }
    
    BOOL isPickingReturnTime = self.viewModel.isPickingReturnTime;
    
    // we're going to snapshot the view and place it on top of the real collection view
    UIView *content  = self.contentContainer;
    UIView *snapshot = [content ehi_snapshotViewAndFrameAfterScreenUpdates:NO];
   
    // position the snapshot properly in the hierarchy
    if(isPickingReturnTime) {
        content.layer.ehi_showsShadow = YES;
        [self.view insertSubview:snapshot belowSubview:content];
    } else {
        snapshot.layer.ehi_showsShadow = YES;
        [self.view insertSubview:snapshot aboveSubview:content];
    }

    // setup the initial state of the view
    CGFloat direction = isPickingReturnTime ? 1.0f : -1.0f;
    CGFloat translationScale = isPickingReturnTime ? 1.0f : 0.5f;
    
    self.contentLeading.constant = content.bounds.size.width * direction * translationScale;
    [self.view layoutIfNeeded];
   
    // animate the content / snapshot to fake the push transition, then remove it
    self.contentLeading.constant = 0.0f;
    UIView.animate(YES).duration(0.4).transform(^{
        [self.view layoutIfNeeded];
        snapshot.frame = CGRectOffset(snapshot.frame, -direction * (1.5f - translationScale) * snapshot.bounds.size.width, 0.0f);
    }).start(^(BOOL finished) {
        // clean up after our animation
        content.layer.ehi_showsShadow = NO;
        [snapshot removeFromSuperview];
    });
}

- (void)invalidateTimeInfoButtonStyling:(MTRComputation *)computation
{
    // style the after hours button that appears above the selection mask
    self.timeInfoButton.type = self.viewModel.infoButtonType;
    self.timeInfoButton.ehi_title = self.viewModel.infoButtonTitle;
    
    UIImage *image = [self.timeInfoButton.ehi_image imageWithRenderingMode:UIImageRenderingModeAlwaysTemplate];
    [self.timeInfoButton setImage:image forState:UIControlStateNormal];
    [self.timeInfoButton setTintColor:[UIColor ehi_greenColor]];
}

- (void)invalidateLocationType:(MTRComputation *)computation
{
    [self invalidateSelectButton];
    [self invalidateClosedBanner];
    [self invalidateLastReturnTimeBanner];
}

- (void)invalidateClosedBanner
{
    CGAffineTransform transform = CGAffineTransformIdentity;
    CGFloat alpha = 1.0f;
    BOOL shouldHideBanner = !self.viewModel.currentTimeIsClosed;

    if(shouldHideBanner) {
        alpha = 0.0f;
        transform = CGAffineTransformScale(transform, 0.95, 0.95);
        transform = CGAffineTransformTranslate(transform, 0.0f, CGRectGetHeight(self.closedLocationBanner.frame));
    }

    [UIView animateWithDuration:0.3 animations:^{
        self.closedLocationBanner.alpha = alpha;
        self.closedLocationBanner.transform = transform;
    }];
}

- (void)invalidateLastReturnTimeBanner
{
    CGAffineTransform transform = CGAffineTransformIdentity;
    CGFloat alpha = 1.0f;
    BOOL shouldHideBanner = !self.viewModel.isLastReturnTime;

    if(shouldHideBanner) {
        alpha = 0.0f;
        transform = CGAffineTransformScale(transform, 0.95, 0.95);
        transform = CGAffineTransformTranslate(transform, 0.0f, CGRectGetHeight(self.lastReturnTimeBanner.frame));
    }

    [UIView animateWithDuration:0.3 animations:^{
        self.lastReturnTimeBanner.alpha = alpha;
        self.lastReturnTimeBanner.transform = transform;
    }];
}


- (void)invalidateSelectButton
{
    BOOL isClosed = self.viewModel.currentTimeIsClosed;

    UIImage *image = !isClosed ? [UIImage imageNamed:@"arrow_white"] : nil;
    [self.timeSelectionButton setImage:image forState:UIControlStateNormal];
}

# pragma mark - Actions

- (IBAction)didTapSelectButton:(UIButton *)button
{
    BOOL isSelectable = self.viewModel.currentTimeIsSelectable;
    if(isSelectable) {
        [self.viewModel selectTimeAtIndexPath:self.indexPathForSelectableTime];
    }
}

- (IBAction)didTapInfoButton:(UIButton *)button
{
    [self.viewModel triggerInfoAction];
}

- (IBAction)didTapSearchButton:(UIButton *)button
{
    [self.viewModel showLocationsMap];
}

# pragma mark - UICollectionViewDataSource

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section
{
    return self.viewModel.times.count;
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    // grab the time for the index
    EHITimePickerTime *time    = [self.viewModel.times objectAtIndex:indexPath.row];
    UICollectionViewCell *cell = [UICollectionViewCell new];
    
    if(collectionView == self.selectionCollectionView) {
        cell = [collectionView ehi_dequeueReusableCellWithClass:[EHITimePickerActiveTimeCell class] model:time atIndexPath:indexPath];
    } else if(collectionView == self.timeCollectionView) {
        cell = [collectionView ehi_dequeueReusableCellWithClass:[EHITimePickerTimeCell class] model:time atIndexPath:indexPath];
    }
    
    return cell;
}

# pragma mark - UICollectionViewDelegate

- (BOOL)collectionView:(UICollectionView *)collectionView shouldSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    return [self.viewModel shouldSelectTimeAtIndexPath:indexPath];
}

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    // tapping on a time should scroll it under the selection mask
    if(collectionView == self.selectionCollectionView) {
        [collectionView scrollToItemAtIndexPath:indexPath atScrollPosition:UICollectionViewScrollPositionCenteredVertically animated:YES];
    }
}

# pragma mark - UICollectionViewDelegateFlowLayout

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)layout sizeForItemAtIndexPath:(NSIndexPath *)indexPath
{
    return [EHITimePickerActiveTimeCell sizeForContainerSize:collectionView.bounds.size];
}

- (UIEdgeInsets)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout insetForSectionAtIndex:(NSInteger)section
{
    CGSize  itemSize = [EHITimePickerActiveTimeCell sizeForContainerSize:self.selectionCollectionView.bounds.size];
    CGFloat offset   = (self.selectionCollectionView.bounds.size.height - itemSize.height) / 2.0f;
    
    return UIEdgeInsetsMake(offset, 0, offset, 0);
}

# pragma mark - UIScrollViewDelegate

- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    // keep the two collection view content offsets in sync
    if(scrollView == self.selectionCollectionView) {
        self.timeCollectionView.contentOffset = self.selectionCollectionView.contentOffset;
    }
}

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView
{
    [self didBeginScrolling];
}

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate
{
    if(!decelerate) {
        [self didFinishScrolling];
    }
}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView
{
    [self didFinishScrolling];
}

- (void)scrollViewDidEndScrollingAnimation:(UIScrollView *)scrollView
{
    [self didFinishScrolling];
}

- (void)scrollViewWillEndDragging:(UIScrollView *)scrollView withVelocity:(CGPoint)velocity targetContentOffset:(inout CGPoint *)targetOffset
{
    targetOffset->y = [self scrollView:scrollView targetContentOffsetForProposedContentOffset:*targetOffset].y;
}

//
// Helpers
//

- (void)didBeginScrolling
{
    [self updateIsScrolling:YES];
}

- (void)didFinishScrolling
{
    [self.viewModel setIndexPathForCurrentTime:self.indexPathForSelectableTime];
    [self updateIsScrolling:NO];
}

- (void)updateIsScrolling:(BOOL)isScrolling
{
    // construct the animation
    UIView.animate(YES).duration(0.25).transform(^{
        self.timeInfoButton.alpha        = isScrolling ? 0.0f : 1.0f;
        self.timeSelectionButton.enabled = !isScrolling;
        self.timeSelectionButton.alpha   = self.timeSelectionButton.isEnabled ? 1.0f : 0.5f;
    }).start(nil);
}

- (CGPoint)scrollView:(UIScrollView *)scrollView targetContentOffsetForProposedContentOffset:(CGPoint)proposedOffset
{
    // if we've reached the bottom, then we don't need to do any snapping
    if(proposedOffset.y == scrollView.contentSize.height - scrollView.bounds.size.height) {
        return proposedOffset;
    }
    
    // otherwise, snap to the nearest row
    CGFloat height = [EHITimePickerActiveTimeCell sizeForContainerSize:scrollView.bounds.size].height;
    CGFloat inset  = scrollView.contentInset.top;
    
    proposedOffset.y = roundf((proposedOffset.y + inset) / height) * height - inset;
    return proposedOffset;
}

# pragma mark - Utilities

- (NSIndexPath *)indexPathForSelectableTime
{
    // grab the collection view center
    CGPoint center = self.selectionCollectionView.center;
    // offset with the current content offset
    center.y += self.selectionCollectionView.contentOffset.y;
    
    // ask the collection for the index at the adjusted center point
    return [self.selectionCollectionView indexPathForItemAtPoint:center];
}

# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenReservationTimeSelect state:EHIScreenReservationTimeSelect];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenReservationTimeSelect;
}

@end
