package com.groundshop.groundshopapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.groundshop.groundshopapp.databinding.FragmentHomeBinding;
import com.groundshop.groundshopapp.R;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    // Создание массива для названий товаров

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        GridLayout gridLayout = root.findViewById(R.id.gridLayout);

        int childCount = gridLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = gridLayout.getChildAt(i);

            TextView productNameView = childView.findViewById(R.id.name1);
            productNameView.setText(productNames[i % productCount]);

            TextView productPriceView = childView.findViewById(R.id.price1);
            productPriceView.setText(productPrices[i % productCount]);

            ImageView productPhotoView = childView.findViewById(R.id.photo1);
            int resId = getResources().getIdentifier("photo" + (i+1), "drawable", requireContext().getPackageName());
            productPhotoView.setImageResource(resId);
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private String[] productNames = {
            "Голубика", "Универсальный", "Кислый", "Нейтрал", "Компост", "Высокие Грядки"
    };

    private String[] productPrices = {
            "$10", "$20", "$30", "$40", "$50", "$60",
    };
    private int productCount = 6;
}
