//
//  EHICalendarViewController.m
//  Enterprise
//
//  Created by Ty Cobb on 3/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICalendarViewController.h"
#import "EHICalendarViewModel.h"
#import "EHICalendarHeaderView.h"
#import "EHICalendarDayCell.h"
#import "EHICalendarMonthCell.h"
#import "EHICalendarLayoutDelegate.h"
#import "EHIReservationSchedulePickerView.h"
#import "EHIButton.h"
#import "EHIActivityIndicator.h"

@interface EHICalendarViewController () <UICollectionViewDataSource, EHICalendarLayoutDelegate>
@property (strong, nonatomic) EHICalendarViewModel *viewModel;
@property (assign, nonatomic) BOOL isTransformingDepth;
@property (weak  , nonatomic) IBOutlet EHIReservationSchedulePickerView *scheduleView;
@property (weak  , nonatomic) IBOutlet UICollectionView *daysCollectionView;
@property (weak  , nonatomic) IBOutlet UICollectionView *monthsCollectionView;
@property (weak  , nonatomic) IBOutlet EHICalendarHeaderView *headerView;
@property (weak  , nonatomic) IBOutlet EHIButton *actionButton;
@property (weak  , nonatomic) IBOutlet EHIActivityIndicator *loadingIndicator;
/** the offset of the collection view that isn't overlapped by the header */
@property (nonatomic, readonly) CGPoint visibleOffset;
@end

@implementation EHICalendarViewController

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        self.viewModel = [EHICalendarViewModel new];
    }
    
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
   
    // style the schedule view
    self.scheduleView.hidesTime = YES;
    
    // configure the days collection view
    self.daysCollectionView.allowsMultipleSelection = YES;
    self.daysCollectionView.contentInset = (UIEdgeInsets){ .top = self.headerView.bounds.size.height };
    
    // fast deceleration allows for a paging effect
    self.daysCollectionView.decelerationRate = UIScrollViewDecelerationRateFast;
    
    // configure the months collection view
    self.monthsCollectionView.userInteractionEnabled = NO;
    self.monthsCollectionView.contentInset = self.daysCollectionView.contentInset;

    // update the initial header
    [self synchronizeActiveMonth];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];

    // determine the length of an individual day cell
    CGFloat length = roundf(self.view.bounds.size.width / EHIDaysPerWeek);
    
    // and update its metrics to be a square of that size
    [EHICalendarDayCell metrics].fixedSize = (CGSize){ .width = length, .height = length };
    // give the month cell a fixed height equal to the days height
    [EHICalendarMonthCell metrics].fixedSize = (CGSize){ .width = EHILayoutValueNil, .height = length };
}

# pragma mark - Accessibility

- (void)registerAccessibilityIdentifiers
{
    [super registerAccessibilityIdentifiers];
    
    self.daysCollectionView.accessibilityIdentifier = EHIItineraryCalendarViewKey;
    self.actionButton.accessibilityIdentifier = EHIReservationFlowContinueKey;
}

# pragma mark - Reactions

- (void)registerReactions:(EHICalendarViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateSelectedRange:)];
    
    model.bind.map(@{
        source(model.title) : dest(self, .title),
        source(model.actionTitle) : dest(self, .actionButton.ehi_title),
        source(model.actionIsDisabled) : ^(NSNumber *isDisabled) {
            self.actionButton.isFauxDisabled = isDisabled.boolValue;
        },
        source(model.firstDayForActiveMonth) : ^(EHICalendarDay *day) {
            [self.headerView.viewModel updateWithModel:day];
        },
        source(model.isLoading) : dest(self, .loadingIndicator.isAnimating),
    });
}

