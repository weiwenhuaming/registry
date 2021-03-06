package com.github.fengdai.registry.sample.binder;

import android.widget.TextView;
import com.github.fengdai.registry.Layout;
import com.github.fengdai.registry.ViewBinder;
import com.github.fengdai.registry.sample.SampleList;
import com.github.fengdai.registry.sample.model.Card;

@SampleList
@Layout(android.R.layout.simple_list_item_1)
public class CardBinderTextOnly implements ViewBinder<Card, TextView> {
  @Override public void bind(Card item, TextView view) {
    view.setText(item.text);
  }
}
