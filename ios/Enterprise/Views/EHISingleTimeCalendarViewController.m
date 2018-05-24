//
//  EHISingleTimeCalendarViewController.m
//  Enterprise
//
//  Created by Rafael Ramos on 5/16/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHISingleTimeCalendarViewController.h"
#import "EHISingleTimeCalendarViewModel.h"
#import "EHIListCollectionView.h"
#import "EHITimePickerMaskView.h"
#import "EHITimePickerActiveTimeCell.h"
#import "EHITimePickerTimeCell.h"
#import "EHIButton.h"

@interface EHISingleTimeCalendarViewController () <UICollectionViewDataSource, UIScrollViewDelegate, UICollectionViewDelegate>
@property (strong, nonatomic) EHISingleTimeCalendarViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *contentContainer;
@property (weak  , nonatomic) IBOutlet UICollectionView *timeCollectionView;
@property (weak  , nonatomic) IBOutlet UICollectionView *selectionCollectionView;
@property (weak  , nonatomic) IBOutlet UIImageView *timeIconImageView;
@property (weak  , nonatomic) IBOutlet EHIButton *timeSelectionButton;
@property (weak  , nonatomic) IBOutlet EHITimePickerMaskView *maskedCollectionViewContainer;
@property (weak  , nonatomic) IBOutlet NSLayoutConstraint *contentLeading;
@end

@implementation EHISingleTimeCalendarViewController

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHISingleTimeCalendarViewModel new];
    }
    
    return self;
}

- (void)updateWithAttributes:(NAVAttributes *)attributes
{
    [super updateWithAttributes:attributes];
    
    EHISingleTimeCalendarViewModel *viewModel = attributes.userObject;
    if(viewModel) {
        self.viewModel = viewModel;
    }
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    // style the time selection button
    self.timeSelectionButton.titleLabel.numberOfLines = 1;
    self.timeSelectionButton.imageHorizontalAlignment = UIControlContentHorizontalAlignmentRight;
    
    // set the deceleration rates for our collection views
    self.timeCollectionView.decelerationRate = UIScrollViewDecelerationRateFast;
    self.selectionCollectionView.decelerationRate = UIScrollViewDecelerationRateFast;
    
    [self performSelector:@selector(reloadCollectionView) withObject:nil afterDelay:0.1f];
}

# pragma mark - Reactions

- (void)registerReactions:(EHISingleTimeCalendarViewModel *)model
{
    [super registerReactions:model];

    model.bind.map(@{
        source(model.title)                : dest(self, .title),
        source(model.selectionButtonTitle) : dest(self, .timeSelectionButton.ehi_title),
    });
}

- (void)reloadCollectionView
{
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
    UIView.animate(YES).duration(0.3).damping(0.8).options(options).transform(^{
        [self.selectionCollectionView scrollToItemAtIndexPath:self.viewModel.initialIndexPath atScrollPosition:UICollectionViewScrollPositionCenteredVertically animated:NO];
    }).start(^(BOOL finished) {
        [self didFinishScrolling];
    });
}

# pragma mark - Actions

- (IBAction)didTapSelectButton:(UIButton *)button
{
    // tell our view model which time to select
    [self.viewModel selectTimeAtIndexPath:self.indexPathForSelectableTime];
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
        self.timeSelectionButton.enabled = isScrolling ? NO : self.viewModel.currentTimeIsSelectable;
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

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenSingleTimeSelect;
}

@end
