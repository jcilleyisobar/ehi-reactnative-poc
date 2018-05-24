//
//  EHIWebBrowserViewController.m
//  Enterprise
//
//  Created by George Stuart on 8/11/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIWebBrowserViewController.h"
#import "EHIButton.h"
#import "EHIActivityIndicator.h"
#import "EHIBarButtonItem.h"
#import "EHINavigationController.h"
#import "EHILabel.h"
#import "EHIWebBrowserViewModel.h"

@interface EHIWebBrowserViewController () <UIWebViewDelegate>

@property (strong, nonatomic) EHIWebBrowserViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UINavigationBar *navigationBar;
@property (weak  , nonatomic) IBOutlet UIWebView *webView;
@property (weak  , nonatomic) IBOutlet UIBarButtonItem *backButton;
@property (weak  , nonatomic) IBOutlet UIBarButtonItem *forwardButton;
@property (weak  , nonatomic) IBOutlet EHIActivityIndicator *loadingIndicator;

@end

@implementation EHIWebBrowserViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
	
	[self.loadingIndicator setType:EHIActivityIndicatorTypeGreen size:self.loadingIndicator.bounds.size];
	
	self.webView.delegate = self;
	self.webView.scalesPageToFit = YES;
	self.webView.keyboardDisplayRequiresUserAction = YES;
    
    [EHINavigationController applyCustomStyleToNavigationBar:self.navigationBar];
    
    self.navigationItem.rightBarButtonItem = [EHIBarButtonItem buttonWithType:EHIButtonTypeClose target:self action:@selector(didTapCloseButton:)];
}

- (void)updateWithAttributes:(NAVAttributes *)attributes
{
    [super updateWithAttributes:attributes];
    
    if([attributes.userObject isKindOfClass:[EHIWebBrowserViewModel class]]) {
        EHIWebBrowserViewModel *viewModel = (EHIWebBrowserViewModel *)attributes.userObject;
        self.viewModel = viewModel;
    }
    else if([attributes.userObject isKindOfClass:[NSURL class]]) {
        self.viewModel = [[EHIWebBrowserViewModel new] initWithUrl:attributes.userObject body:nil];
    }
    else {
        self.viewModel = [EHIWebBrowserViewModel new];
    }
    
    self.viewModel.handler = attributes.handler;
}

- (void)registerReactions:(EHIWebBrowserViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:^(MTRComputation *computation) {
        NSURLRequest *request = model.request;
        [self.webView loadRequest:request];
        
        NSString *title = self.viewModel.title;
        [self updateTitle:title];
    }];
}

# pragma mark - Actions

- (IBAction)didTapCloseButton:(EHIButton *)sender
{
    [self.viewModel close];    
}

#pragma mark - UIWebViewDelegate methods

- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType
{
    // check if this is the redirect from 3DS and parse it if true
    if ([self.viewModel is3dsRequest:request]) {
        return NO;
    }
    
	return YES;
}

- (void)webViewDidStartLoad:(UIWebView *)webView
{
	[self.loadingIndicator startAnimating];
}

- (void)webViewDidFinishLoad:(UIWebView *)webView
{
    BOOL shouldUpdateTitle = self.viewModel.title == nil;
    if(shouldUpdateTitle) {
        NSString *title = [webView stringByEvaluatingJavaScriptFromString:@"document.title"];
        [self updateTitle:title];
    }
    
	self.backButton.enabled = webView.canGoBack;
	self.forwardButton.enabled = webView.canGoForward;
	[self.loadingIndicator stopAnimating];
}

- (void)webView:(UIWebView *)webView didFailLoadWithError:(NSError *)error
{
	[self.loadingIndicator stopAnimating];
}

- (UINavigationItem *)navigationItem
{
    return self.navigationBar.topItem;
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenWebBrowser;
}

//
// Helpers
//

- (void)updateTitle:(NSString *)title
{
    self.title = title;
    [(EHILabel *)self.navigationItem.titleView sizeToFit];
}

@end
