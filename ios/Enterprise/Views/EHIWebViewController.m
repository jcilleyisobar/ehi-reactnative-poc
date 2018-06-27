//
//  EHIWebViewController.m
//  Enterprise
//
//  Created by Alex Koller on 6/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIWebViewController.h"
#import "EHIWebViewModel.h"
#import "EHIActivityIndicator.h"
#import "EHIRestorableConstraint.h"
#import "EHIButton.h"
#import "EHILabel.h"

NS_ASSUME_NONNULL_BEGIN

@interface EHIWebViewController () <UIWebViewDelegate>
@property (strong, nonatomic) EHIWebViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIWebView *webView;
@property (weak  , nonatomic) IBOutlet EHIActivityIndicator *loadingIndicator;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *navigationViewHeightConstraint;
@property (weak  , nonatomic) IBOutlet EHIButton *closeButton;
@property (weak  , nonatomic) IBOutlet EHILabel *titleLabel;
@end

@implementation EHIWebViewController

# pragma mark - View lifecycle

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.closeButton.type = EHIButtonTypeClose;
    self.webView.delegate = self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIWebViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.title)                   : ^(NSString *title){
                                                    self.title           = title;
                                                    self.titleLabel.text = title;
                                                },
        source(model.title)                   : dest(self, .titleLabel.text),
        source(model.isLoading)               : dest(self, .loadingIndicator.isAnimating),
        source(model.hideModalNavigationBar)  : dest(self, .navigationViewHeightConstraint.isDisabled),
        source(model.htmlString)              : ^(NSString *htmlString) {
                                                    [self.webView loadHTMLString:htmlString baseURL:nil];
                                                },
    });
}

# pragma mark - Actions

- (IBAction)didTapDismiss:(id)sender
{
    [self.viewModel dismiss];
}

- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType
{
    NSString *link = request.URL.absoluteString;
    BOOL isLink    = UIWebViewNavigationTypeLinkClicked == navigationType;
    BOOL canHandle = self.viewModel.canHandleAnchorLinks;
    BOOL isAnchor  = [self.viewModel isAnchorLink:link];
    if(!canHandle && isLink && isAnchor) {
        NSString *script = [self.viewModel javascriptScrollCommandForLink:link];
        if(script) {
            [webView stringByEvaluatingJavaScriptFromString:script];
        }
    }
    
    return YES;
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenWebView;
}

@end

NS_ASSUME_NONNULL_END
