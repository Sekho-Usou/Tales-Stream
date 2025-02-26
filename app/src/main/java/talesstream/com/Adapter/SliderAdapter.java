package talesstream.com.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import talesstream.com.Domain.SliderItems;
import talesstream.com.R;

import java.lang.ref.WeakReference;
import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SlideViewHolder> {
    private List<SliderItems> sliderItems;
    private WeakReference<ViewPager2> viewPagerRef;
    private Context context;

    // Constructor that accepts both ViewPager2 and slider items
    public SliderAdapter(Context context, ViewPager2 viewPager2, List<SliderItems> sliderItems) {
        this.context = context;
        this.viewPagerRef = new WeakReference<>(viewPager2);
        this.sliderItems = sliderItems;
    }

    @NonNull
    @Override
    public SlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.slide_item_container, parent, false);
        return new SlideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlideViewHolder holder, int position) {
        holder.setImageView(sliderItems.get(position));

        // Safe looping to prevent infinite addition of items
        if (position == sliderItems.size() - 2 && viewPagerRef.get() != null) {
            viewPagerRef.get().post(runnable);
        }
    }

    @Override
    public int getItemCount() {
        return sliderItems.size();
    }

    public class SlideViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;

        public SlideViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ImageSlide);
        }

        void setImageView(SliderItems sliderItem) {
            Glide.with(context)
                    .load(sliderItem.getImage())
                    .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(60)))
                    .into(imageView);
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int size = sliderItems.size();
            sliderItems.addAll(sliderItems); // Duplicate items
            notifyItemRangeInserted(size, sliderItems.size());
        }
    };
}
