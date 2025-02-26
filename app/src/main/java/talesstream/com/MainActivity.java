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

import androidx.recyclerview.widget.GridLayoutManager;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MenuItem homeBtn, searchBtn, exploreBtn, profileBtn;
    private String[] folders = {"Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh",
            "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jharkhand", "Karnataka", "Kerala",
            "Maharashtra", "Madhya Pradesh", "Manipur", "Meghalaya", "Mizoram", "Nagaland", "Odisha",
            "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura", "Uttarakhand",
            "Uttar Pradesh", "West Bengal"};
    private ViewPager2 viewPager2;
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

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout); // to refresh the page

        // Initialize Spinner
        spin = findViewById(R.id.spinner);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.view1);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));  // 2 columns in the grid

        // Set up Spinner
        String[] country = {"Select State..", "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh",
                "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jharkhand", "Karnataka", "Kerala",
                "Maharashtra", "Madhya Pradesh", "Manipur", "Meghalaya", "Mizoram", "Nagaland", "Odisha",
                "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura", "Uttarakhand",
                "Uttar Pradesh", "West Bengal"};

        ArrayAdapter<String> aa = new ArrayAdapter<>(this, R.layout.spinner_item, country);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);

        // Show progress bar while fetching videos
        ProgressBar progressBar = findViewById(R.id.progressBar);
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

    private void refreshData() {
        // Example: Simulate a network refresh or data update
        // Replace with your actual data refresh logic
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Stop the refresh animation once data is updated
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 2000); // Simulating a 2-second data refresh delay
    }

    // For slide show
    private void banners() {
        List<SliderItems> sliderItems = new ArrayList<>();
        sliderItems.add(new SliderItems(R.drawable.mizo));
        sliderItems.add(new SliderItems(R.drawable.naga));
        sliderItems.add(new SliderItems(R.drawable.assamese));
        sliderItems.add(new SliderItems(R.drawable.tamil));

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

    // Runnable to handle the auto swipe
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
        new Handler().postDelayed(() -> updateActiveButton(), 100);
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
