package devnik.trancefestivalticker.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;

import org.greenrobot.greendao.query.Query;

import java.util.List;

import devnik.trancefestivalticker.App;
import devnik.trancefestivalticker.R;
import devnik.trancefestivalticker.model.DaoSession;
import devnik.trancefestivalticker.model.Festival;
import devnik.trancefestivalticker.model.FestivalDetail;
import devnik.trancefestivalticker.model.FestivalDetailDao;
import devnik.trancefestivalticker.model.FestivalDetailImages;
import devnik.trancefestivalticker.model.FestivalDetailImagesDao;

/**
 * Created by nik on 07.03.2018.
 */

public class FestivalDetailFragment extends DialogFragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {
    private Festival festival;
    private FestivalDetailDao festivalDetailDao;
    private Query<FestivalDetail> festivalDetailQuery;
    private FestivalDetail festivalDetail;

    private FestivalDetailImagesDao festivalDetailImagesDao;
    private Query<FestivalDetailImages> festivalDetailImagesQuery;
    private List<FestivalDetailImages> festivalDetailImages;

    private TextView lblCount, lblTitle, lblDate;
    private ImageSwitcher imageSwitcher;

    private SliderLayout imageSlider;
    public static FestivalDetailFragment newInstance() {
        FestivalDetailFragment f = new FestivalDetailFragment();
        return f;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_festival_detail, container, false);
        lblCount = (TextView) v.findViewById(R.id.lbl_header);
        lblTitle = (TextView) v.findViewById(R.id.title);
        lblDate = (TextView) v.findViewById(R.id.dateString);
        //imageSwitcher = (ImageSwitcher) v.findViewById(R.id.imgSwitch);
        imageSlider = (SliderLayout) v.findViewById(R.id.slider);

        festival = (Festival) getArguments().getSerializable("festival");
        DaoSession daoSession = ((App)getActivity().getApplication()).getDaoSession();
        festivalDetailDao = daoSession.getFestivalDetailDao();
        festivalDetailQuery = festivalDetailDao.queryBuilder().where(FestivalDetailDao.Properties.Festival_id.eq(festival.getFestival_id())).build();
        festivalDetail = festivalDetailQuery.unique();

        //Nur wenn das Festival eingetragende Details hat
        if(festivalDetail!=null) {
            festivalDetailImagesDao = daoSession.getFestivalDetailImagesDao();
            festivalDetailImagesQuery = festivalDetailImagesDao.queryBuilder().where(FestivalDetailImagesDao.Properties.FestivalDetailId.eq(festivalDetail.getFestival_detail_id())).build();
            festivalDetailImages = festivalDetailImagesQuery.list();

            initImageSlider();

            loadData();
        }
        return v;
    }
    public void loadData(){
        lblTitle.setText(festival.getName());
        lblDate.setText(DateFormat.format("dd.MM.yyyy", festival.getDatum_start())+ " - " + DateFormat.format("dd.MM.yyyy", festival.getDatum_end()));
    }
    public void initImageSlider(){
        for(FestivalDetailImages image : festivalDetailImages){
            TextSliderView textSliderView = new TextSliderView(getActivity());
            // initialize a SliderLayout
            textSliderView
                    .description(image.getTitle())
                    .image(image.getUrl())
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra",image.getTitle());

            imageSlider.addSlider(textSliderView);
        }
        imageSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        imageSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        imageSlider.setCustomAnimation(new DescriptionAnimation());
        imageSlider.setDuration(4000);
        imageSlider.addOnPageChangeListener(this);


    }
    public void initImageSwitcher(){
        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                return new ImageView(getActivity());
            }

        });
        // Set animations
        // https://danielme.com/2013/08/18/diseno-android-transiciones-entre-activities/
        //Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim);
        //Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        //imageSwitcher.setInAnimation(fadeIn);
        //imageSwitcher.setOutAnimation(fadeOut);
        Glide.with(getActivity())
                .load(festivalDetailImages.get(1).getUrl())
                .asBitmap()
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        //position++;
                        //if (position == gallery.length) {
                        //    position = 0;
                        //}
                        imageSwitcher.setImageDrawable(new BitmapDrawable(getResources(), resource));
                        return true;
                    }
                }).into((ImageView) imageSwitcher.getCurrentView());
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(android.support.v4.app.DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    //For ImageSlider
    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(getActivity(),slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}
}