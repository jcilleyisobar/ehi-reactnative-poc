package com.ehi.enterprise.android.utils;

import android.support.annotation.AnimRes;
import android.support.annotation.IntDef;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.ehi.enterprise.android.ui.util.ProgressFragment;
import com.ehi.enterprise.android.ui.util.ProgressFragmentHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.interfaces.ReactorComputationFunction;

public class FragmentUtils {
    private static final String TAG = FragmentUtils.class.getSimpleName();
    private static boolean sProgressAdded = false;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ADD, REMOVE, REPLACE, SHOW, HIDE, ATTACH, DETACH})
    public @interface TransactionType {
    }

    public static final int ADD = 0;
    public static final int REMOVE = 1;
    public static final int REPLACE = 2;
    public static final int SHOW = 3;
    public static final int HIDE = 4;
    public static final int ATTACH = 5;
    public static final int DETACH = 6;

    public static final String PROGRESS_TAG = "ehi.ProgressFragment";

    /**
     * Use this class as a builder for a new fragment transaction
     * <p/>
     * <p/>
     * Example:
     * <pre>
     *         {@code
     * new FragmentUtils.Transaction(getSupportFragmentManager(), FragmentUtils.REPLACE)
     *                  .fragment(DriverInfoFragment.newInstance(price, driverInfo, edit))
     *                  .into(R.id.ac_single_fragment_container)
     *                  .addToBackStack(DriverInfoFragment.TAG)
     *                  .commit();
     *         }
     *     </pre>
     */
    public static class Transaction {
        private FragmentManager mFragmentManager;
        private Fragment mFragment;
        private int mContainerResId = android.R.id.content;
        private boolean mAddToBackStack = false;
        private String mAddToBackStackTag;
        private
        @TransactionType
        int mTransactionType;
        private
        @AnimRes
        int mEnterAnimation = -1;
        private
        @AnimRes
        int mExitAnimation = -1;
        private
        @AnimRes
        int mPopEnterAnimation = -1;
        private
        @AnimRes
        int mPopExitAnimation = -1;
        private String mFragmentTag;

        /**
         * Begin creating a new {@link com.ehi.enterprise.android.utils.FragmentUtils.Transaction}
         *
         * @param fragmentManager the {@link FragmentManager} to work with
         * @param transactionType the {@link com.ehi.enterprise.android.utils.FragmentUtils.TransactionType} of the operation to complete
         */
        public Transaction(FragmentManager fragmentManager, @TransactionType int transactionType) {
            mFragmentManager = fragmentManager;
            mTransactionType = transactionType;
        }

        /**
         * The {@link Fragment} to use
         *
         * @param fragment {@link Fragment} to use
         * @return this instance of the {@link com.ehi.enterprise.android.utils.FragmentUtils.Transaction} to continue building
         */
        public Transaction fragment(Fragment fragment) {
            mFragment = fragment;
            return this;
        }

        /**
         * The {@link Fragment} to use
         *
         * @param fragment    {@link Fragment} to use
         * @param fragmentTag {@link String} to tag the fragment with in the {@link FragmentManager}
         * @return this instance of the {@link com.ehi.enterprise.android.utils.FragmentUtils.Transaction} to continue building
         * @see {@link FragmentTransaction#add(Fragment, String) or {@link FragmentTransaction#replace(int, Fragment, String)}}
         */
        public Transaction fragment(Fragment fragment, String fragmentTag) {
            mFragment = fragment;
            mFragmentTag = fragmentTag;
            return this;
        }

        /**
         * The layout container to place the fragment in
         *
         * @param containerResId int resource ID of the container
         * @return this instance of the {@link com.ehi.enterprise.android.utils.FragmentUtils.Transaction} to continue building
         * @see {@link FragmentTransaction#replace(int, Fragment)} or {@link FragmentTransaction#add(Fragment, String)}
         */
        public Transaction into(int containerResId) {
            mContainerResId = containerResId;
            return this;
        }

        /**
         * Use custom enter and exit animations for the fragment
         *
         * @param enterAnimation {@link AnimRes} resource for the entrance animation
         * @param exitAnimation  {@link AnimRes} resource for the exit animation
         * @return this instance of the {@link com.ehi.enterprise.android.utils.FragmentUtils.Transaction} to continue building
         * @see {@link FragmentTransaction#setCustomAnimations(int, int)}
         */
        public Transaction withAnimations(@AnimRes int enterAnimation, @AnimRes int exitAnimation) {
            mEnterAnimation = enterAnimation;
            mExitAnimation = exitAnimation;
            return this;
        }

        /**
         * Use custom pop enter and exit animations for the fragment
         *
         * @param popEnterAnimation {@link AnimRes} resource for the pop entrance animation
         * @param popExitAnimation  {@link AnimRes} resource for the pop exit animation
         * @return this instance of the {@link com.ehi.enterprise.android.utils.FragmentUtils.Transaction} to continue building
         * @see {@link FragmentTransaction#setCustomAnimations(int, int, int, int)}
         */
        public Transaction withPopAnimations(@AnimRes int popEnterAnimation, @AnimRes int popExitAnimation) {
            mPopEnterAnimation = popEnterAnimation;
            mPopExitAnimation = popExitAnimation;
            return this;
        }

        /**
         * Add this transaction to the back stack
         *
         * @param backstackTag Tag to tag the {@link Fragment} with in the {@link FragmentManager}
         * @return this instance of the {@link com.ehi.enterprise.android.utils.FragmentUtils.Transaction} to continue building
         * @see {@link FragmentTransaction#addToBackStack(String)}
         */
        public Transaction addToBackStack(String backstackTag) {
            mAddToBackStack = true;
            mAddToBackStackTag = backstackTag;
            return this;
        }

        /**
         * Create a {@link FragmentTransaction} with the properties that have been set using the builder
         * This is not a required method, only useful if to further modify the {@link FragmentTransaction}
         * on the caller's end
         *
         * @return {@link FragmentTransaction}
         */
        public FragmentTransaction createTransaction() {
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

            switch (mTransactionType) {
                case ADD:
                    if (mEnterAnimation != -1 && mExitAnimation != -1 && mPopEnterAnimation != -1 && mPopExitAnimation != -1) {
                        fragmentTransaction.setCustomAnimations(mEnterAnimation, mExitAnimation, mPopEnterAnimation, mPopExitAnimation);
                    } else if (mEnterAnimation != -1 && mExitAnimation != -1 && mPopEnterAnimation == -1 && mPopExitAnimation == -1) {
                        fragmentTransaction.setCustomAnimations(mEnterAnimation, mExitAnimation);
                    }

                    if (mAddToBackStack) {
                        fragmentTransaction.addToBackStack(mAddToBackStackTag);
                    }

                    if (mContainerResId > 0) {
                        if (TextUtils.isEmpty(mFragmentTag)) {
                            fragmentTransaction.add(mContainerResId, mFragment);
                        } else {
                            fragmentTransaction.add(mContainerResId, mFragment, mFragmentTag);
                        }
                    } else {
                        fragmentTransaction.add(mFragment, mFragmentTag);
                    }
                    break;
                case REMOVE:
                    fragmentTransaction.remove(mFragment);
                    break;
                case REPLACE:
                    if (mAddToBackStack) {
                        fragmentTransaction.addToBackStack(mAddToBackStackTag);
                    }

                    if (mEnterAnimation != -1 && mExitAnimation != -1 && mPopEnterAnimation != -1 && mPopExitAnimation != -1) {
                        fragmentTransaction.setCustomAnimations(mEnterAnimation, mExitAnimation, mPopEnterAnimation, mPopExitAnimation);
                    } else if (mEnterAnimation != -1 && mExitAnimation != -1 && mPopEnterAnimation == -1 && mPopExitAnimation == -1) {
                        fragmentTransaction.setCustomAnimations(mEnterAnimation, mExitAnimation);
                    }

                    if (mContainerResId > 0) {
                        if (TextUtils.isEmpty(mFragmentTag)) {
                            fragmentTransaction.replace(mContainerResId, mFragment);
                        } else {
                            fragmentTransaction.replace(mContainerResId, mFragment, mFragmentTag);
                        }
                    }
                    break;
                case SHOW:
                    fragmentTransaction.show(mFragment);
                    break;
                case HIDE:
                    fragmentTransaction.hide(mFragment);
                    break;
                case ATTACH:
                    fragmentTransaction.attach(mFragment);
                    break;
                case DETACH:
                    fragmentTransaction.detach(mFragment);
                    break;

            }

            return fragmentTransaction;
        }

        /**
         * Commit the transaction to the {@link FragmentManager}, catch any exception that might occur
         */
        public void commit() {
            try {
                createTransaction().commit();
            } catch (Exception e) {
                DLog.e(TAG, "", e);
            }
        }
    }

    public static void addProgressFragment(FragmentActivity activity) {
        addProgressFragment(activity, false, false);
    }

    /**
     * @param activity
     * @param cancelable           False if you wish to not allow the user to back out
     * @param useDeterminateLoader False if you wish to use generic spinner
     */
    public static void addProgressFragment(FragmentActivity activity, boolean cancelable, boolean useDeterminateLoader) {
        if (activity != null) {
            Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag(PROGRESS_TAG);
            if ((fragment != null && fragment.isHidden()) || !sProgressAdded) {
                if (fragment == null) {
                    new Transaction(activity.getSupportFragmentManager(), ADD)
                            .into(android.R.id.content)
                            .fragment(new ProgressFragmentHelper.Builder()
                                    .isCancelable(cancelable)
                                    .isDeterminateLoader(useDeterminateLoader)
                                    .build(), PROGRESS_TAG)
                            .commit();

                } else {
                    ProgressFragment progressFragment = (ProgressFragment) fragment;
                    new Transaction(activity.getSupportFragmentManager(), SHOW)
                            .into(android.R.id.content)
                            .fragment(progressFragment, PROGRESS_TAG)
                            .commit();

                    if (useDeterminateLoader && !BaseAppUtils.isLowMemoryDevice(activity)) {
                        progressFragment.progressUsingDeterminateLoader();
                    } else {
                        progressFragment.progressUsingSpinnerLoader();
                    }
                }
                sProgressAdded = true;
            }
        }
    }

    public static void removeProgressFragment(FragmentActivity activity) {
        try {
            Fragment progress = activity.getSupportFragmentManager().findFragmentByTag(PROGRESS_TAG);
            if (progress != null) {
                new Transaction(activity.getSupportFragmentManager(), HIDE)
                        .fragment(progress)
                        .commit();
                sProgressAdded = false;
            }
        } catch (Exception e) {
            DLog.w(TAG, e);
        }

    }

    public static void clearBackStack(FragmentActivity fragmentActivity) {
        fragmentActivity.getSupportFragmentManager()
                .popBackStack(null, 0);
    }

    public static void clearBackStack(FragmentActivity fragmentActivity, String fragmentTag) {
        fragmentActivity.getSupportFragmentManager()
                .popBackStack(fragmentTag, 0);
    }

    public static void clearBackStackInclusive(FragmentActivity fragmentActivity) {
        fragmentActivity.getSupportFragmentManager()
                .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public static void clearBackStackInclusive(FragmentActivity fragmentActivity, String fragmentTag) {
        fragmentActivity.getSupportFragmentManager()
                .popBackStack(fragmentTag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public static ReactorComputationFunction progress(final ReactorVar<Boolean> source, final FragmentActivity target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (source.getValue()) {
                    FragmentUtils.addProgressFragment(target);
                } else {
                    FragmentUtils.removeProgressFragment(target);
                }
            }
        };
    }

    public static ReactorComputationFunction progressDefinite(final ReactorVar<Boolean> source, final FragmentActivity target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (source.getValue()) {
                    FragmentUtils.addProgressFragment(target, true, true);
                } else {
                    FragmentUtils.removeProgressFragment(target);
                }
            }
        };
    }

}
