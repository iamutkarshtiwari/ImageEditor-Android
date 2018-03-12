package com.xinlan.imageeditlibrary.editimage.fragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.SaveCallback;
import com.xinlan.imageeditlibrary.BaseActivity;
import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;
import com.xinlan.imageeditlibrary.editimage.ModuleConfig;
import com.xinlan.imageeditlibrary.editimage.model.RatioItem;
import com.xinlan.imageeditlibrary.editimage.utils.Matrix3;
import com.xinlan.imageeditlibrary.editimage.view.imagezoom.ImageViewTouchBase;

import static com.isseiaoki.simplecropview.CropImageView.CropMode.FIT_IMAGE;
import static com.isseiaoki.simplecropview.CropImageView.CropMode.FREE;
import static com.isseiaoki.simplecropview.CropImageView.CropMode.RATIO_16_9;
import static com.isseiaoki.simplecropview.CropImageView.CropMode.RATIO_3_4;
import static com.isseiaoki.simplecropview.CropImageView.CropMode.RATIO_4_3;
import static com.isseiaoki.simplecropview.CropImageView.CropMode.RATIO_9_16;
import static com.isseiaoki.simplecropview.CropImageView.CropMode.SQUARE;


/**
 * 图片剪裁Fragment
 * 
 * @author panyi
 * 
 */
public class CropFragment extends BaseEditFragment {
    public static final int INDEX = ModuleConfig.INDEX_CROP;
	public static final String TAG = CropFragment.class.getName();
	private View mainView;
	private View backToMenu;// 返回主菜单
	private LinearLayout ratioList;
	public CropImageView mCropPanel;


	private enum RatioText {
		FREE("FREE"),
		FIT_IMAGE("FIT IMAGE"),
		SQUARE("1:1"),
		RATIO_3_4("3:4"),
		RATIO_4_3("4:3"),
		RATIO_9_16("9:16"),
		RATIO_16_9("16_9");

		private String ratioText;

		RatioText(String ratioText) {
			this.ratioText = ratioText;
		}

		public String getRatioText() {
			return ratioText;
		}
	}

	private CropImageView.CropMode getCropModeFromIndex(int index) {
		return CropImageView.CropMode.values()[index];
	}

	private List<TextView> textViewList = new ArrayList<TextView>();

	public static int SELECTED_COLOR = Color.YELLOW;
	public static int UNSELECTED_COLOR = Color.WHITE;
	private CropRationClick mCropRationClick = new CropRationClick();
	public TextView selctedTextView;

	public static CropFragment newInstance() {
		CropFragment fragment = new CropFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mainView = inflater.inflate(R.layout.fragment_edit_image_crop, null);
		return mainView;
	}

	private void setUpRatioList() {
		// init UI
		ratioList.removeAllViews();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_VERTICAL;
		params.leftMargin = 20;
		params.rightMargin = 20;
		RatioText[] ratioTextList = RatioText.values();
		for (int i = 0; i < ratioTextList.length; i++) {
			TextView text = new TextView(activity);
			text.setTextColor(UNSELECTED_COLOR);
			text.setTextSize(20);
			text.setText(ratioTextList[i].ratioText);
			textViewList.add(text);
			ratioList.addView(text, params);
			text.setTag(i);
			if (i == 0) {
				selctedTextView = text;
			}
			text.setTag(CropImageView.CropMode.values()[i]);
			text.setOnClickListener(mCropRationClick);
		}// end for i
		selctedTextView.setTextColor(SELECTED_COLOR);
	}

