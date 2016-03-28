package com.github.fengdai.registry.sample;

import com.github.fengdai.registry.ViewBinder;
import com.github.fengdai.registry.internal.Model;
import com.github.fengdai.registry.internal.RegistryImpl;
import com.github.fengdai.registry.sample.binder.BarIconAndText;
import com.github.fengdai.registry.sample.binder.BarTextOnly;
import com.github.fengdai.registry.sample.binder.FooBinder;
import com.github.fengdai.registry.sample.model.Bar;
import com.github.fengdai.registry.sample.model.Foo;
import java.util.LinkedHashMap;
import java.util.Map;

public class TextList$$Registry extends RegistryImpl {
  public TextList$$Registry() {
    super(createModels(), 3);
  }

  private static Map<Class<?>, Model<?>> createModels() {
    Map<Class<?>, Model<?>> map = new LinkedHashMap<>();
    map.put(Foo.class, com_github_fengdai_registry_internal_model_Foo());
    map.put(Bar.class, com_github_fengdai_registry_internal_model_Bar());
    return map;
  }

  private static Model<Foo> com_github_fengdai_registry_internal_model_Foo() {
    return Model.oneToOne(Foo.class)
        .add(0, FooBinder.class, android.R.layout.simple_list_item_1)
        .build();
  }

  private static Model<Bar> com_github_fengdai_registry_internal_model_Bar() {
    Model.ToManyBuilder<Bar, Class<? extends ViewBinder<Bar, ?>>> builder =
        Model.oneToMany(Bar.class, TextList.BarMapper.class);
    builder.add(BarIconAndText.class, 1, BarIconAndText.class, android.R.layout.activity_list_item);
    builder.add(BarIconAndText.class, 2, BarTextOnly.class, android.R.layout.simple_list_item_1);
    return builder.build();
  }
}