- (void)invalidateSelectedRange:(MTRComputation *)computation
{
    NSRange range = self.viewModel.selectedRange;
 
    // capture the paths to deselect for later
    NSArray *indexPathsToDeselect = (self.daysCollectionView.indexPathsForSelectedItems ?: @[]).select(^(NSIndexPath *indexPath) {
        return !NSRangeContains(range, indexPath.item);
    });
    
    // as long as our range is non-null, lets select some paths
    if(!NSRangeIsNull(range)) {
        // iterate over all the values in the range
        NSUInteger maximum = NSMaxRange(range);
        for(NSUInteger item=range.location ; item<=maximum ; item++) {
            NSIndexPath *indexPath = [NSIndexPath indexPathForItem:item inSection:0];
            
            // tell the day if it's a cap or not
            EHICalendarDayViewModel *day = [self dayModelAtIndexPath:indexPath];
            day.style = [self styleForDayAtIndexPath:indexPath inRange:range];
        
            // and if we need to select this path, then do so
            if(![self.daysCollectionView.indexPathsForSelectedItems containsObject:indexPath]) {
                [self.daysCollectionView selectItemAtIndexPath:indexPath animated:YES scrollPosition:UICollectionViewScrollPositionNone];
            }
        }
    }

    // deselect all the unselected paths after selection
    for(NSIndexPath *indexPath in indexPathsToDeselect) {
        [self.daysCollectionView deselectItemAtIndexPath:indexPath animated:YES];
    }
}

//
// Helpers
//

- (EHICalendarDayStyle)styleForDayAtIndexPath:(NSIndexPath *)indexPath inRange:(NSRange)range
{
    EHICalendarDayStyle result = EHICalendarDayStyleNone;
   
    // if this is the only item in a complete range, make it the combo cell
    if(range.length == 0 && self.viewModel.selectedRangeIsComplete && range.location == indexPath.item) {
        result = EHICalendarDayStyleCombined;
    }
    // show the left cap for the first item, as long as it's not the end of the week
    else if(range.location == indexPath.item) {
        result = [self.viewModel dayAtIndexPath:indexPath].isLastInWeek
            ? EHICalendarDayStyleSolo : EHICalendarDayStyleLeft;
    }
    // show the right cap for the first item, as long as it's not the start of a week
    else if(NSMaxRange(range) == indexPath.item) {
        result = [self.viewModel dayAtIndexPath:indexPath].isFirstInWeek
            ? EHICalendarDayStyleSolo : EHICalendarDayStyleRight;
    }
   
    return result;
}

# pragma mark - Months

- (void)synchronizeActiveMonth
{
    [self.viewModel updateActiveMonthWithIndexPath:[self indexPathForActiveMonth]];
}

- (NSIndexPath *)indexPathForActiveMonth
{
    NSArray *indexPaths = self.monthsCollectionView.indexPathsForVisibleItems;
    
    // sort the paths and then find the first one that is "visible"
    NSIndexPath *result = indexPaths.sort.find(^(NSIndexPath *visiblePath) {
        // the point tolerance to subtract from the content offset when checking if the month is in front of it
        const CGFloat tolerance = 10.0f;
        // get the frame of the month
        CGRect frame = [self.monthsCollectionView cellForItemAtIndexPath:visiblePath].frame;
        // if this month is below the visible offset (within our tolerance), then it's the one we want
        return frame.origin.y >= self.monthsCollectionView.contentOffset.y - tolerance;
    });
    
    // by default just use the first month
    return result ?: [NSIndexPath indexPathForItem:0 inSection:0];
}

# pragma mark - Interface Actions

- (IBAction)didTapTransitionButton:(EHIButton *)button
{
    if(button.isFauxDisabled) {
        [self.viewModel showToast];
    } else {
        [self.viewModel initiateTransition];
    }
}

# pragma mark - UICollectionViewDataSource

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section
{
    return collectionView == self.daysCollectionView ? self.viewModel.numberOfDays : self.viewModel.numberOfMonths;
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    if(collectionView == self.daysCollectionView) {
        return [self collectionView:collectionView dayCellAtIndexPath:indexPath];
    } else {
        return [self collectionView:collectionView monthCellAtIndexPath:indexPath];
    }
}

//
// Helpers
//

