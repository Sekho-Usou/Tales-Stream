package talesstream.com;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import talesstream.com.Adapter.SliderAdapter;
import talesstream.com.Domain.SliderItems;
import talesstream.com.Fragments.ExploreFragment;
import talesstream.com.Fragments.MainFragment;
import talesstream.com.Fragments.ProfileFragment;
import talesstream.com.Fragments.SearchFragment;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MenuItem homeBtn, searchBtn, exploreBtn, profileBtn;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private String[] folders = {"Nagaland"};
    private ViewPager2 viewPager2;
    private ProgressBar progressBar;
    private Handler slideHandler = new Handler();
    private BottomNavigationView bottomNavigationView;
    private Spinner spin;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // for status bar color
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.black));

        // Initialize Spinner
        spin = findViewById(R.id.spinner);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.view1);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));  // 2 columns in the grid

        // Set up Spinner
        String[] tribe = {"Select Tribe..", "Ao", "Angami", "Chakhesang", "Chang",
                "Khiamniungan", "Konyak", "Lotha", "Phom", "Pochury", "Rengma",
                "Sangtam", "Sumi", "Yimkhiung", "Zeliang", "Tikhir", "Makury"};

        ArrayAdapter<String> aa = new ArrayAdapter<>(this, R.layout.spinner_item, tribe);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);

        // Show progress bar while fetching videos
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        initView();
        banners();

        // Initialize BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            if (item.getItemId() == R.id.home_btn) {
                selectedFragment = new MainFragment();
            } else if (item.getItemId() == R.id.search_btn) {
                selectedFragment = new SearchFragment();
            } else if (item.getItemId() == R.id.explore_btn) {
                selectedFragment = new ExploreFragment();
            } else if (item.getItemId() == R.id.profile_btn) {
                selectedFragment = new ProfileFragment();
            }
            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });

        // Load the default fragment (Home)
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.home_btn); // Default to Home
        }
    }

    private void banners() {
        List<SliderItems> sliderItems = new ArrayList<>();
        sliderItems.add(new SliderItems(R.drawable.sky_spirit));
        sliderItems.add(new SliderItems(R.drawable.the_goddess));
        sliderItems.add(new SliderItems(R.drawable.rice_beer));
        sliderItems.add(new SliderItems(R.drawable.jina_and_etiben));

        // Initialize ViewPager2 and SliderAdapter
        viewPager2 = findViewById(R.id.viewpagerSlider);
        SliderAdapter sliderAdapter = new SliderAdapter(this, viewPager2, sliderItems);

        viewPager2.setAdapter(sliderAdapter);
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(4);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(25));
        transformer.addTransformer((page, position) -> {
            float scale = 1 - Math.abs(position);
            page.setScaleY(0.85f + scale * 0.15f);
        });
        viewPager2.setPageTransformer(transformer);

        // Start auto swipe functionality
        slideHandler.postDelayed(slideRunnable, 3000);  // Start after 3 seconds
    }

    private Runnable slideRunnable = new Runnable() {
        @Override
        public void run() {
            int currentItem = viewPager2.getCurrentItem();
            int nextItem = currentItem + 1;
            if (nextItem >= viewPager2.getAdapter().getItemCount()) {
                nextItem = 0;  // Loop back to the first item
            }
            viewPager2.setCurrentItem(nextItem, true);  // Move to next item with animation
            slideHandler.postDelayed(this, 3000);  // Re-run the runnable after 3 seconds
        }
    };

    private void initView() {
        viewPager2 = findViewById(R.id.viewpagerSlider);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null) // Allows back navigation
                .commit();
    }

    private long backPressedTime = 0; // Time of the last back press
    private Toast backToast; // Toast message for back press prompt

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (currentFragment instanceof MainFragment) {
            if (backPressedTime + 2000 > System.currentTimeMillis()) {
                super.onBackPressed();
                finish(); // Close the app
            } else {
                backToast = Toast.makeText(getApplicationContext(), "Press back again to exit", Toast.LENGTH_SHORT);
                backToast.show();
                backPressedTime = System.currentTimeMillis();
            }
        } else {
            super.onBackPressed();
        }

        // Delay setting the selected item to ensure the fragment updates first
        new Handler().postDelayed(this::updateActiveButton, 100);
    }

    private void updateActiveButton() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (currentFragment instanceof MainFragment) {
            bottomNavigationView.setSelectedItemId(R.id.home_btn);
        } else if (currentFragment instanceof SearchFragment) {
            bottomNavigationView.setSelectedItemId(R.id.search_btn);
        } else if (currentFragment instanceof ExploreFragment) {
            bottomNavigationView.setSelectedItemId(R.id.explore_btn);
        } else if (currentFragment instanceof ProfileFragment) {
            bottomNavigationView.setSelectedItemId(R.id.profile_btn);
        }
    }
}