package com.groundshop.groundshopapp.ui.orderitem;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.groundshop.groundshopapp.R;

public class OrderItemView extends LinearLayout {
    private TextView orderDetailsTextView;
    private Button deleteButton;

    public OrderItemView(Context context) {
        super(context);
        init(context);
    }

    public OrderItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public OrderItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.order_item_view, this);

        orderDetailsTextView = findViewById(R.id.order_details_text_view);
        deleteButton = findViewById(R.id.delete_button);
    }

    public void setOrderDetails(String details) {
        orderDetailsTextView.setText(details);
    }

    public void setOnDeleteClickListener(View.OnClickListener listener) {
        deleteButton.setOnClickListener(listener);
    }
}
