//
//  EHICreditCardPanguiRequest.m
//  Enterprise
//
//  Created by Rafael Ramos on 9/28/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHICreditCardPanguiRequest.h"
#import "EHICreditCardType.h"

@interface EHICreditCardPanguiRequest ()
@property (strong, nonatomic) EHICreditCard *creditCard;
@property (strong, nonatomic) EHICreditCardSubmissionToken *token;
@end

@implementation EHICreditCardPanguiRequest

+ (instancetype)requestForCreditCard:(EHICreditCard *)creditCard token:(EHICreditCardSubmissionToken *)token
{
    EHICreditCardPanguiRequest *model = [EHICreditCardPanguiRequest new];
    model.creditCard = creditCard;
    model.token = token;
    
    return model;
}

# pragma mark - Network

- (void)encodeWithRequest:(EHINetworkRequest *)request
{
    [super encodeWithRequest:request];
    
    NSString *key  = self.token.cardSubmissionKey ?: @"";
    NSString *code = @(self.token.context.sourceSystemId).description;
    
    NSString *number     = self.creditCard.cardNumber ?: @"";
    NSString *holder     = self.creditCard.holderName ?: @"";
    NSString *cvv        = self.creditCard.cvvNumber ?: @"";
    NSString *expiration = [NSString stringWithFormat:@"%02d%ld", (int)self.creditCard.expirationMonth, (long)self.creditCard.expirationYear];
    
    request[@"SecurityCredential"] = @{
        @"ServiceAccountToken" : key ?: @""
    };
    
    request[@"SourceSystemCode"] = code;
    
    request[@"PaymentMedia"] = @{
        @"EncryptionKeyIdentifier"                      : @"",
        @"EncryptedPrimaryAccountNumber"                : number,
        @"CardHolderName"                               : holder,
        @"ExpirationMonthYearText"                      : expiration,
        @"EncryptedCardCustomerIdentificationNumber"    : cvv
    };

    request[@"Request"] = @{
        @"CallerIdentity"                   : @"eApp_iOs",
        @"CallingInterfaceVersion"          : @"2.7.0",
        @"CallingApplicationVersion"        : [NSBundle versionShort],
        @"CallingHostOrWeblogicInstance"    : @"eApp_Mobile_App_iOs",
        @"RequestId"                        : [NSUUID.UUID UUIDString],
        @"CallingProcess"                   : self.token.context.callingApplicationName ?: @"",
        @"CallingApplicationName"           : self.token.context.callingApplicationName ?: @""
    };

}

@end
