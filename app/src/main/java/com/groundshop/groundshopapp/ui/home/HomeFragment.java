package com.groundshop.groundshopapp.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import com.groundshop.groundshopapp.ui.database.ProductDB;

import android.app.AlertDialog;
import android.widget.EditText;
import android.widget.Button;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ProductDB dataBase;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        GridLayout gridLayout = root.findViewById(R.id.gridLayout);

        Context context = getContext();
        dataBase = new ProductDB(context);
        int childCount = gridLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = gridLayout.getChildAt(i);
            final int index = i+1;

            String productName = dataBase.getProductNameById(index);
            String productPrice = dataBase.getProductPriceById(index);

            TextView productNameView = childView.findViewById(R.id.name1);
            productNameView.setText(productName);

            TextView productPriceView = childView.findViewById(R.id.price1);
            productPriceView.setText(productPrice);

            String photoName;
            if(index>6){ 
                photoName = "photo_250";
            }else{ photoName = "photo" + (index); }

            ImageView productPhotoView = childView.findViewById(R.id.photo1);
            int resId = getResources().getIdentifier(photoName, "drawable", requireContext().getPackageName());
            productPhotoView.setImageResource(resId);

            productPriceView.setOnClickListener(v -> {
                showProductDetailsDialog(index, productName, dataBase.getProductPriceById(index), productPriceView);
            });
        }

        return root;
    }

    private void showProductDetailsDialog(int index, String productName, String oldPrice, TextView productPriceView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View customLayout = getLayoutInflater().inflate(R.layout.change_price_window, null);
        builder.setView(customLayout);

        TextView dialogTitle = customLayout.findViewById(R.id.changePriceTitle);
        EditText dialogInput = customLayout.findViewById(R.id.changePriceInput);
        Button closeButton = customLayout.findViewById(R.id.closeButton);
        Button setButton = customLayout.findViewById(R.id.setButton);

        String volume = dataBase.getProductVolumeById(index);

        dialogInput.setText(oldPrice);
        dialogTitle.setText(productName+" ("+volume+")");

        AlertDialog dialog = builder.create();

        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(R.drawable.change_price_background);
        }

        // TODO: POST Request
        setButton.setOnClickListener(v -> {
            String newPrice = dialogInput.getText().toString();

            if (!newPrice.isEmpty()) {
                dataBase.updateProductPrice(index, newPrice);
                Log.d("SetButton", "Price updated for product ID: " + index + ", New Price: " + newPrice);
                productPriceView.setText(newPrice);
            } else {
                Log.d("SetButton", "New price is empty");
            }

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
}
