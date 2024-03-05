package com.groundshop.groundshopapp.ui.notifications;

import android.os.Bundle;
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



import java.util.List;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private NotificationsViewModel notificationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textNotifications;
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // Получение контейнера LinearLayout из макета
        LinearLayout containerLayout = root.findViewById(R.id.container);

        notificationsViewModel.getOrders().observe(getViewLifecycleOwner(), orders -> {
            if (orders != null && !orders.isEmpty()) {
                containerLayout.removeAllViews();

                for (Order order : orders) {
                    OrderItemView orderItemView = new OrderItemView(requireContext());
                    String orderDetails = "Order ID: " + order.getId() + "\n" +
                            "Name: " + order.getName() + "\n" +
                            "Phone: " + order.getPhone() + "\n";
                    orderItemView.setOrderDetails(orderDetails);
                    orderItemView.setOnDeleteClickListener(v -> {
                        // Обработка нажатия на кнопку удаления заказа
                        containerLayout.removeView(orderItemView);
                    });
                    containerLayout.addView(orderItemView);
                }
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
