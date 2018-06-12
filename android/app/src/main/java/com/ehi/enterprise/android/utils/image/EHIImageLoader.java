package com.ehi.enterprise.android.utils.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.widget.ImageView;

import com.ehi.enterprise.android.models.location.EHIImage;
import com.ehi.enterprise.android.utils.BaseAppUtils;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.interfaces.ReactorComputationFunction;

public class EHIImageLoader {
    private static Context mContext;
    private String mUrl;
    private static EHIImageLoader sInstance;
    private List<EHIImage> mImages;
    @EHIImageUtils.ImageType private int mImageType;

    public static EHIImageLoader with(Context context) {
        mContext = context;
        sInstance = new EHIImageLoader();
        return sInstance;
    }

    public EHIImageLoader load(String url) {
        mUrl = url;
        return sInstance;
    }

    public EHIImageLoader loadTypeFromList(@EHIImageUtils.ImageType int imageType, List<EHIImage> images){
        mImages = images;
        mImageType = imageType;
        return sInstance;
    }

    public void into(ImageView imageView) {
        if(!TextUtils.isEmpty(mUrl)) {
            Picasso.with(mContext).load(mUrl).config(Bitmap.Config.RGB_565).into(imageView);
        }
        else if (mImages != null){
            final Pair<Integer, Integer> spec = BaseAppUtils.getDefaultCarImageMeasureSpec(mContext);
            imageView.measure(spec.first, spec.second);
            mUrl = EHIImageUtils.getCarClassImageUrl(mImages,
                                                     mImageType,
                                                     imageView.getMeasuredWidth());

            if(!EHITextUtils.isEmpty(mUrl)) {
                Picasso.with(mContext).load(mUrl).config(Bitmap.Config.RGB_565).into(imageView);
            }
        }
    }

    public static ReactorComputationFunction image(final ReactorVar<String> source,
                                                   final ImageView target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                EHIImageLoader.with(target.getContext())
                        .load(source.getValue())
                        .into(target);
            }
        };
    }

    public static ReactorComputationFunction imageByType(final ReactorVar<List<EHIImage>> source,
                                                         final ImageView target,
                                                         final @EHIImageUtils.ImageType int type){
        return new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if(target != null && source.getValue() != null) {
                    EHIImageLoader.with(target.getContext())
                                  .loadTypeFromList(type, source.getValue())
                                  .into(target);
                }
            }
        };
    }
}