	/**
	 * 选择剪裁比率
	 * 
	 * @author
	 * 
	 */
	private final class CropRationClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			TextView curTextView = (TextView) v;
			selctedTextView.setTextColor(UNSELECTED_COLOR);
			CropImageView.CropMode cropMode = (CropImageView.CropMode) v.getTag();
			selctedTextView = curTextView;
			selctedTextView.setTextColor(SELECTED_COLOR);
			mCropPanel.setCropMode(cropMode);
			// System.out.println("dataItem   " + dataItem.getText());
		}
	}// end inner class

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

        backToMenu = mainView.findViewById(R.id.back_to_main);
        ratioList = (LinearLayout) mainView.findViewById(R.id.ratio_list_group);
        setUpRatioList();
        this.mCropPanel = ensureEditActivity().mCropPanel;
		backToMenu.setOnClickListener(new BackToMenuClick());// 返回主菜单
	}

    @Override
    public void onShow() {
        activity.mode = EditImageActivity.MODE_CROP;

        activity.mCropPanel.setVisibility(View.VISIBLE);
        activity.mainImage.setImageBitmap(activity.getMainBit());
        activity.mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        activity.mainImage.setScaleEnabled(false);// 禁用缩放
        //
        RectF r = activity.mainImage.getBitmapRect();
//        saveBitmap(activity.getMainBit(), activity.saveFilePath);
//        mCropPanel.load(Uri.parse(activity.saveFilePath));
        mCropPanel.setImageBitmap(activity.getMainBit());
        // System.out.println(r.left + "    " + r.top);
        activity.bannerFlipper.showNext();
    }

    /**
	 * 返回按钮逻辑
	 * 
	 * @author panyi
	 * 
	 */
	private final class BackToMenuClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			backToMain();
		}
	}// end class

	/**
	 * 返回主菜单
	 */
	@Override
	public void backToMain() {
		activity.mode = EditImageActivity.MODE_NONE;
		mCropPanel.setVisibility(View.GONE);
		activity.mainImage.setScaleEnabled(true);// 恢复缩放功能
		activity.bottomGallery.setCurrentItem(0);
		if (selctedTextView != null) {
			selctedTextView.setTextColor(UNSELECTED_COLOR);
		}
		activity.bannerFlipper.showPrevious();
	}

	/**
	 * 保存剪切图片
	 */
	public void applyCropImage() {
		// System.out.println("保存剪切图片");
		final Uri savedImageUri = getImageUri(getContext(), activity.getMainBit());

		mCropPanel.crop(savedImageUri)
				.execute(new CropCallback() {
					@Override public void onSuccess(Bitmap cropped) {
						activity.changeMainBitmap(cropped,true);
						backToMain();

					}

					@Override public void onError(Throwable e) {
						e.printStackTrace();
						backToMain();
						Toast.makeText(getContext(), "Error while saving image", Toast.LENGTH_SHORT).show();
					}
				});
	}

	public Uri getImageUri(Context inContext, Bitmap inImage) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
		return Uri.parse(path);
	}


	private void deleteImageFile(String savedPath) {
		File fdelete = new File(savedPath);
		fdelete.delete();
	}

	/**
	 * 图片剪裁生成 异步任务
	 * 
	 * @author panyi
	 * 
	 */
//	private final class CropImageTask extends AsyncTask<Bitmap, Void, Bitmap> {
//		private Dialog dialog;
//
//		@Override
//		protected void onCancelled() {
//			super.onCancelled();
//			dialog.dismiss();
//		}
//
//		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
//		@Override
//		protected void onCancelled(Bitmap result) {
//			super.onCancelled(result);
//			dialog.dismiss();
//		}
//
//		@Override
//		protected void onPreExecute() {
//			super.onPreExecute();
//			dialog = BaseActivity.getLoadingDialog(getActivity(), R.string.saving_image,
//					false);
//			dialog.show();
//		}
//
//		@SuppressWarnings("WrongThread")
//        @Override
//		protected Bitmap doInBackground(Bitmap... params) {
//			RectF cropRect = mCropPanel.getCropRect();// 剪切区域矩形
//			Matrix touchMatrix = activity.mainImage.getImageViewMatrix();
//			// Canvas canvas = new Canvas(resultBit);
//			float[] data = new float[9];
//			touchMatrix.getValues(data);// 底部图片变化记录矩阵原始数据
//			Matrix3 cal = new Matrix3(data);// 辅助矩阵计算类
//			Matrix3 inverseMatrix = cal.inverseMatrix();// 计算逆矩阵
//			Matrix m = new Matrix();
//			m.setValues(inverseMatrix.getValues());
//			m.mapRect(cropRect);// 变化剪切矩形
//
//			// Paint paint = new Paint();
//			// paint.setColor(Color.RED);
//			// paint.setStrokeWidth(10);
//			// canvas.drawRect(cropRect, paint);
//			// Bitmap resultBit = Bitmap.createBitmap(params[0]).copy(
//			// Bitmap.Config.ARGB_8888, true);
//			Bitmap resultBit = Bitmap.createBitmap(params[0],
//					(int) cropRect.left, (int) cropRect.top,
//					(int) cropRect.width(), (int) cropRect.height());
//
//			//saveBitmap(resultBit, activity.saveFilePath);
//			return resultBit;
//		}
//
//		@Override
//		protected void onPostExecute(Bitmap result) {
//			super.onPostExecute(result);
//			dialog.dismiss();
//			if (result == null)
//				return;
//
//            activity.changeMainBitmap(result,true);
//			activity.mCropPanel.setCropRect(activity.mainImage.getBitmapRect());
//			backToMain();
//		}
//	}// end inner class

	/**
	 * 保存Bitmap图片到指定文件
	 */
	public static void saveBitmap(Bitmap bm, String filePath) {
		File f = new File(filePath);
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println("保存文件--->" + f.getAbsolutePath());
	}
}// end class
