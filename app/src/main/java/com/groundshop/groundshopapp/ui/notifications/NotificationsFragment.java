package com.groundshop.groundshopapp.ui.notifications;

import android.app.Activity;
import android.os.Bundle;
import android.view.Display;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.groundshop.groundshopapp.databinding.FragmentNotificationsBinding;
import com.groundshop.groundshopapp.ui.parser.Order;
import com.groundshop.groundshopapp.R;
import com.groundshop.groundshopapp.ui.orderitem.OrderItemView;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private NotificationsViewModel notificationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);
        Activity activity = requireActivity();
        notificationsViewModel.setActivity(activity);
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        LinearLayout containerLayout = root.findViewById(R.id.container);
        TextView noOrders = containerLayout.findViewById(R.id.noOrderText);

        Display display = requireActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        int screenHeight = outMetrics.heightPixels-200;

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) noOrders.getLayoutParams();
        layoutParams.height = screenHeight;
        noOrders.setLayoutParams(layoutParams);

        notificationsViewModel.getOrders().observe(getViewLifecycleOwner(), orders -> {
            containerLayout.removeAllViews();
            if (orders != null && !orders.isEmpty()) {
                for (Order order : orders) {
                    OrderItemView orderItemView = new OrderItemView(requireContext());

                    orderItemView.setOrderName(order.getName());
                    orderItemView.setOrderPhone(order.getPhone());
                    orderItemView.setOrderDetails(order.getComment());

                    orderItemView.setOnDeleteClickListener(v -> {
                        containerLayout.removeView(orderItemView);
                        notificationsViewModel.removeOrder(order);
                    });
                    containerLayout.addView(orderItemView);
                }
            } else {
                containerLayout.addView(noOrders);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
