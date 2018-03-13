package com.xinlan.imageeditlibrary.editimage.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;
import com.xinlan.imageeditlibrary.editimage.ModuleConfig;
import com.xinlan.imageeditlibrary.editimage.utils.Utils;
import com.xinlan.imageeditlibrary.editimage.view.BrightnessView;
import com.xinlan.imageeditlibrary.editimage.view.imagezoom.ImageViewTouchBase;


public class BrightnessFragment extends BaseEditFragment {

    public static final int INDEX = ModuleConfig.INDEX_BRIGHTNESS;
    public static final String TAG = BrightnessFragment.class.getName();
    BrightnessView mBrightnessView;
    SeekBar mSeekBar;
    private View mainView;
    private View mBackToMenu;

    private boolean start = true;

    @SuppressLint("ValidFragment")
    private BrightnessFragment() {
    }

    public static BrightnessFragment newInstance() {
        BrightnessFragment fragment = new BrightnessFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_edit_image_brightness, null);
        mappingView(mainView);
        return mainView;
    }

    private void mappingView(View view) {
        mSeekBar = view.findViewById(R.id.seekBar);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mBackToMenu = mainView.findViewById(R.id.back_to_main);

        this.mBrightnessView = ensureEditActivity().mBrightnessView;
        mBackToMenu.setOnClickListener(new BrightnessFragment.BackToMenuClick());
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float value = progress - 1000;
                activity.mBrightnessView.setBright(value / 10f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        initView();
    }

    @Override
    public void onShow() {
        activity.mode = EditImageActivity.MODE_BRIGHTNESS;
        activity.mainImage.setImageBitmap(activity.getMainBit());
        activity.mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        activity.mainImage.setVisibility(View.GONE);

        activity.mBrightnessView.setImageBitmap(activity.getMainBit());
        activity.mBrightnessView.setVisibility(View.VISIBLE);
        initView();
        activity.bannerFlipper.showNext();
    }

    @Override
    public void backToMain() {
        activity.mode = EditImageActivity.MODE_NONE;
        activity.bottomGallery.setCurrentItem(0);
        activity.mainImage.setVisibility(View.VISIBLE);
        activity.mBrightnessView.setVisibility(View.GONE);
        activity.bannerFlipper.showPrevious();
    }

    public void applyBrightness() {
        if (mSeekBar.getProgress() == 1000) {// 没有做旋转
            backToMain();
            return;
        }
        Bitmap bitmap = ((BitmapDrawable) mBrightnessView.getDrawable()).getBitmap();
        activity.changeMainBitmap(Utils.brightBitmap(bitmap, mBrightnessView.getBright()), true);
        backToMain();
    }

    private void initView() {
        mSeekBar.setProgress(1000);
    }

    private void back() {
        getActivity().onBackPressed();
    }

    private final class BackToMenuClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            backToMain();
        }
    }//

}
