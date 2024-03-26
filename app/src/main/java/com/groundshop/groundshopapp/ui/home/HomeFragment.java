package com.groundshop.groundshopapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.groundshop.groundshopapp.databinding.FragmentHomeBinding;
import com.groundshop.groundshopapp.R;

import android.app.AlertDialog;
import android.widget.EditText;
import android.widget.Button;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

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

            final int index = i;
            productPriceView.setOnClickListener(v -> {
                String productName = productNames[index % productCount];
                String productPrice = productPrices[index % productCount];
                showProductDetailsDialog(productName, productPrice);
            });
        }

        return root;
    }

    private void showProductDetailsDialog(String productName, String oldPrice) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View customLayout = getLayoutInflater().inflate(R.layout.change_price_window, null);
        builder.setView(customLayout);

        TextView dialogTitle = customLayout.findViewById(R.id.changePriceTitle);
        EditText dialogInput = customLayout.findViewById(R.id.changePriceInput);
        Button closeButton = customLayout.findViewById(R.id.closeButton);
        Button setButton = customLayout.findViewById(R.id.setButton);

        dialogInput.setText(oldPrice);

        AlertDialog dialog = builder.create();
        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(R.drawable.change_price_background);
        }

        // TODO: POST Request
        setButton.setOnClickListener(v -> {

            dialog.dismiss();
        });

        closeButton.setOnClickListener(v -> {
            dialog.dismiss();
        });

    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private final String[] productNames = {
            "Голубика", "Универсальный", "Кислый", "Нейтрал", "Компост", "Высокие Грядки"
    };

    // TODO: DATABASE
    private final String[] productPrices = {
            "$10", "$20", "$30", "$40", "$50", "$60",
    };
    private final int productCount = 6;
}
