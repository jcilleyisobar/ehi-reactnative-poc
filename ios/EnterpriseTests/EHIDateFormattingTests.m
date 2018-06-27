//
//  EHIDateFormattingTests.m
//  Enterprise
//
//  Created by Rafael Ramos on 28/09/17.
//Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHITests.h"
#import "NSDate+MaskingTests.h"

NS_INLINE NSString * MaskDateUsingLocaleIdentifier(NSString *maskedDate, NSString *localeIdentifier)
{
    NSLocale *enUS   = [NSLocale localeWithLocaleIdentifier:localeIdentifier];
    return [NSDate ehi_localizedMaskedDate:maskedDate usingLocale:enUS];
}

SpecBegin(EHIDateFormattingTests)

describe(@"NSDate_Formatting", ^{
    NSString * const maskedDate   = @"••••-••-05";

    context(@"given nil", ^{
        it(@"should return nil", ^{
            expect([NSDate ehi_localizedMaskedDate:nil usingLocale:nil]).to.beNil();
            expect([NSDate ehi_localizedMaskedDate:nil usingLocale:NSLocale.autoupdatingCurrentLocale]).to.beNil();
        });
    });

    context(@"localize for locale", ^{
        it(@"en_US", ^{
            NSString *result = MaskDateUsingLocaleIdentifier(maskedDate, @"en_US_POSIX");
            expect(result).to.equal(@"••/05/••••");
        });

        it(@"fr_CA", ^{
            NSString *result = MaskDateUsingLocaleIdentifier(maskedDate, @"fr_CA");
            expect(result).to.equal(@"••••-••-05");
        });

        it(@"fr_FR", ^{
            NSString *result = MaskDateUsingLocaleIdentifier(maskedDate, @"fr_CA");
            expect(result).to.equal(@"••••-••-05");
        });

        it(@"fr", ^{
            NSString *result = MaskDateUsingLocaleIdentifier(maskedDate, @"fr");
            expect(result).to.equal(@"05/••/••••");
        });

        it(@"de_DE", ^{
            NSString *result = MaskDateUsingLocaleIdentifier(maskedDate, @"de_DE");
            expect(result).to.equal(@"05.••.••••");
        });

        it(@"de", ^{
            NSString *result = MaskDateUsingLocaleIdentifier(maskedDate, @"de");
            expect(result).to.equal(@"05.••.••••");
        });

        it(@"es_US", ^{
            NSString *result = MaskDateUsingLocaleIdentifier(maskedDate, @"es_US");
            expect(result).to.equal(@"••/05/••••");
        });

        it(@"es_ES", ^{
            NSString *result = MaskDateUsingLocaleIdentifier(maskedDate, @"es_ES");
            expect(result).to.equal(@"05/••/••••");
        });

        it(@"es", ^{
            NSString *result = MaskDateUsingLocaleIdentifier(maskedDate, @"es");
            expect(result).to.equal(@"05/••/••••");
        });

        it(@"en_GB", ^{
            NSString *result = MaskDateUsingLocaleIdentifier(maskedDate, @"en_GB");
            expect(result).to.equal(@"05/••/••••");
        });

        it(@"en", ^{
            NSString *result = MaskDateUsingLocaleIdentifier(maskedDate, @"en");
            expect(result).to.equal(@"••/05/••••");
        });

        it(@"en_CA", ^{
            NSString *result = MaskDateUsingLocaleIdentifier(maskedDate, @"en_CA");
            expect(result).to.equal(@"••••-••-05");
        });

        it(@"en_US", ^{
            NSString *result = MaskDateUsingLocaleIdentifier(maskedDate, @"en_US");
            expect(result).to.equal(@"••/05/••••");
        });

        it(@"en_US_POSIX", ^{
            NSString *result = MaskDateUsingLocaleIdentifier(maskedDate, @"en_US_POSIX");
            expect(result).to.equal(@"••/05/••••");
        });
    });
});

SpecEnd