- (EHICalendarDayCell *)collectionView:(UICollectionView *)collectionView dayCellAtIndexPath:(NSIndexPath *)indexPath
{
    // get the date for this cell
    EHICalendarDay *day = [self.viewModel dayAtIndexPath:indexPath];
   
    EHICalendarDayCell *cell =
        [collectionView ehi_dequeueReusableCellWithClass:[EHICalendarDayCell class] model:day atIndexPath:indexPath];
    // tell the cell where it lies in the selection, if anywhere
    cell.viewModel.style = [self styleForDayAtIndexPath:indexPath inRange:self.viewModel.selectedRange];
    
    return cell;
}

- (EHICalendarMonthCell *)collectionView:(UICollectionView *)collectionView monthCellAtIndexPath:(NSIndexPath *)indexPath
{
    // get the day that begins this month
    EHICalendarDay *day = [self.viewModel firstDayInMonthAtIndexPath:indexPath];
    
    EHICalendarMonthCell *cell =
        [collectionView ehi_dequeueReusableCellWithClass:[EHICalendarMonthCell class] model:day atIndexPath:indexPath];
    // configure the header's visibility
    [cell setIsVisible:self.daysCollectionView.isTracking animated:NO];
    
    return cell;
}

# pragma mark - UICollectionViewDelegate

- (BOOL)collectionView:(UICollectionView *)collectionView shouldSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    // short-circuit collection view's normal selection process
    [self.viewModel selectDateAtIndexPath:indexPath];
    return NO;
}

- (BOOL)collectionView:(UICollectionView *)collectionView shouldDeselectItemAtIndexPath:(NSIndexPath *)indexPath
{
    [self.viewModel selectDateAtIndexPath:indexPath];
    return NO;
}

# pragma mark - UICollectionViewDelegateFlowLayout

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)layout sizeForItemAtIndexPath:(NSIndexPath *)indexPath
{
    // grab the right cell class
    Class<EHIListCell> klass = collectionView == self.daysCollectionView ?
        [EHICalendarDayCell class] : [EHICalendarMonthCell class];
    // and return the size in our container
    return [klass sizeForContainerSize:collectionView.bounds.size];
}

- (UIEdgeInsets)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)layout insetForSectionAtIndex:(NSInteger)section
{
    return collectionView == self.monthsCollectionView ? UIEdgeInsetsZero : [self calendarInsets];
}

- (CGFloat)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout minimumLineSpacingForSectionAtIndex:(NSInteger)section
{
    return 1.0f;
}

//
// Helpers
//

- (UIEdgeInsets)calendarInsets
{
    // calculate the amount of whitespace we have after laying out 7 days wide
    CGSize  itemSize = [EHICalendarDayCell sizeForContainerSize:self.daysCollectionView.bounds.size];
    CGFloat padding  = (self.daysCollectionView.bounds.size.width - itemSize.width * EHIDaysPerWeek) / 2.0f;
   
    // return the padding applied to the right and left
    return (UIEdgeInsets){ .left = padding, .right = padding };
}

# pragma mark - EHIReservationCalendarMonthsLayoutDelegate

- (NSInteger)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)layout monthRowForMonthIndexPath:(NSIndexPath *)indexPath
{
    // caluclate week by dividing the day index by the number of weekdays
    EHICalendarDay *day = [self.viewModel firstDayInMonthAtIndexPath:indexPath];
    return [self.viewModel rowIndexForDate:day.date];
}

- (NSInteger)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)layout monthRowForDayIndexPath:(NSIndexPath *)indexPath
{
    EHICalendarDay *day = [self.viewModel dayAtIndexPath:indexPath];
    return [self.viewModel rowIndexForDate:[day.date ehi_firstInMonth]];
}

- (CGFloat)collectionView:(UICollectionView *)collectionView offsetForStickyHeaderOfLayout:(UICollectionViewLayout *)layout
{
    EHICalendarMonthCell *monthCell = collectionView.visibleCells.firstObject;
    // calulate the delta between the header view's label and the cell's label
    return self.headerView.titlePosition - monthCell.titlePosition;
}

- (CGFloat)collectionView:(UICollectionView *)collectionView containerHeightForStickyHeaderOfLayout:(UICollectionViewLayout *)layout
{
    return self.headerView.containerHeight;
}

# pragma mark - UIScrollView

- (void)scrollViewWillBeginDragging:(UICollectionView *)scrollView
{
    [self didUpdateCalendarTrackingStateToValue:YES];
}

- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    if(scrollView == self.daysCollectionView) {
        // synchronize the collection views
        [self.monthsCollectionView setContentOffset:scrollView.contentOffset];
        [self.monthsCollectionView.collectionViewLayout invalidateLayout];
        // check the scrolling velocity
        [self invalidateVelocityForScrollView:self.daysCollectionView];
    }
}

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate
{
    // if we're not going to decelerate we need to finish now
    if(!decelerate) {
        [self didUpdateCalendarTrackingStateToValue:NO];
    }
}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView
{
    [self didUpdateCalendarTrackingStateToValue:NO];
}

//
// Helpers
//

- (void)didUpdateCalendarTrackingStateToValue:(BOOL)isTracking
{
    if(!isTracking) {
        // update the active header when we stop tracking
        [self synchronizeActiveMonth];
    }
   
    // update visibility of fixed ui elements based on tracking state
    [self minimizeVisualImportanceOfDays:isTracking animated:YES];
    [self.headerView setMonthIsVisible:!isTracking animated:YES];
   
    // update visibilty of the month headers
    for(EHICalendarMonthCell *monthCell in self.monthsCollectionView.visibleCells) {
        [monthCell setIsVisible:isTracking animated:YES];
    }   
}

- (void)minimizeVisualImportanceOfDays:(BOOL)shouldMinimize animated:(BOOL)animated
{
    UIView.animate(animated).duration(0.25).option(UIViewAnimationOptionAllowUserInteraction).transform(^{
        self.daysCollectionView.alpha = shouldMinimize ? 0.4f : 1.0f;
        // make sure we've turnned off the depth transform
        if(!shouldMinimize) {
            self.isTransformingDepth = NO;
        }
    }).start(nil);
}

# pragma mark - Velocity

- (void)invalidateVelocityForScrollView:(UIScrollView *)scrollView
{
    CGFloat velocity = fabs([scrollView.panGestureRecognizer velocityInView:scrollView].y);
    
    // set the depth transform, if necessary; check velocity against arbitrary threshold
    if(velocity > 2000.0f) {
        [self setIsTransformingDepth:YES animated:YES];
    }
}

- (void)setIsTransformingDepth:(BOOL)isTransformingDepth
{
    [self setIsTransformingDepth:isTransformingDepth animated:NO];
}

- (void)setIsTransformingDepth:(BOOL)isTransformingDepth animated:(BOOL)animated
{
    if(_isTransformingDepth == isTransformingDepth) {
        return;
    }
   
    _isTransformingDepth = isTransformingDepth;
    
    CATransform3D transform = CATransform3DIdentity;
    transform.m34 = -1.0f / 1000.0f;
    
    if(isTransformingDepth) {
        transform = CATransform3DTranslate(transform, 0.0f, 0.0f, -20.0f);
    }

    UIView.animate(animated).duration(0.25).option(UIViewAnimationOptionAllowUserInteraction).transform(^{
        self.daysCollectionView.layer.transform = transform;
    }).start(nil);
}

# pragma mark - Accessors

- (EHICalendarDayViewModel *)dayModelAtIndexPath:(NSIndexPath *)indexPath
{
    EHICalendarDayCell *cell =
        (EHICalendarDayCell *)[self.daysCollectionView cellForItemAtIndexPath:indexPath];
    return cell.viewModel;
}

# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenReservationCalendar state:EHIScreenReservationCalendar];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}


# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenReservationCalendar;
}

@end
